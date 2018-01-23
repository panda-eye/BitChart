package com.bitchartdev.bitchart.Tasks

import android.os.AsyncTask
import com.bitchartdev.bitchart.BitChartActivity
import com.bitchartdev.bitchart.R
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.ref.WeakReference

/**
 * Created by bitchartdev on 23.01.2018.
 */

class KrakenTask: AsyncTask<WeakReference<BitChartActivity>, Unit, Pair<WeakReference<BitChartActivity>, LinkedHashMap<String, Any>>>() {
    override fun doInBackground(vararg p: WeakReference<BitChartActivity>): Pair<WeakReference<BitChartActivity>, LinkedHashMap<String, Any>> {
        val activity = p[0].get() ?: return Pair(p[0], LinkedHashMap())
        val response = try {
            TaskHelper.getResponse(activity.getString(R.string.kraken_link))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val parsed = if (response != null) {
            try {
                TaskHelper.mapper.readValue<LinkedHashMap<String, Any>>(response)
            } catch (e: Exception) {
                e.printStackTrace()
                LinkedHashMap<String, Any>()
            }
        } else {
            TaskHelper.showTaskSnackbar(activity.forSnackbarView, R.string.update_failed)
            LinkedHashMap()
        }
        return Pair(p[0], parsed)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onPostExecute(pair: Pair<WeakReference<BitChartActivity>, LinkedHashMap<String, Any>>) {
        super.onPostExecute(pair)
        val parsed = pair.second
        if (parsed.isNotEmpty() && (parsed["error"] as ArrayList<*>).size == 0) {
            val activity = pair.first.get() ?: return
            try {
                val result = (parsed["pair"] as LinkedHashMap<String, LinkedHashMap<String, Any>>)["XXBTZUSD"]
                        ?: return
                val min = result["l"] as ArrayList<String>
                val max = result["h"] as ArrayList<String>
                val market = TaskHelper.Companion.Market()
                market.max = max[0]
                market.min = min[0]
                market.min24h = min[1]
                market.max24h = max[1]
                activity.readKraken(market)
            } catch (e: Exception) {e.printStackTrace()}
        }
    }
}