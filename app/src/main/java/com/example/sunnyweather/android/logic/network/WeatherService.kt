package com.example.sunnyweather.android.logic.network

import com.example.sunnyweather.android.SunnyWeatherApplication
import com.example.sunnyweather.android.logic.model.DailyResponse
import com.example.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService
{
    //{lng},{lat} 用来传递经纬度参数，路径中的占位符用大括号括起来。
    @GET("v2.5/${SunnyWeatherApplication.Token}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng: String,
                           @Path("lat") lat: String): retrofit2.Call<RealtimeResponse>
    @GET("v2.5/${SunnyWeatherApplication.Token}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng")lng: String,
                        @Path("lat") lat: String): retrofit2.Call<DailyResponse>
}