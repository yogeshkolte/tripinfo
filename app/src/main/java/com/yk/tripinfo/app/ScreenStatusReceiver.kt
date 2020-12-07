package com.yk.tripinfo.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.yk.tripinfo.app.LocationUpdateBroadcastReceiver
import com.yk.tripinfo.app.requestBGUpdates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ScreenStatusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("ScreenStatusReceiver.onReceive")
        if (context != null && intent != null) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)

                        fusedLocationClient.requestBGUpdates(
                            LocationUpdateBroadcastReceiver.getPendingIntent(context),
                            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                        )
                    }
                }
            }
        }
    }
}