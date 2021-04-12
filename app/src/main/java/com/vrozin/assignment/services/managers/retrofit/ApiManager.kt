package com.vrozin.assignment.services.managers.retrofit

import com.vrozin.assignment.services.models.DateTime
import io.reactivex.Flowable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/** Implemented as singleton */
object ApiManager {
    private const val TAG = "ApiManager"
    private const val BASE_URL = "https://dateandtimeasjson.appspot.com"

    private val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
    private val client = retrofit.create(IApiTime::class.java)

    fun getDateTime(): Flowable<DateTime> {
        return client.getDateTime()
    }
}