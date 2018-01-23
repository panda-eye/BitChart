package com.bitchartdev.bitchart

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutCompat
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bitchartdev.bitchart.Tasks.BitfinesTask
import com.bitchartdev.bitchart.Tasks.KrakenTask
import com.bitchartdev.bitchart.Tasks.TaskHelper
import com.bitchartdev.bitchart.Tasks.YoBitTask
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity.*
import java.lang.ref.WeakReference

class BitChartActivity : AppCompatActivity() {

    private lateinit var krakenMin: AppCompatTextView
    private lateinit var krakenMax: AppCompatTextView

    private lateinit var yoBitMin: AppCompatTextView
    private lateinit var yoBitMax: AppCompatTextView

    private lateinit var bitfinesMin: AppCompatTextView
    private lateinit var bitfinesMax: AppCompatTextView

    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var bottomLayout: LinearLayoutCompat

    private lateinit var lineChart: LineChart
    lateinit var forSnackbarView: LinearLayoutCompat
    private val ref = WeakReference(this)
    private var updated = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        val titleLayout = ttl as LinearLayoutCompat
        titleLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_title)
        titleLayout.findViewById<AppCompatTextView>(R.id.marketMin).setText(R.string.min_title)
        titleLayout.findViewById<AppCompatTextView>(R.id.marketMax).setText(R.string.max_title)

        val krakenLayout = kraken as LinearLayoutCompat
        krakenLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_kraken)
        krakenMin = krakenLayout.findViewById(R.id.marketMin)
        krakenMax = krakenLayout.findViewById(R.id.marketMax)

        val yoBitLayout = yobit as LinearLayoutCompat
        yoBitLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_yobit)
        yoBitMin = yoBitLayout.findViewById(R.id.marketMin)
        yoBitMax = yoBitLayout.findViewById(R.id.marketMax)

        bottomSheet = BottomSheetDialog(this@BitChartActivity)
        bottomLayout = LinearLayoutCompat(this@BitChartActivity)
        bottomLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        bottomSheet.setContentView(bottomLayout)

        val bitfinesLayout = bittrex as LinearLayoutCompat
        bitfinesLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_bitfines)
        bitfinesMin = bitfinesLayout.findViewById(R.id.marketMin)
        bitfinesMax = bitfinesLayout.findViewById(R.id.marketMax)

        setChart()
        forSnackbarView = for_snackbar

        TaskHelper.mapper = ObjectMapper().registerKotlinModule()
    }
    override fun onResume() {
        super.onResume()
        executeTasks()
    }

    fun readKraken(market: TaskHelper.Companion.Market) {
        krakenMin.text = market.min24h
        krakenMax.text = market.max24h
        addDataSet(market.parse(),"Kraken")
        updated()
    }

    fun readYoBit(market: TaskHelper.Companion.Market) {
        yoBitMax.text = market.max24h
        yoBitMin.text = market.min24h
        addDataSet(market.parse(), "YoBit")
        updated()
    }
    fun readBitfines(market: TaskHelper.Companion.Market) {
        bitfinesMax.text = market.max24h
        bitfinesMin.text = market.min24h
        addDataSet(market.parse(), "Bitfines")
        updated()
    }
    private fun updated() {
        if (++updated == 3) {
            updated = 0
            title = getString(R.string.app_name)
            calculate()
        }
    }

    private fun calculate() {
        val toHaveBenefit = ArrayList<String>()
        bottomLayout.removeAllViews()
        val names = arrayOf(getString(R.string.name_kraken), getString(R.string.name_yobit), getString(R.string.name_bitfines))
        names.forEach {
            val temp = names.filter { a -> a != it }
            temp.forEach { t ->
                val text = "$it ${getString(R.string.to)} $t"
                val right = 1.0 - when (t) {
                    names[0] -> krakenCommission
                    names[1] -> yoBitCommission
                    else -> bitfinexCommission
                }
                val left = 1.0 - when (it) {
                    names[0] -> krakenCommission
                    names[1] -> yoBitCommission
                    else -> bitfinexCommission
                }

                if (right > left) {
                    toHaveBenefit.add(text)
                }
            }
        }
        if (toHaveBenefit.size == 0) {
            bottomLayout.addView(AppCompatTextView(this@BitChartActivity).also { it.setText("No benefit"); it.setPadding(4,4,4,4) })
        } else {
            bottomLayout.addView(AppCompatTextView(this@BitChartActivity).also { it.setText("Benefits:"); it.setPadding(4,4,4,4) })
            toHaveBenefit.forEach {b ->
                bottomLayout.addView(AppCompatTextView(this@BitChartActivity).also { it.setText(b); it.setPadding(4,4,4,4) })
            }
        }
    }

    private fun executeTasks(showError: Boolean = false) {
        if (TaskHelper.hasInternet(this@BitChartActivity)) {
            lineChart.data.clearValues()
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
            resetText()
            title = getString(R.string.updating_title)
            KrakenTask().execute(ref)
            YoBitTask().execute(ref)
            BitfinesTask().execute(ref)
        } else if (showError) {
            TaskHelper.showTaskSnackbar(forSnackbarView, R.string.no_internet)
        }
    }
    private fun resetText() {
        val line = getString(R.string.no_data)
        krakenMax.text = line
        krakenMin.text = line
        yoBitMin.text = line
        yoBitMax.text = line
        bitfinesMin.text = line
        bitfinesMax.text = line
    }

    private fun setChart() {
        lineChart = findViewById(R.id.graph)
        lineChart.setDrawGridBackground(false)
        lineChart.description.isEnabled = false

        // add an empty data object
        lineChart.data = LineData()
//        mChart.getXAxis().setDrawLabels(false);
//        mChart.getXAxis().setDrawGridLines(false);

        lineChart.invalidate()

    }

    private var mColors = ColorTemplate.VORDIPLOM_COLORS

    private fun addDataSet(list: List<Float>, label: String) {

        val data = lineChart.data

        if (data != null) {

            val count = data.dataSetCount + 1
            val entryList = ArrayList<Entry>()
            (0 until list.size).mapTo(entryList) { Entry(it.toFloat(), list[it]) }

            val set = LineDataSet(entryList, label)
            set.lineWidth = 2.5f
            set.circleRadius = 4.5f

            val color = mColors[count % mColors.size]

            set.color = color
            set.setCircleColor(color)
            set.highLightColor = color
            set.valueTextSize = 10f
            set.valueTextColor = Color.BLACK

            data.addDataSet(set)

            addLegend()

            data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()


        }
    }

    private fun addLegend() {
        val l = lineChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textSize = 11f
        l.textColor = Color.BLACK
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
    }

