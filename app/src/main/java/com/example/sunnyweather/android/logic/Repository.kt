package com.example.sunnyweather.android.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunnyweather.android.logic.model.Place
import com.example.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

// Repository：数据仓库层
// 负责：决定“数据从哪来”，并把数据包装成 UI 好用的形式
object Repository {

    // 对外提供一个方法：搜索城市
    // 返回值是 LiveData<Result<List<Place>>>
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {

        val result = try {

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

        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }

        // 8️⃣ 把 result 发射出去
        //    emit 会通知观察这个 LiveData 的上层（ViewModel / UI）
        emit(result)
    }
}