package com.francisco.geovane.marcello.felipe.projetofinalandroid.utils

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object FirebaseUtils {

    private var LOG_TAG = "DEBUG_FirebaseUtils_"
    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun fetchRemoteConfig() : FirebaseRemoteConfig {

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Log.d(LOG_TAG, "onComplete Succeeded: $task.result")

            } else { Log.d(LOG_TAG, "onComplete failed") }
        }.addOnFailureListener { e -> Log.d(LOG_TAG, "onFailure : " + e.message) }

        return remoteConfig
    }
}