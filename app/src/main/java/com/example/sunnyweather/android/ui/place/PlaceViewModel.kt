package com.example.sunnyweather.android.ui.place

import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.android.logic.model.Place
import androidx.lifecycle.switchMap
import com.example.sunnyweather.android.logic.Repository

class PlaceViewModel: ViewModel()
{
    private val searchLiveData= MutableLiveData<String>()
    val placeList= ArrayList<Place>()
    //swithcMap?
    //searchLiveData 变一次（输入一个 query），就要“换一条新的数据来源”（Repository 返回的 LiveData），并且只关心最新那次搜索的结果。
    val placeLiveData=
    searchLiveData.switchMap {query->
        Repository.searchPlaces(query)
    }
    fun searchPlaces(query: String)
    {
        searchLiveData.value=query
    }
}