//    private fun setData(listFirt: List<Float>, listSec: List<Float>, listThird: List<Float>) {
//        var set1: LineDataSet
//        var set2: LineDataSet
//        var set3: LineDataSet
//
//        val entryListFirst = ArrayList<Entry>()
//        val entryListSec = ArrayList<Entry>()
//        val entryListThird = ArrayList<Entry>()
//        (0 until listFirt.size).mapTo(entryListFirst) { Entry(it.toFloat(), listFirt[it]) }
//        (0 until listSec.size).mapTo(entryListSec) { Entry(it.toFloat(), listSec[it]) }
//        (0 until listThird.size).mapTo(entryListThird) { Entry(it.toFloat(), listThird[it]) }
//
//        if (lineChart.data != null && lineChart.getData().getDataSetCount() > 0) run {
//            set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
//            set2 = lineChart.getData().getDataSetByIndex(1) as LineDataSet
//            set3 = lineChart.getData().getDataSetByIndex(2) as LineDataSet
//            set1.values = entryListFirst
//            set2.values = entryListSec
//            set3.values = entryListThird
//            lineChart.data.notifyDataChanged()
//            lineChart.notifyDataSetChanged()
//        } else {
//            // create a dataset and give it a type
//            set1 = LineDataSet(entryListFirst, "DataSet 1")
//
//            set1.axisDependency = YAxis.AxisDependency.LEFT
//            set1.color = ColorTemplate.getHoloBlue()
//            set1.setCircleColor(Color.BLACK)
//            set1.lineWidth = 2f
//            set1.circleRadius = 3f
//            set1.fillAlpha = 65
//            set1.fillColor = ColorTemplate.getHoloBlue()
//            set1.highLightColor = Color.rgb(244, 117, 117)
//            set1.setDrawCircleHole(false)
//            //set1.setFillFormatter(new MyFillFormatter(0f));
//            //set1.setDrawHorizontalHighlightIndicator(false);
//            //set1.setVisible(false);
//            //set1.setCircleHoleColor(Color.WHITE);
//
//            // create a dataset and give it a type
//            set2 = LineDataSet(entryListSec, "DataSet 2")
//            set2.setAxisDependency(YAxis.AxisDependency.RIGHT)
//            set2.setColor(Color.RED)
//            set2.setCircleColor(Color.BLACK)
//            set2.setLineWidth(2f)
//            set2.setCircleRadius(3f)
//            set2.setFillAlpha(65)
//            set2.setFillColor(Color.RED)
//            set2.setDrawCircleHole(false)
//            set2.setHighLightColor(Color.rgb(244, 117, 117))
//            //set2.setFillFormatter(new MyFillFormatter(900f));
//
//            set3 = LineDataSet(entryListThird, "DataSet 3")
//            set3.setAxisDependency(YAxis.AxisDependency.RIGHT)
//            set3.setColor(Color.YELLOW)
//            set3.setCircleColor(Color.BLACK)
//            set3.setLineWidth(2f)
//            set3.setCircleRadius(3f)
//            set3.setFillAlpha(65)
//            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.GREEN, 200))
//            set3.setDrawCircleHole(false)
//            set3.setHighLightColor(Color.rgb(244, 117, 117))
//
//            // create a data object with the datasets
//            val data = LineData(set1, set2, set3)
//            data.setValueTextColor(Color.BLACK)
//            data.setValueTextSize(9f)
//
//            // set data
//            lineChart.setData(data)
//
//            val l = lineChart.getLegend()
//
//            // modify the legend ...
//            l.setForm(Legend.LegendForm.LINE)
//            l.setTextSize(11f)
//            l.setTextColor(Color.BLACK)
//            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
//            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
//            l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
//            l.setDrawInside(false)
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.update_item -> {
                executeTasks(true)
                true
            }
            R.id.sheet -> {
                bottomSheet.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private fun TaskHelper.Companion.Market.parse(): List<Float> {
            val ar = ArrayList<Float>()
            if (min24h != "") ar.add(min24h.toFloat())
            if (max24h != "") ar.add(max24h.toFloat())
            return ar
        }
        private val krakenCommission = 0.001
        private val bitfinexCommission = 0.0008
        private val yoBitCommission = 0.0005
    }
}
