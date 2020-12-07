package com.yk.tripinfo.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yk.tripinfo.util.AppViewModelFactory
import com.yk.tripinfo.R
import com.yk.tripinfo.app.LocationViewModel
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.databinding.TripLocationsFragmentBinding
import timber.log.Timber


class TripLocationListFragment : Fragment() {

    companion object {
        fun newInstance() = TripLocationListFragment()
    }

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var binding: TripLocationsFragmentBinding
    private lateinit var adapter: TripLocationListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application

        binding =
            DataBindingUtil.inflate(inflater, R.layout.trip_locations_fragment, container, false)
        binding.lifecycleOwner = this

        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)
        locationViewModel =
            ViewModelProvider(this, viewModelFactory).get(LocationViewModel::class.java)

        adapter = TripLocationListAdapter()

        binding.apply {
            tripLocationsList.adapter = adapter
        }
        Timber.d("onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        locationViewModel.dbLocationUpdates.observe(viewLifecycleOwner) {
//            Timber.d("Refresh Recyclerview ${it?.size}")
//            it?.let { adapter.submitList(it) }
//        }
    }
}