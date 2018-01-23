package com.bitchartdev.bitchart.Tasks

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import com.bitchartdev.bitchart.R
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by bitchartdev on 23.01.2018.
 */

class TaskHelper {
    companion object {
        lateinit var mapper: ObjectMapper
        fun getResponse(url: String): String? {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 7000
            connection.readTimeout = 10000
            //connection.setRequestProperty("Content-Type", "application/json")
            connection.connect()
            val iStr: InputStream = if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream else return null
            var respContent: String? = null
            try {
                val reader = BufferedReader(InputStreamReader(iStr, "UTF-8"), 8)
                respContent = reader.readLine()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                iStr.close()
                connection.disconnect()
                return respContent
            }
        }

        fun showTaskSnackbar(view: View, messageResId: Int) {
            val snack = Snackbar.make(view, messageResId, view.context.resources.getInteger(R.integer.snackbar_duration))
                    .setActionTextColor(ContextCompat.getColor(view.context, R.color.snackbar_foreground))
            snack.view.background = ContextCompat.getDrawable(view.context, R.color.snackbar_background)
            snack.show()
        }

        fun hasInternet() = Runtime.getRuntime().exec ("ping -c 1 google.com").waitFor() == 0
    }
}