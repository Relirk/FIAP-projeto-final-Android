package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.map.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.LocationObj
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService

class MapViewModel {

    private val firebasePlaceService = FirebasePlaceService()
    fun fetchPlaces() : LiveData<MutableList<LocationObj>> {
        val placesList = MutableLiveData<MutableList<LocationObj>>()
        firebasePlaceService.getAllLocations().observeForever {
                result ->
            placesList.value = result
        }
        return placesList
    }
}

