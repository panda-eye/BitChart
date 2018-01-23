package com.bitchartdev.bitchart

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutCompat
import android.view.Menu
import android.view.MenuItem
import com.bitchartdev.bitchart.Tasks.KrakenTask
import com.bitchartdev.bitchart.Tasks.TaskHelper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity.*
import java.lang.ref.WeakReference

class BitChartActivity : AppCompatActivity() {

    private lateinit var krakenMin: AppCompatTextView
    private lateinit var krakenMax: AppCompatTextView

    private lateinit var yoBitMin: AppCompatTextView
    private lateinit var yoBitMax: AppCompatTextView

    private lateinit var bitfinesMin: AppCompatTextView
    private lateinit var bitfinesMax: AppCompatTextView

    private lateinit var lineChart: LineChart
    lateinit var forSnackbarView: LinearLayoutCompat
    private val ref = WeakReference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

//        val titleLayout = title as LinearLayoutCompat
//        titleLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_title)
//        titleLayout.findViewById<AppCompatTextView>(R.id.marketMin).setText(R.string.min_title)
//        titleLayout.findViewById<AppCompatTextView>(R.id.marketMax).setText(R.string.max_title)

        val krakenLayout = kraken as LinearLayoutCompat
        krakenLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_kraken)
        krakenMin = krakenLayout.findViewById(R.id.marketMin)
        krakenMax = krakenLayout.findViewById(R.id.marketMax)

        val yoBitLayout = yobit as LinearLayoutCompat
        yoBitLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_yobit)
        yoBitMin = yoBitLayout.findViewById(R.id.marketMin)
        yoBitMax = yoBitLayout.findViewById(R.id.marketMax)

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
//        val krakenSeries = List<float>(market.parse())
        krakenMin.text = market.min24h
        krakenMax.text = market.max24h
//        krakenSeries.setAnimated(true)
//        krakenSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.kraken_graph_line)
//        krakenSeries.title = getString(R.string.name_kraken)
        setData(market.parse())
    }
    fun readYoBit(market: TaskHelper.Companion.Market) {
//        val yoBitSeries = LineGraphSeries<Float>(market.parse())
//        yoBitSeries.setAnimated(true)
//        yoBitSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.yobit_graph_line)
//        yoBitSeries.title = getString(R.string.name_yobit)
//        graphLayout.addSeries(yoBitSeries)
        // TODO:
    }
    fun readBitfines(market: TaskHelper.Companion.Market) {
//        val bitfinesSeries = LineGraphSeries<Float>(market.parse())
//        bitfinesSeries.setAnimated(true)
//        bitfinesSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.bitfines_graph_line)
//        bitfinesSeries.title = getString(R.string.name_bitfines)
//        graphLayout.addSeries(bitfinesSeries)
        // TODO:
    }

    private fun executeTasks(showError: Boolean = false) {
        if (TaskHelper.hasInternet(this@BitChartActivity)) {
            KrakenTask().execute(ref)
            //YoBitTask().execute(ref)
            //BitfinesTask().execute(ref)

//            graphLayout.removeAllSeries()
        } else if (showError) {
            TaskHelper.showTaskSnackbar(forSnackbarView, R.string.no_internet)
        }
    }

    private fun setChart() {
        lineChart = findViewById(R.id.graph)
        lineChart.setDrawGridBackground(false)

        // no description text
        lineChart.description.isEnabled = false

        // enable touch gestures
        lineChart.setTouchEnabled(true)

        // enable scaling and dragging
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(true)
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true)

        // x-axis limit line
        val llXAxis = LimitLine(10f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f

        val xAxis = lineChart.getXAxis()
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        val leftAxis = lineChart.axisLeft

        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true)

        lineChart.axisRight.isEnabled = false

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);
//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        lineChart.animateX(2500)
        //mChart.invalidate();

    }

    private fun setData(list: List<Float>) {
        val set1: LineDataSet

        val entryList = ArrayList<Entry>()
        (0 until list.size).mapTo(entryList) { Entry(it.toFloat(), list[it]) }

        if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
            set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = entryList
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(entryList, "DataSet 1")

            set1.setDrawIcons(false)

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            val dataSets = java.util.ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the datasets

            // create a data object with the datasets
            val data = LineData(dataSets)

            // set data
            lineChart.data = data
        }
    }

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
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private fun TaskHelper.Companion.Market.parse(): List<Float> {
            val ar = ArrayList<Float>()
            if (min24h != "") ar.add(min24h.toFloat())
            //if (min != "") ar.add(p(min, 1.0))
            //if (max != "") ar.add(p(max, 2.0))
            if (max24h != "") ar.add(max24h.toFloat())
            return ar
        }
    }
}
