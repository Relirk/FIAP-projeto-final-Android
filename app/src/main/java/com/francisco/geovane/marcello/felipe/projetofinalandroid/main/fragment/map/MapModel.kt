package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class MapModel {

    var id: String? = ""
    var name: String? = ""
    var address: String? = ""
    var latlong: LatLng? = null
    var phone: String? = ""
    var types: List<Place.Type>? = emptyList()
    var isAutoComplete: Boolean = false

    @JvmName("getLat1")
    fun getLat(): Double { return latlong!!.latitude }

    @JvmName("getLong")
    fun getLong(): Double { return latlong!!.longitude }
}