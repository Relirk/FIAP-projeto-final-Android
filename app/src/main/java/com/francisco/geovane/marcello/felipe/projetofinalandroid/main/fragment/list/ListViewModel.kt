package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.Place
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service.FirebasePlaceService

class ListViewModel : ViewModel() {

    private val firebasePlaceService = FirebasePlaceService()
    fun fetchPlaces() : LiveData<MutableList<Place>> {
        val placesList = MutableLiveData<MutableList<Place>>()
        firebasePlaceService.getAllLocations().observeForever {
            result ->
            placesList.value = result
        }
        return placesList
    }
    /*private val _text = MutableLiveData<String>().apply {
        value = "This is list Fragment"
    }
    val text: LiveData<String> = _text*/
}