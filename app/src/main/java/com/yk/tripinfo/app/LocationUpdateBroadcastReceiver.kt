package com.yk.tripinfo.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.yk.tripinfo.data.AppDatabase
import com.yk.tripinfo.data.model.AppLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class LocationUpdateBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Timber.d("LocationUpdateBroadcastReceiver.onReceive")
        context ?: return
        intent ?: return

        when (intent.action) {
            AppConfig.ACTION_PROCESS_LOCATION_UPDATES -> {
                val locationResult = LocationResult.extractResult(intent) ?: return
                var location: Location? = null

                locationResult.locations.forEach loc@{ loc ->
                    location = loc
                    if (loc.accuracy.toInt() == LocationRequest.PRIORITY_HIGH_ACCURACY) return@loc
                }
                location?.let {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val database: AppDatabase =
                                    AppDatabase.getInstance(context.applicationContext)
                                val repository = LocationRepository.getInstance(
                                    context.applicationContext,
                                    database.tripInfoDao()
                                )

                                repository.insertLocation(
                                    AppLocation(
                                        System.currentTimeMillis(),
                                        it.latitude,
                                        it.longitude,
                                        "BroadcastReceiver",
                                        0
                                    )
                                )
                            }catch (e: Exception){
                                Timber.e(e)
                            }
                        }
                    }
                }

            }
        }
    }
    companion object{
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationUpdateBroadcastReceiver::class.java)
            intent.action = AppConfig.ACTION_PROCESS_LOCATION_UPDATES
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}