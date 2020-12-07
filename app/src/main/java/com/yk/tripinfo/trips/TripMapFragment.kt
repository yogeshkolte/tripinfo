package com.yk.tripinfo.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yk.tripinfo.R
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.app.TripViewModel
import com.yk.tripinfo.util.AppViewModelFactory
import timber.log.Timber

class TripMapFragment : Fragment() {
    private var tripId: Long = 0
    private lateinit var tripsViewModel: TripViewModel
    private lateinit var mMap: GoogleMap
    private var mapInitialized: Boolean = false
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabledDuringRotateOrZoom = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
        }
        mapInitialized = true
        Timber.d("OnMapReadyCallback mMap ${mMap?.hashCode()}")
        addObserversToVM()
        tripsViewModel.getLocationsForTrip(tripId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)
        tripsViewModel = ViewModelProvider(this, viewModelFactory).get(TripViewModel::class.java)
        arguments?.let {
            val safeArgs = TripMapFragmentArgs.fromBundle(requireArguments())
            tripId = safeArgs.tripId
            Timber.d("TripID $tripId")
        }
        return inflater.inflate(R.layout.fragment_trip_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun addObserversToVM() {
        Timber.d("addObserversToVM")
        tripsViewModel.selectedLocations.observe(viewLifecycleOwner, { locations ->
            if (mapInitialized && this::mMap.isInitialized) {
                locations.forEach {
                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        )
                    )
                }
                val loc = locations.last()
                val latlng = LatLng(
                    loc.latitude,
                    loc.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))
            }
        })
    }
}