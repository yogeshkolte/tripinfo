package com.yk.tripinfo.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yk.tripinfo.R
import com.yk.tripinfo.app.LocationViewModel
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.databinding.MainFragmentBinding
import com.yk.tripinfo.util.AppViewModelFactory
import timber.log.Timber

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        val application = requireNotNull(this.activity).application

        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)
        locationViewModel = ViewModelProvider(this, viewModelFactory).get(LocationViewModel::class.java)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewmodel = viewModel

        setListeners()
        addObserversToVM()

        locationViewModel.getLastLocation()

    }

    private fun setListeners() {
    }

    private fun addObserversToVM() {
    }
}