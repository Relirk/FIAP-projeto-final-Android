package com.francisco.geovane.marcello.felipe.projetofinalandroid.main.service

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.francisco.geovane.marcello.felipe.projetofinalandroid.BuildConfig
import com.francisco.geovane.marcello.felipe.projetofinalandroid.main.model.LocationObj
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FirebasePlaceService {
    val db = Firebase.firestore
    private var appId: String = BuildConfig.APP_ID

    private var imageRef: StorageReference? = Firebase.storage.reference.child("locations")

    fun getAllLocations(): LiveData<MutableList<LocationObj>> {
        val users = MutableLiveData<MutableList<LocationObj>>()
        db.collection("Locations")
            .get()
            .addOnSuccessListener { result ->
                val listPlaces = mutableListOf<LocationObj>()
                for (document in result) {
                    val id = document.id
                    val name = document.getString("name")
                    val lat = document.get("lat")
                    val lng = document.get("lng")
                    val isVisited = document.getBoolean("isVisited")
                    val address = document.getString("address")
                    val description = document.getString("description")
                    val phoneNumber = document.getString("phoneNumber")
                    val imageUrl = document.getString("image")
                    val flavor = document.getString("flavor")
                    val place = LocationObj(
                        id!!,
                        name!!,
                        description!!,
                        lat!! as Number,
                        lng!! as Number,
                        isVisited!!,
                        phoneNumber!!,
                        address!!,
                        imageUrl!!,
                        flavor!!
                    )
                    if (place.flavor == appId) {
                        listPlaces.add(place)
                    }
                    Log.d(TAG, "DocumentSnapshot data: $document")
                }
                users.value = listPlaces
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        return users
    }

    fun saveNewLocation(fields: LocationObj) {
        db.collection("Locations")
            .add(
                mapOf(
                    "name" to fields.name,
                    "address" to fields.address,
                    "description" to fields.description,
                    "phoneNumber" to fields.phoneNumber,
                    "isVisited" to fields.isVisited,
                    "flavor" to fields.flavor
                )
            )
            .addOnSuccessListener {
                Log.d("Firebase", "Document ${fields.name} saved successful! ")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error saving new document:", exception)
            }
    }

    fun saveNewPlaces(places: Array<LocationObj>) {
        for (place in places) {
            db.collection("Locations")
                .add({
                    "name" to place.name;
                    "address" to place.address;
                    "description" to place.description;
                    "lat" to place.lat;
                    "lng" to place.lng;
                    "phoneNumber" to place.phoneNumber;
                    "isVisited" to place.isVisited;
                    "flavor" to place.flavor;
                })
                .addOnSuccessListener {
                    Log.d("Firebase", "Document ${place.name} saved successful! ")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error saving new document:", exception)
                }
        }
    }

    fun saveEditedLocation(id: String, fields: LocationObj) {
        val fieldRef = db.collection("Locations").document(id)

        fieldRef
            .update(mapOf(
                "name" to fields.name,
                "address" to fields.address,
                "description" to fields.description,
                "phoneNumber" to fields.phoneNumber,
                "isVisited" to fields.isVisited
            ))
            .addOnSuccessListener {
                Log.d("Firebase", "Document ${fields.name} saved successful! ")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error saving document $id:", exception)
            }
    }

    fun deleteLocation(id: String) {
        db.collection("Locations").document(id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "Document $id deleted successful.") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document $id", e) }
    }

    fun uploadImage(imageURI: Uri) {
        val upload = imageRef?.child("locations/")?.putFile(imageURI)
        upload?.addOnSuccessListener {
            Log.e("CloudStorage", "Image upload successfully!")
        }?.addOnFailureListener {
            Log.e(TAG, "Image upload failed ")
        }
    }
}