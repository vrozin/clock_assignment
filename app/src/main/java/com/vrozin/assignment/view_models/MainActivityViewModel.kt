package com.vrozin.assignment.view_models

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vrozin.assignment.services.managers.TimeManager
import com.vrozin.assignment.services.managers.retrofit.ApiManager
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    private val app: Application = application

    val bottomAreaHeightInitial = app.dpToPx(80)
    val bottomAreaMargin = app.dpToPx(40)

    private val _networkPermissionGranted = MutableLiveData(false)
    val networkPermissionGranted: LiveData<Boolean>
        get() = _networkPermissionGranted

    private val _currentTime = MutableLiveData("")
    val currentTime: LiveData<String>
        get() = _currentTime

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val flowable = Flowable.interval(1L, TimeUnit.SECONDS, io.reactivex.schedulers.Schedulers.io())
        .observeOn(io.reactivex.schedulers.Schedulers.newThread())
        .map {
            ApiManager.getDateTime().subscribe(
                { response ->
                    response?.let { _currentTime.postValue(TimeManager.getTime(it.dateTime)) }
                },
                {
                    Log.e(TAG, "Error while fetching DateTime object", it)
                }
            )
        }

    fun networkPermissionGranted() {
        if (_networkPermissionGranted.value == false) {
            _networkPermissionGranted.value = true
            flowable.subscribe {
                compositeDisposable.add(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    private fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

}