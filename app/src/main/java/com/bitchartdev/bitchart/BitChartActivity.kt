package com.bitchartdev.bitchart

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

    private lateinit var graphLayout: GraphView
    lateinit var forSnackbarView: LinearLayoutCompat
    private val ref = WeakReference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        val titleLayout = title as LinearLayoutCompat
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

        val bitfinesLayout = bittrex as LinearLayoutCompat
        bitfinesLayout.findViewById<AppCompatTextView>(R.id.marketName).setText(R.string.name_bitfines)
        bitfinesMin = bitfinesLayout.findViewById(R.id.marketMin)
        bitfinesMax = bitfinesLayout.findViewById(R.id.marketMax)

        graphLayout = graph
        forSnackbarView = for_snackbar

        TaskHelper.mapper = ObjectMapper().registerKotlinModule()
    }
    override fun onResume() {
        super.onResume()
        executeTasks()
    }

    fun readKraken(market: TaskHelper.Companion.Market) {
        val krakenSeries = LineGraphSeries<DataPoint>(market.parse())
        krakenSeries.setAnimated(true)
        krakenSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.kraken_graph_line)
        graphLayout.addSeries(krakenSeries)
    }
    fun readYoBit(market: TaskHelper.Companion.Market) {
        val yoBitSeries = LineGraphSeries<DataPoint>(market.parse())
        yoBitSeries.setAnimated(true)
        yoBitSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.yobit_graph_line)
        graphLayout.addSeries(yoBitSeries)
    }
    fun readBitfines(market: TaskHelper.Companion.Market) {
        val bitfinesSeries = LineGraphSeries<DataPoint>(market.parse())
        bitfinesSeries.setAnimated(true)
        bitfinesSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.bitfines_graph_line)
        graphLayout.addSeries(bitfinesSeries)
    }

    private fun executeTasks(showError: Boolean = false) {
        if (TaskHelper.hasInternet(this@BitChartActivity)) {
            KrakenTask().execute(ref)
            //YoBitTask().execute(ref)
            //BitfinesTask().execute(ref)

            graphLayout.removeAllSeries()
        } else if (showError) {
            TaskHelper.showTaskSnackbar(forSnackbarView, R.string.no_internet)
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
        private fun TaskHelper.Companion.Market.parse(): Array<DataPoint> {
            val ar = ArrayList<DataPoint>()
            if (min24h != "") ar.add(p(min24h, 0.0))
            if (min != "") ar.add(p(min, 1.0))
            if (max != "") ar.add(p(max, 2.0))
            if (min24h != "") ar.add(p(max24h, 3.0))
            return ar.toTypedArray()
        }
        private fun p(value: String, position: Double) = DataPoint(position, value.toDoubleOrNull() ?: 0.0)
    }
}
