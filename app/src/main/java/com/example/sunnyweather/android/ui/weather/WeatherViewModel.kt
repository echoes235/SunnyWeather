package com.example.sunnyweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.sunnyweather.android.logic.Repository
import com.example.sunnyweather.android.logic.model.Location

class WeatherViewModel: ViewModel()
{
    private val locationLiveData= MutableLiveData<Location>()
    var locationLng=""
    var locationLat=""
    var placeName=""
    var hasLoaded=false
    val weatherLiveData=locationLiveData.switchMap {
        location ->
        Repository.refreshWeather(location.lng,location.lat)
    }
    private var lastReqTime = 0L

    fun canRequest(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastReqTime < 8000) return false // 8 秒内不重复
        lastReqTime = now
        return true
    }
    fun refreshWeather(lng: String,lat: String)
    {
        locationLiveData.value= Location(lng,lat)
    }
}