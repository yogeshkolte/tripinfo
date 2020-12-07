package com.yk.tripinfo.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.data.TripInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocationRepository private constructor(private val dataSource: TripInfoDao) {

    suspend fun insertLocation(appLocation: AppLocation) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.insertAppLocation(appLocation)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun getLastKnownLocation(context: Context): Location? {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return fusedLocationClient.awaitLastLocation()
    }

    suspend fun getCurrentLocation(context: Context): AppLocation? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return fusedLocationClient.awaitCurrentLocation()
    }

    fun getLocationUpdates(context: Context, priority: Int): Flow<AppLocation> {
        Timber.d("getCurrentLocation")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = when (priority) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> fusedLocationClient.createHighPriorityLocationRequest()
            LocationRequest.PRIORITY_NO_POWER -> fusedLocationClient.createNoPowerLocationRequest()
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY -> fusedLocationClient.createBalancedPowerLocationRequest()
            else -> fusedLocationClient.createCurrentLocationRequest()
        }
        return fusedLocationClient.locationFlow(locationRequest)
    }

    companion object {
        @Volatile
        private var instance: LocationRepository? = null

        fun getInstance(context: Context, dataSource: TripInfoDao): LocationRepository {
            return instance ?: synchronized(this) {
                instance
                    ?: create(
                        context, dataSource
                    ).also { instance = it }
            }
        }

        private fun create(context: Context, dataSource: TripInfoDao): LocationRepository {
            return LocationRepository(
                dataSource
            )
        }
    }
}
