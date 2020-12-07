package com.yk.tripinfo.util

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.yk.tripinfo.app.AppConfig.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.yk.tripinfo.app.AppConfig.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.yk.tripinfo.app.AppConfig.runningQOrLater
import timber.log.Timber


object PermissionsUtil {
    @TargetApi(29)
    public fun foregroundAndBackgroundLocationPermissionApproved(context: Context): Boolean {
        Timber.d("foregroundAndBackgrroundLocationPermissionsApproved")
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29)
    public fun requestForegroundAndBackgroundPermissions(activity: FragmentActivity) {
        Timber.d("requestForegroundAndBackgroundPermissions")
        if (foregroundAndBackgroundLocationPermissionApproved(activity.applicationContext))
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Timber.d("Request foreground only location permission")
        ActivityCompat.requestPermissions(
            activity,
            permissionsArray,
            resultCode
        )
    }
}