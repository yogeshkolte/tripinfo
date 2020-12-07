package com.yk.tripinfo.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.yk.tripinfo.R
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.app.TripViewModel
import com.yk.tripinfo.data.model.Trip
import com.yk.tripinfo.databinding.AddNewTripFragmentBinding
import com.yk.tripinfo.util.AppViewModelFactory
import timber.log.Timber
import java.util.*

class AddNewTripFragment : DialogFragment() {
    lateinit var binding: AddNewTripFragmentBinding

    companion object {
        fun newInstance() = AddNewTripFragment()
    }

    private lateinit var viewModel: TripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = AppViewModelFactory.getInstance(application as TripInfoApp)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TripViewModel::class.java)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_new_trip_fragment, container, false)


        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            btnStart.tag = null
            btnStart.setOnClickListener {
                Timber.d("Dismiss")
                Timber.d("Open Trip ${viewModel.openTrip.value}")
                if (btnStart.tag != null) {
                    btnStart.tag.let {
                        if (it is Trip) {
                            viewModel.updatTrip(
                                Trip(
                                    it.id,
                                    it.name,
                                    it.start_date,
                                    Date(),
                                    it.description,
                                    it.trip_destination,
                                    it.trip_tracking
                                )
                            )
                            btnStart.tag = null
                        }
                        dismiss()
                        return@setOnClickListener
                    }
                }

                if (binding.edvTripName.text?.toString()?.length!! > 0) {
                    viewModel.addTrip(
                        Trip(
                            System.currentTimeMillis(), binding.edvTripName.text?.toString()!!,
                            Date(), null, null, null, 0
                        )
                    )
                    dismiss()
                } else {
                    Snackbar.make(binding.root, "Enter valid name.", Snackbar.LENGTH_SHORT).show()
                }
            }

            viewModel.openTrip.observe(viewLifecycleOwner) {
                it?.let {
                    btnStart.tag = it
                    edvTripName.setText(it.name)
                    btnStart.text = "End"
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)
    }

}