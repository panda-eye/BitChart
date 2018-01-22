package com.bitchartdev.bitchart.Tasks

import android.os.AsyncTask
import com.bitchartdev.bitchart.BitChartActivity
import com.bitchartdev.bitchart.R
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.ref.WeakReference

/**
 * Created by bitchartdev on 23.01.2018.
 */

class KrakenTask: AsyncTask<WeakReference<BitChartActivity>, Unit, WeakReference<BitChartActivity>>() {
    override fun doInBackground(vararg p: WeakReference<BitChartActivity>): WeakReference<BitChartActivity> {
        val activity = p[0].get() ?: return p[0]
        val response = TaskHelper.getResponse(activity.getString(R.string.kraken_link))
        if (response != null) {
            val parsed = TaskHelper.mapper.readValue<LinkedHashMap<String, Any>>(response)
        } else {
            //TaskHelper.showTaskSnackbar(, R.string.update_failed)
        }
        return p[0]
    }

    override fun onPostExecute(result: WeakReference<BitChartActivity>) {
        super.onPostExecute(result)
        val activity = result.get() ?: return
        // TODO: Update data
    }
}