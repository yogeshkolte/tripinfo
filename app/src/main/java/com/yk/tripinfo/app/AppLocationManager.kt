package com.yk.tripinfo.app

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.*
import timber.log.Timber

class AppLocationManager(
    val lifecycleOwner: LifecycleOwner,
    val locationListener: LocationListener,
    val context: Context
) : LifecycleObserver {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult != null) {
                Timber.d("Location: ${locationResult.lastLocation}")
            }
        }
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun addLocationListener() {
        startUpdatingLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationListener() {
        stopUpdatingLocation()
    }

    fun createHighPriorityLocationRequest(): LocationRequest {
        val FASTEST_UPDATE_INTERVAL = 1000 * 60 * 15
        val FORE_GROUND_UPDATE_INTERVAL = 1000 * 60 * 1
        val MAX_WAIT_TIME = FORE_GROUND_UPDATE_INTERVAL * 3

        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            smallestDisplacement = 500f
        }
    }

    fun createCurrentLocationRequest(): LocationRequest {
        val FASTEST_UPDATE_INTERVAL = 1000 * 60 * 15
        val FORE_GROUND_UPDATE_INTERVAL = 1000 * 60 * 1
        val MAX_WAIT_TIME = FORE_GROUND_UPDATE_INTERVAL * 3

        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            numUpdates = 1
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        Timber.d("startUpdatingLocation")

        fusedLocationClient.requestLocationUpdates(
            createHighPriorityLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener { locationResult ->

            Timber.d("LocationUpdates got location.")
        }.addOnFailureListener { e ->
            Timber.d("LocationUpdates Unable to get location.")
            e.printStackTrace()
        }
    }
    private fun stopUpdatingLocation() {
        Timber.d("stopUpdatingLocation")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}