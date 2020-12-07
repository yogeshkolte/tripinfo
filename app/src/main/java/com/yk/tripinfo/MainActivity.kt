package com.yk.tripinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yk.tripinfo.app.BaseActivity
import com.yk.tripinfo.app.AppConfig.BACKGROUND_LOCATION_PERMISSION_INDEX
import com.yk.tripinfo.app.AppConfig.LOCATION_PERMISSION_INDEX
import com.yk.tripinfo.app.AppConfig.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
import com.yk.tripinfo.app.AppConfig.REQUEST_TURN_DEVICE_LOCATION_ON
import com.yk.tripinfo.app.LocationViewModel
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.app.TripViewModel
import com.yk.tripinfo.databinding.ActivityMainBinding
import com.yk.tripinfo.util.AppViewModelFactory
import com.yk.tripinfo.util.PermissionsUtil
import kotlinx.android.synthetic.main.app_bar_main.view.*
import timber.log.Timber


class MainActivity : BaseActivity() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var tripsViewModel: TripViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    // var requestingLocationUpdates = false
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        val toolbar = binding.appbarMain.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout? = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mapsFragment, R.id.aboutFragment, R.id.settingsFragment),
            drawerLayout
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                ?: return
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController, appBarConfiguration)

        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
        sideNavView?.setupWithNavController(navController)

//        navController.addOnDestinationChangedListener{ _, destination, _ ->
//            val dest: String = try {
//                resources.getResourceName(destination.id)
//            } catch (e: Resources.NotFoundException) {
//                Integer.toString(destination.id)
//            }
//
//            Toast.makeText(this@MainActivity, "Navigated to $dest",
//                Toast.LENGTH_SHORT).show()
//            Timber.d("Navigated to $dest")
//        }

        // val viewModelFactory = LocationViewModelFactory(application as TripInfoApp)
        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)

        locationViewModel =
            ViewModelProvider(this, viewModelFactory).get(LocationViewModel::class.java)

        tripsViewModel = ViewModelProvider(this, viewModelFactory).get(TripViewModel::class.java)

        lifecycleScope.launchWhenStarted {
            tripsViewModel.allTrips.observe(this@MainActivity) {
                it?.let {
                    Timber.d("Trips ${it.size}")
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (PermissionsUtil.foregroundAndBackgroundLocationPermissionApproved(this)) {
            checkDeviceLocationSettings(true)
        } else {
            PermissionsUtil.requestForegroundAndBackgroundPermissions(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //  outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState!!)
    }

    override fun onStop() {
        super.onStop()
        stopUpdatingLocation()
        tripsViewModel.deleteTrackedLocations()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettings(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettings(false)
        }
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult != null) {
                Timber.d("Location: ${locationResult.lastLocation}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        Timber.d("startUpdatingLocation")

        locationViewModel.startLocationUpdates()
    }

    private fun stopUpdatingLocation() {
        Timber.d("stopUpdatingLocation")
        locationViewModel.stopLocationUpdates()
    }

    fun checkDeviceLocationSettings(
        resolve: Boolean = true
    ) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e(sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                startUpdatingLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // The NavigationView already has these same navigation items, so we only add
        // navigation items to the menu here if there isn't a NavigationView
        if (navigationView == null) {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            return true
        }
        return retValue
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}




