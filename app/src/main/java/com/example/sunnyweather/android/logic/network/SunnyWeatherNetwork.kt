package com.example.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
object SunnyWeatherNetwork
{
    //1) 首先我们通过Servicecreator创建了一个placeService的实例(因为PlaceService是一个接口不能new,所以必须通过retrofit动态代理创建)
    private val placeService = ServiceCreator.create<PlaceService>()
    // 2) 对外提供一个挂起函数：搜索城市
    //    placeService.searchPlaces(query) 返回的是 Call<PlaceResponse>（回调式请求）
    //    .await() 会把 Call<PlaceResponse> “变成” PlaceResponse（协程风格）
    suspend fun searchPlaces(query: String) = placeService.serachPlaces(query).await()
    //---天气相关API服务---
    private val weatherService= ServiceCreator.create<WeatherService>()
    suspend fun getDailyWeather(lng:String,lat: String)= weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng:String,lat: String)= weatherService.getRealtimeWeather(lng, lat).await()

    // 3) 给 Retrofit 的 Call<T> 增加一个 await() 扩展函数（泛型，适用于任何 Call<T>）
    //    目标：把 Retrofit 的回调（enqueue）包装成“可挂起返回”的协程函数
    private suspend fun <T> Call<T>.await(): T
    {
        // 4) suspendCoroutine：把“回调式异步代码”接到“协程”里
        //    它会先让当前协程挂起，并给你一个 continuation（用来恢复协程）
        //Q:continuation到底是什么?他是一个变量吗?
        //A:并不是,他是一个承载着我从哪里恢复的信息的小船,在它回来的时候会带着我要返回的信息
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>
            {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>)
                {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            continuation.resume(body)
                        } else {
                            continuation.resumeWithException(
                                RuntimeException("HTTP ${response.code()} success but body is null")
                            )
                        }
                    } else {
                        val err = try { response.errorBody()?.string() } catch (e: Exception) { "errorBody read failed" }
                        continuation.resumeWithException(
                            RuntimeException("HTTP ${response.code()} ${response.message()}, errorBody=$err")
                        )
                    }
                }
                override fun onFailure(call: Call<T>, t: Throwable)
                {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}