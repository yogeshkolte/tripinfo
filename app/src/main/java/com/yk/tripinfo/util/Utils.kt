package com.yk.tripinfo.util

import timber.log.Timber

//In miles
public fun distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {
    val earthRadius = 3958.75
    val dLat = Math.toRadians((lat2 - lat1).toDouble())
    val dLng = Math.toRadians((lng2 - lng1).toDouble())
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val dist = earthRadius * c
    Timber.d("Distance $dist")
    return dist.toFloat()
}

public fun shouldUpdateLocation(lat1: Float, lng1: Float, lat2: Float, lng2: Float):Boolean {
   return distFrom(lat1, lng1, lat2, lng2) > 0.1
}