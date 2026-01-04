package com.example.sunnyweather.android.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.android.R
import com.example.sunnyweather.android.logic.model.Place

class PlaceAdapter(private val onClick:(Place)->Unit): RecyclerView.Adapter<PlaceAdapter.ViewHolder>()
{
    private val placeList=mutableListOf<Place>()
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val placeName: TextView=view.findViewById(R.id.placeName)
        val placeAddress: TextView=view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val place =placeList[position]
        holder.placeName.text=place.name
        holder.placeAddress.text=place.address
        holder.itemView.setOnClickListener {
            val pos=holder.bindingAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            onClick(placeList[pos]) }
    }
    override fun getItemCount(): Int=placeList.size
    fun submitList(list:List<Place>)
    {
        placeList.clear()
        placeList.addAll(list)
        notifyDataSetChanged()
    }
}