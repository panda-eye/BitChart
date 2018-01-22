package com.bitchartdev.bitchart.Tasks

import android.os.AsyncTask
import com.bitchartdev.bitchart.BitChartActivity
import com.bitchartdev.bitchart.R
import java.lang.ref.WeakReference

/**
 * Created by bitchartdev on 23.01.2018.
 */

class BitfinesTask : AsyncTask<WeakReference<BitChartActivity>, Unit, WeakReference<BitChartActivity>>() {
    override fun doInBackground(vararg p: WeakReference<BitChartActivity>): WeakReference<BitChartActivity> {
        val activity = p[0].get() ?: return p[0]
        val response = TaskHelper.getResponse(activity.getString(R.string.bitfinex_link))
        if (response != null) {
            // TODO: Parse response
        } else {
            // TODO: Catch error
        }
        return p[0]
    }
    override fun onPostExecute(result: WeakReference<BitChartActivity>) {
        val activity = result.get() ?: return
        // TODO: Update data
        super.onPostExecute(result)
    }
}