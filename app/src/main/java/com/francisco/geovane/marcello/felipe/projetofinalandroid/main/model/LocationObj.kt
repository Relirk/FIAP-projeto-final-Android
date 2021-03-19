package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model

data class LocationObj(
        val id: String, //
        val name: String, //
        val description: String,
        val lat: Number?, //
        val lng: Number?, //
        val isVisited: Boolean,
        val phoneNumber: String, //
        val address: String, //
        val image: String?,
        val flavor: String? //
)