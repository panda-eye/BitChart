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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
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
        setData(market.parse(), arrayListOf(10000f,11000f, 12000f), arrayListOf(11200f, 11000f, 10800f))
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

    private fun setData(listFirt: List<Float>, listSec: List<Float>, listThird: List<Float>) {
        var set1: LineDataSet
        var set2: LineDataSet
        var set3: LineDataSet

        val entryListFirst = ArrayList<Entry>()
        val entryListSec = ArrayList<Entry>()
        val entryListThird = ArrayList<Entry>()
        (0 until listFirt.size).mapTo(entryListFirst) { Entry(it.toFloat(), listFirt[it]) }
        (0 until listSec.size).mapTo(entryListSec) { Entry(it.toFloat(), listSec[it]) }
        (0 until listThird.size).mapTo(entryListThird) { Entry(it.toFloat(), listThird[it]) }

        if (lineChart.data != null && lineChart.getData().getDataSetCount() > 0) run {
            set1 = lineChart.getData().getDataSetByIndex(0) as LineDataSet
            set2 = lineChart.getData().getDataSetByIndex(1) as LineDataSet
            set3 = lineChart.getData().getDataSetByIndex(2) as LineDataSet
            set1.values = entryListFirst
            set2.values = entryListSec
            set3.values = entryListThird
            lineChart.getData().notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(entryListFirst, "DataSet 1")

            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = ColorTemplate.getHoloBlue()
            set1.setCircleColor(Color.BLACK)
            set1.lineWidth = 2f
            set1.circleRadius = 3f
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue()
            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = LineDataSet(entryListSec, "DataSet 2")
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT)
            set2.setColor(Color.RED)
            set2.setCircleColor(Color.BLACK)
            set2.setLineWidth(2f)
            set2.setCircleRadius(3f)
            set2.setFillAlpha(65)
            set2.setFillColor(Color.RED)
            set2.setDrawCircleHole(false)
            set2.setHighLightColor(Color.rgb(244, 117, 117))
            //set2.setFillFormatter(new MyFillFormatter(900f));

            set3 = LineDataSet(entryListThird, "DataSet 3")
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT)
            set3.setColor(Color.YELLOW)
            set3.setCircleColor(Color.BLACK)
            set3.setLineWidth(2f)
            set3.setCircleRadius(3f)
            set3.setFillAlpha(65)
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.GREEN, 200))
            set3.setDrawCircleHole(false)
            set3.setHighLightColor(Color.rgb(244, 117, 117))

            // create a data object with the datasets
            val data = LineData(set1, set2, set3)
            data.setValueTextColor(Color.BLACK)
            data.setValueTextSize(9f)

            // set data
            lineChart.setData(data)

            val l = lineChart.getLegend()

            // modify the legend ...
            l.setForm(Legend.LegendForm.LINE)
            l.setTextSize(11f)
            l.setTextColor(Color.BLACK)
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
            l.setDrawInside(false)
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
