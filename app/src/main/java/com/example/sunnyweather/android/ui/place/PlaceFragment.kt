package com.example.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.sunnyweather.android.R
import com.example.sunnyweather.android.databinding.FragmentPlaceBinding
import com.example.sunnyweather.android.ui.weather.WeatherActivity

class PlaceFragment: Fragment(R.layout.fragment_place)
{
    private  val viewModel: PlaceViewModel by viewModels()
    //_binding：真实存放 binding 的地方，可空，生命周期跟着 View 走，销毁时置 null
    //binding：给你用的安全入口，每次访问都从 _binding 取，并用 !! 表示“我保证此刻它一定不空”
    private var _binding: FragmentPlaceBinding?=null
    private val binding get()=_binding!!
    private lateinit var adapter: PlaceAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        _binding= FragmentPlaceBinding.bind(view)
        binding.recycleView.layoutManager= LinearLayoutManager(requireContext())
        if(viewModel.isPlaceSaved())
        {
            val place=viewModel.getSavedPlace()
            val intent=Intent(requireContext(), WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            activity?.finish()
        }
        adapter= PlaceAdapter(){place->
            val intent= Intent(requireContext(), WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            viewModel.savePlace(place)
            startActivity(intent)
            activity?.finish()
        }
        binding.recycleView.adapter=adapter
        binding.searchPlaceEdit.addTextChangedListener { editable->
            val content=editable?.toString().orEmpty()
            if(content.isNotEmpty())
            {
                viewModel.searchPlaces(content)
            }
            else
            {
                showEmptyState()
                adapter.submitList(emptyList())
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner){ result->
            val places =result.getOrNull()
            if(places!=null)
            {
                showListState()
                adapter.submitList(places)
            }
            else
            {
                Toast.makeText(requireContext(), "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
    private fun showEmptyState()
    {
        binding.recycleView.visibility=View.GONE
        binding.bgImageView.visibility=View.VISIBLE
    }
    private fun showListState() {
        binding.recycleView.visibility = View.VISIBLE
        binding.bgImageView.visibility = View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}