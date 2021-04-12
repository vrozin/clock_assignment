package com.vrozin.assignment.services.managers

import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    private const val FORMAT_IN  = "yyyy-MM-dd HH:mm:ss.SSSSS"
    private const val FORMAT_OUT = "HH:mm:ss"

    fun getTime(dateTime: String?): String {
        // SimpleDateFormat should be placed here since User can change the Default format while
        // the app is running
        val inDateFormat  = SimpleDateFormat(FORMAT_IN,  Locale.getDefault()).apply { this.timeZone = TimeZone.getTimeZone("GMT") }
        val outDateFormat = SimpleDateFormat(FORMAT_OUT, Locale.getDefault())

        var toReturn = ""

        dateTime?.let {
            try {
                val date = inDateFormat.parse(dateTime)
                date?.let {
                    toReturn = outDateFormat.format(date)
                }

            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return toReturn
    }
}