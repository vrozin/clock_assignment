package com.vrozin.assignment.services.models

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.vrozin.assignment.BuildConfig

class ApplicationBase : Application(), LifecycleObserver, Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appBackgrounded() {
        Log.i(TAG, "appBackgrounded")
        sendBroadcast(Intent(APP_BACKGROUNDED))
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun appForegrounded() {
        Log.i(TAG, "appForegrounded")
        sendBroadcast(Intent(APP_FOREGROUNDED))
    }

    companion object {
        private const val TAG = "ApplicationBase"
        const val APP_BACKGROUNDED = "APP_BACKGROUNDED"
        const val APP_FOREGROUNDED = "APP_FOREGROUNDED"
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }

    }
}