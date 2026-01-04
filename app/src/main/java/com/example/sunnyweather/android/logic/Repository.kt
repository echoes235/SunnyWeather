package com.example.sunnyweather.android.logic

import android.content.Context
import androidx.collection.emptyIntSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunnyweather.android.logic.dao.PlaceDao
import com.example.sunnyweather.android.logic.model.Place
import com.example.sunnyweather.android.logic.model.Weather
import com.example.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

// Repository：数据仓库层
// 负责：决定“数据从哪来”，并把数据包装成 UI 好用的形式
object Repository {

    fun savePlace(place: Place)= PlaceDao.savePlace(place)
    fun getSavedPlace()= PlaceDao.getSavedPlace()
    fun isPlaceSaved()= PlaceDao.isPlaceSaved()
    // 对外提供一个方法：搜索城市
    // 返回值是 LiveData<Result<List<Place>>>
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {

            // 调用 Network 层的挂起函数
            // 这里会真正发起网络请求
            // 因为 liveData(Dispatchers.IO)，所以运行在 IO 线程，不会卡 UI
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)

            if (placeResponse.status == "ok") {

                val places = placeResponse.places

                Result.success(places)

            } else {
                Result.failure(
                    RuntimeException("response status is ${placeResponse.status}")
                )
            }
    }
    fun refreshWeather(lng: String,lat: String)=fire(Dispatchers.IO)
    {
        // coroutineScope：创建一个“协程作用域”
        // 作用：保证里面启动的 async 全部完成后，这个 coroutineScope 才会结束
        // 如果里面任何一个子协程失败（抛异常），coroutineScope 会把异常向外抛
        coroutineScope {
            val deferredRealtime=async {
                SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
            }
            val deferredDaily=async {
                SunnyWeatherNetwork.getDailyWeather(lng,lat)
            }
            val realtimeResponse=deferredRealtime.await()
            val dailyResponse=deferredDaily.await()
            if(realtimeResponse.status=="ok"&&dailyResponse.status=="ok")
            {
                val weather= Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                Result.success(weather)
            }else
            {
                Result.failure(
                    RuntimeException("realtime response s" +
                            "tatus is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}")
                )
            }
        }
    }
    private fun <T> fire(context: CoroutineContext,block: suspend ()-> Result<T>) =
        liveData<Result<T>>(context) {
        val result=try {
            block()
        }
        catch (e:Exception)
        {
            Result.failure<T>(e)
        }
        emit(result)
    }
}