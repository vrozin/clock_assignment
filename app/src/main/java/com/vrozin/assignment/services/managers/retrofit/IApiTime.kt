package com.vrozin.assignment.services.managers.retrofit

import com.vrozin.assignment.services.models.DateTime
import io.reactivex.Flowable
import retrofit2.http.GET

interface IApiTime {
    @GET("/")
    fun getDateTime(): Flowable<DateTime>
//    fun getDateTime(): Call<DateTime>
}