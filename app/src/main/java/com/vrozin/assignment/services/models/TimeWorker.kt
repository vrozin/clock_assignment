package com.vrozin.assignment.services.models

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vrozin.assignment.R
import com.vrozin.assignment.services.managers.retrofit.ApiManager
import java.io.IOException
import java.lang.RuntimeException

//////////////////////////////////////// Can't use this since //////////////////////////////////////
//////////// "Interval duration for `PeriodicWorkRequest`s must be at least 15 minutes." ///////////
////////////////////////////////////////////////////////////////////////////////////////////////////
//class TimeWorker(private val context: Context,
//                 workerParams: WorkerParameters) : Worker(context, workerParams)
//{
//    companion object {
//        private const val TAG = "TimeWorker"
//        const val DATE_TIME_KEY = "data_key"
//    }
//
//    override fun doWork(): Result {
//        var responseSuccessful = true
//        val result: Data.Builder = Data.Builder()
//
//        try {
//            // Since we're already in BG, it's ok for a sync call
//            val response = ApiManager.getDateTime().execute() // ApiManager.getDateTime() was returning Call<DateTime>
//
//            if (response.isSuccessful) {
//                result.putString(DATE_TIME_KEY, response.body()?.dateTime
//                                ?: context.getString(R.string.network_no_data))
//            } else
//            {
//                Log.e(TAG, "doWork: API request is unsuccessful")
//                responseSuccessful = false
//            }
//
//        } catch (e: IOException) {
//            Log.e(TAG, "doWork: failed to read the API data")
//            e.printStackTrace()
//            responseSuccessful = false
//
//        } catch (e: RuntimeException) {
//            Log.e(TAG, "doWork: API request failed with RuntimeException o_O ")
//            e.printStackTrace()
//            responseSuccessful = false
//        }
//
//        return if (responseSuccessful) Result.success(result.build())
//        else Result.failure()
//    }
//}