package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.fragment.map

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.R
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.AnalyticsUtils
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.utils.FirebaseUtils
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var mapsApiKey: String

    private var LOG_TAG = "myLog__"
    private var globalSavedInstanceState: Bundle? = null
    private var bundle: Bundle = Bundle()
    private var appId: String = BuildConfig.APP_ID
    private var pageId: String = "Map"
    private val REQUEST_CODE = 200

    private lateinit var globalRoot: View
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var selectedPlace: MapModel
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )


    // default location on map - FIAP - Campus Paulista
    private val defaultAddress = LatLng(-23.5641095, -46.65240989999999)
    private val defaultZoom = 16F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        globalSavedInstanceState = savedInstanceState
        globalRoot = root

        val btnReset: Button = root.findViewById(R.id.btn_reset)
        val btnAdd: Button = root.findViewById(R.id.btn_add)

        // Analytics
        analytics = FirebaseAnalytics.getInstance(context)
        AnalyticsUtils.setPageData(analytics, bundle, appId, pageId)

        // Remote Config
        remoteConfig = FirebaseUtils.fetchRemoteConfig()
        mapsApiKey = remoteConfig.getString("google_maps_api_key")


        btnReset.setOnClickListener {
            map.clear()
            autocompleteFragment.setText("")
            setDefaultAdress()
        }

        btnAdd.setOnClickListener {
            //TODO: utilizar 'selectedPlace' para obter as informações que serão salvas no DB
            Log.i(LOG_TAG, Gson().toJson(selectedPlace))
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        if (checkSelfPermission(requireActivity().applicationContext, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this.PERMISSIONS, REQUEST_CODE)
        } else {
            initMap()
        }

        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap()
                } else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Permissão negada")
                            .setMessage("Para usar as funcionalidades do mapa a permissão de localização é necessária, caso tenha selecionado por 'não perguntar novamente' verifique as configurações do seu dispositivo, do contrário acesse novamente a guia Mapa")
                            .setPositiveButton("Yes",
                                DialogInterface.OnClickListener { dialog, _ ->
                                   dialog.dismiss()
                                })
                            .show()
                    }
                }
            }
        }
    }

    private fun initMap() {
        if (checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            val task: Task<*> = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location ->
                if (location != null) currentLocation = location as Location
                loadMap()
            }
        }
    }

    private fun loadMap(): Boolean {
        mapView = globalRoot.findViewById(R.id.mapView) as MapView
        mapView.onCreate(globalSavedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
            Places.initialize(requireActivity().applicationContext, mapsApiKey);

            autocompleteFragment = childFragmentManager.findFragmentById(R.id.map_autocomplete) as AutocompleteSupportFragment
            autocompleteFragment.setPlaceFields(
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.PHONE_NUMBER,
                    Place.Field.TYPES
                )
            )

            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

                override fun onPlaceSelected(place: Place) {

                    selectedPlace = MapModel()
                    selectedPlace.id = place.id
                    selectedPlace.name = place.name
                    selectedPlace.address = place.address
                    selectedPlace.latlong = place.latLng
                    selectedPlace.phone = place.phoneNumber
                    selectedPlace.types = place.types
                    selectedPlace.isAutoComplete = true

                    updateMap(selectedPlace)
                }

                override fun onError(status: Status) {

                    Log.i(LOG_TAG, status.toString())
                }
            })

        } catch (e: Exception) {
            Log.e(LOG_TAG, e.toString())
            return false
        }

        mapView.getMapAsync(this)

        return true
    }

    private fun updateMap(latlong: LatLng) {

        selectedPlace = MapModel()
        selectedPlace.latlong = latlong

        updateMap(selectedPlace)
    }

    private fun updateMap(place: MapModel) {

        try {

            if(!place.isAutoComplete) {

                val geocoder = Geocoder(this.context)
                val addressList: List<Address> = geocoder.getFromLocation(
                    place.getLat(),
                    place.getLong(),
                    1
                )

                place.name = if (place.name!!.isEmpty() && !TextUtils.isDigitsOnly(addressList[0].featureName)) {
                    addressList[0].featureName
                } else if (place.name!!.isEmpty() && TextUtils.isDigitsOnly(addressList[0].featureName)){
                    addressList[0].subLocality + " " + addressList[0].subThoroughfare + ", " + addressList[0].subAdminArea
                } else {
                    place.name!!
                }

                place.address = addressList[0].getAddressLine(0)
            }

            val options = MarkerOptions()
            options.position(place.latlong!!)
            options.title(place.name)
            options.draggable(true)

            map.addMarker(options).showInfoWindow()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latlong, defaultZoom))

        } catch (e: IOException) {
            Log.d(LOG_TAG, e.localizedMessage.toString())
        }
    }

    private fun setDefaultAdress() {

        selectedPlace = MapModel()
        selectedPlace.name = "FIAP"
        selectedPlace.latlong = defaultAddress

        updateMap(selectedPlace)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isRotateGesturesEnabled = false
        map.setMinZoomPreference(10F)
        map.setMaxZoomPreference(18F)
        map.setOnMarkerDragListener(this)
        map.setOnMapClickListener(this)

        setDefaultAdress()
    }

    override fun onMapClick(clickedPoint: LatLng) { updateMap(clickedPoint) }

    //do nothing - here just because it's an override mandatory
    override fun onMarkerDragStart(movedPoint: Marker) { }
    override fun onMarkerDrag(movedPoint: Marker) { }

    override fun onMarkerDragEnd(movedPoint: Marker) { updateMap(
        LatLng(
            movedPoint.position.latitude,
            movedPoint.position.longitude
        )
    ) }

//    override fun onResume() {
//
//        super.onResume()
//        mapView.onResume()
//    }
}