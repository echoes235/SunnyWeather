package com.example.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

//全局获取Context,方便在其他类中使用SunnyWeatherApplication.context获取context对象
class SunnyWeatherApplication : Application() {

    companion object {
        const val Token="yjLdYIu9JM9wls79"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}