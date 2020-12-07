package com.yk.tripinfo.app

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.multidex.MultiDexApplication
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.yk.tripinfo.BuildConfig
import com.yk.tripinfo.data.AppDatabase
import com.yk.tripinfo.data.TripInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TripInfoApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {
    var numberOfActivitiesInForeground = 0

    private val database: AppDatabase
        get() = AppDatabase.getInstance(this)

    private val dataSource: TripInfoDao
        get() = database.tripInfoDao()

    val locationRepository: LocationRepository
        get() = LocationRepository.getInstance(this, dataSource)

    val tripRepository: TripRespository
        get() = TripRespository.getInstance(this, dataSource)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        registerActivityLifecycleCallbacks(this)

        registerReceiver(ScreenStatusReceiver(), IntentFilter(Intent.ACTION_SCREEN_ON))
    }

    private fun startBgLocationUpdate(context: Context, priority: Int) {
        Timber.d("startBgLocationUpdate")
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
                        LocationUpdateBroadcastReceiver.getPendingIntent(
                            context
                        ), priority
                    )
                }
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        numberOfActivitiesInForeground++
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        numberOfActivitiesInForeground--
        Timber.d("onActivityStopped $numberOfActivitiesInForeground")
        if (numberOfActivitiesInForeground == 0) {
            startBgLocationUpdate(this, LocationRequest.PRIORITY_NO_POWER)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}

