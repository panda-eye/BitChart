package com.bitchartdev.bitchart

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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

    lateinit var krakenLayout: LinearLayoutCompat
    lateinit var yoBitLayout: LinearLayoutCompat
    lateinit var bittrexLayout: LinearLayoutCompat
    lateinit var graphLayout: GraphView
    lateinit var forSnackbarLayout: LinearLayoutCompat
    private val ref = WeakReference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        krakenLayout = kraken as LinearLayoutCompat
        yoBitLayout = yobit as LinearLayoutCompat
        bittrexLayout = bittrex as LinearLayoutCompat
        graphLayout = graph
        forSnackbarLayout = for_snackbar

        TaskHelper.mapper = ObjectMapper().registerKotlinModule()
    }
    override fun onResume() {
        super.onResume()
        // TODO: Uncomment
        if (TaskHelper.hasInternet()) {
            KrakenTask().execute(ref)
            //YoBitTask().execute(ref)
            //BitfinesTask().execute(ref)
        }
    }

    fun readKraken() {
        val krakenSeries = LineGraphSeries<DataPoint>(arrayOf(p(0.0,0.0)))
        krakenSeries.setAnimated(true)
        krakenSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.kraken_graph_line)
    }
    fun readYoBit() {
        val yoBitSeries = LineGraphSeries<DataPoint>(arrayOf(p(0.0, 0.0)))
        yoBitSeries.setAnimated(true)
        yoBitSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.yobit_graph_line)
    }
    fun readBitfines() {
        val bitfinesSeries = LineGraphSeries<DataPoint>(arrayOf(p(0.0, 0.0)))
        bitfinesSeries.setAnimated(true)
        bitfinesSeries.backgroundColor = ContextCompat.getColor(this@BitChartActivity, R.color.bitfines_graph_line)
    }

    private fun executeTasks(showError: Boolean = false) {
        if (TaskHelper.hasInternet()) {
            KrakenTask().execute(ref)
            //YoBitTask().execute(ref)
            //BitfinesTask().execute(ref)
        } else if (showError) {
            TaskHelper.showTaskSnackbar(forSnackbarLayout, R.string.message_no_internet)
        }
    }

    private fun p(x: Double, y: Double) = DataPoint(x, y)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.update_item -> {
                onResume()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
