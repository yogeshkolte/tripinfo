package com.yk.tripinfo

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.yk.tripinfo.app.LocationViewModel
import timber.log.Timber

class MainActivityLifeCycleObserver(
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    val locationListener: LocationListener,
    val locationViewModel: LocationViewModel
) : LifecycleObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        startUpdatingLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        stopUpdatingLocation()
    }


    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        Timber.d("startUpdatingLocation")

    }
    private fun stopUpdatingLocation() {
        Timber.d("stopUpdatingLocation")

    }
}