package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.map.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.LocationObj
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService


class MapAdapter(private val context: Context): RecyclerView.Adapter<MapAdapter.AdapterHolder>() {

    private var placeList = mutableListOf<LocationObj>()

    private val firebasePlaceService = FirebasePlaceService()

    fun setList(list: MutableList<LocationObj>) {
        placeList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        val map = LayoutInflater.from(context).inflate(R.layout.fragment_map, parent, false)
        return AdapterHolder(map)
    }

    override fun getItemCount(): Int {
        return if (placeList.size > 0) {
            placeList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val place = placeList[position]
        holder.bindView(place, position)
    }


    inner class AdapterHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindView(place: LocationObj, position: Int) {}
    }

}