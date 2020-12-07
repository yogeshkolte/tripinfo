package com.yk.tripinfo.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yk.tripinfo.R
import com.yk.tripinfo.app.LocationViewModel
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.app.TripViewModel
import com.yk.tripinfo.util.AppViewModelFactory
import com.yk.tripinfo.util.shouldUpdateLocation
import timber.log.Timber

class MapsFragment : Fragment() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var tripsViewModel: TripViewModel

    private lateinit var mMap: GoogleMap
    private var mapInitialized: Boolean = false

    var currentLatLng = LatLng(0.0, 0.0)

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap = googleMap
        with(mMap.uiSettings){
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabledDuringRotateOrZoom = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
        }
        if (currentLatLng.latitude != 0.0 && currentLatLng.longitude != 0.0) {
            locationViewModel.getLastLocation()
            googleMap.addMarker(MarkerOptions().position(currentLatLng))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        mapInitialized = true
        Timber.d("OnMapReadyCallback mMap ${mMap?.hashCode()}")
        addObserversToVM()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        setHasOptionsMenu(true)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)
        tripsViewModel = ViewModelProvider(this, viewModelFactory).get(TripViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return NavigationUI.onNavDestinationSelected(
            item!!,
            navController
        ) || super.onOptionsItemSelected(item)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        Timber.d("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    private fun addObserversToVM() {
        Timber.d("addObserversToVM")
        locationViewModel.currentKnownLocation.observe(viewLifecycleOwner, { location ->
            Timber.d("Map location: ${location.toString()} mapInitialized: ${mapInitialized} mMap.isInitialized: ${this::mMap.isInitialized}")

            if (mapInitialized && this::mMap.isInitialized) {
                if (!shouldUpdateLocation(
                        currentLatLng.latitude.toFloat(),
                        currentLatLng.longitude.toFloat(),
                        location.latitude.toFloat(),
                        location.longitude.toFloat()
                    )
                ) return@observe
                currentLatLng = LatLng(location.latitude, location.longitude)
                val zoomLevel = 15f
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are Here"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))
            }

        })
        tripsViewModel.displayLocations.observe(viewLifecycleOwner, { locations ->
            if (mapInitialized && this::mMap.isInitialized) {
                locations.forEach { mMap.addMarker(MarkerOptions().position(LatLng(it.latitude,it.longitude)))}
            }
        })
    }
}