package com.example.sunnyweather.android.logic.network

import com.example.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.sunnyweather.android.SunnyWeatherApplication
interface PlaceService
{
    @GET("v2/place")
    fun  serachPlaces(@Query("query")query: String,
                      @Query("token")token: String= SunnyWeatherApplication.Token,
                      @Query("lang")lang: String="zh_CN"
    ): retrofit2.Call<PlaceResponse>
}