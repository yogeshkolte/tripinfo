package com.yk.tripinfo.app

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.CancellationTokenSource
import com.yk.tripinfo.app.AppConfig.FASTEST_UPDATE_INTERVAL
import com.yk.tripinfo.data.model.AppLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resumeWithException

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitLastLocation(): Location? =
    suspendCancellableCoroutine { continuation ->
        lastLocation.addOnSuccessListener { location ->
            continuation.resume(location) { t ->
                Timber.e(t)
            }
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }

fun Location.displayString(): String {
    return "Location ${this.latitude} ${this.longitude} "
}

@SuppressLint("MissingPermission")
fun FusedLocationProviderClient.locationFlow(locationRequest: LocationRequest) =
    callbackFlow<AppLocation> {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                Timber.d("FusedLocationProviderClient.locationFlow Callback")
                result ?: return
                var location: Location? = null
                for (loc in result.locations) {
                    if (location == null) {
                        location = loc
                    } else {
                        if (loc.accuracy < location.accuracy) {
                            location = loc
                        }
                    }
                }
                location?.let {
                    offer(
                        AppLocation(
                            System.currentTimeMillis(),
                            location.latitude,
                            location.longitude,
                            "",
                            0
                        )
                    )
                }
            }
        }

        requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        ).addOnFailureListener { e -> close(e) }

        awaitClose {
            removeLocationUpdates(callback) // clean up when Flow collection ends
        }
    }

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitCurrentLocation(): AppLocation? =
    suspendCancellableCoroutine { continuation ->
        val cancellationToken = CancellationTokenSource().token
        val task = getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken)
        task.addOnSuccessListener { location ->
            Timber.d("FusedLocationProviderClient.awaitCurrentLocation ${location.latitude} ${location.longitude}")
            continuation.resume(
                AppLocation(
                    System.currentTimeMillis(),
                    location.latitude,
                    location.longitude,
                    "",
                    0
                )
            ) { t ->
                Timber.e(t)
            }
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.requestBGUpdates(
    pendingIntent: PendingIntent, priority: Int
) =
    suspend {
        withContext(Dispatchers.IO) {
            val locationRequest = when (priority) {
                LocationRequest.PRIORITY_HIGH_ACCURACY -> createHighPriorityLocationRequest()
                LocationRequest.PRIORITY_NO_POWER -> createNoPowerLocationRequest()
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY -> createBalancedPowerLocationRequest()
                else -> createNoPowerLocationRequest()
            }
            requestLocationUpdates(locationRequest, pendingIntent)
        }
    }

fun FusedLocationProviderClient.createHighPriorityLocationRequest(): LocationRequest =
    LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 5000
        smallestDisplacement = 600f
    }


fun FusedLocationProviderClient.createCurrentLocationRequest(): LocationRequest =
    LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 0
        numUpdates = 1
    }


fun FusedLocationProviderClient.createNoPowerLocationRequest(): LocationRequest =
    LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_NO_POWER
        fastestInterval = FASTEST_UPDATE_INTERVAL
        smallestDisplacement = 600f
    }


fun FusedLocationProviderClient.createBalancedPowerLocationRequest(): LocationRequest =
    LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        fastestInterval = FASTEST_UPDATE_INTERVAL
        smallestDisplacement = 600f
        expirationTime = 1000 * 60 * 1
        numUpdates = 1
    }
