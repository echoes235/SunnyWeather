package com.example.sunnyweather.android.ui.weather

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.android.R
import com.example.sunnyweather.android.databinding.ActivityWeatherBinding
import com.example.sunnyweather.android.databinding.ForecastBinding
import com.example.sunnyweather.android.databinding.LifeIndexBinding
import com.example.sunnyweather.android.databinding.NowBinding
import com.example.sunnyweather.android.logic.model.Weather
import com.example.sunnyweather.android.logic.model.getSky
import java.util.Locale

class WeatherActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var nowBinding: NowBinding
    private lateinit var forecastBinding: ForecastBinding
    private lateinit var lifeBinding: LifeIndexBinding
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nowBinding = binding.nowLayout
        forecastBinding = binding.forecastLayout
        lifeBinding = binding.lifeLayout
        if (viewModel.locationLng.isEmpty())
        {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty())
        {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty())
        {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null)
            {
                showWeatherInfo(weather)
            } else
            {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        })
        binding.swipeRefresh.setColorSchemeResources(R.color.white)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        nowBinding.navBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener
        {
            override fun onDrawerStateChanged(newState: Int)
            {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float)
            {
            }

            override fun onDrawerOpened(drawerView: View)
            {
            }

            override fun onDrawerClosed(drawerView: View)
            {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })
        if (viewModel.canRequest())
        {
            viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        } else
        {
            Toast.makeText(this, "请求太频繁，稍等一下", Toast.LENGTH_SHORT).show()
        }
    }

    fun closeDrawer()
    {
        binding.drawerLayout.closeDrawers()
    }

    fun refreshWeather()
    {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather)
    {
        nowBinding.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        //填充now里面的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        nowBinding.currentTemp.text = currentTempText
        nowBinding.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowBinding.currentAQI.text = currentPM25Text
        nowBinding.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //填充forecast的数据
        forecastBinding.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days)
        {
            val skycon = daily.skycon[i]
            val temprerature = daily.temperature[i]
            val view = LayoutInflater.from(this)
                .inflate(R.layout.forecast_item, forecastBinding.forecastLayout, false)
            val dataInfo = view.findViewById<TextView>(R.id.dataInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dataInfo.text = simpleDataFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempeText = "${temprerature.min.toInt()}~${temprerature.max.toInt()}℃"
            temperatureInfo.text = tempeText
            forecastBinding.forecastLayout.addView(view)
        }
        val lifeIndex = daily.lifeIndex
        lifeBinding.coldRiskText.text = lifeIndex.coldRisk[0].desc
        lifeBinding.dressingText.text = lifeIndex.dressing[0].desc
        lifeBinding.carWashingText.text = lifeIndex.carWashing[0].desc
        lifeBinding.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }
}