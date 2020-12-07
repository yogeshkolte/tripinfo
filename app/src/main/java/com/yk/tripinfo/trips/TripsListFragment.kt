package com.yk.tripinfo.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.yk.tripinfo.util.AppViewModelFactory
import com.yk.tripinfo.R
import com.yk.tripinfo.app.TripInfoApp
import com.yk.tripinfo.app.TripViewModel
import com.yk.tripinfo.data.model.Trip
import timber.log.Timber

class TripsListFragment : Fragment() {

    companion object {
        fun newInstance() = TripsListFragment()
    }

    private lateinit var viewModel: TripViewModel
    private lateinit var lstAdapter: TripsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = AppViewModelFactory(application as TripInfoApp)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TripViewModel::class.java)

        val root = inflater.inflate(R.layout.trips_list_fragment, container, false)
        val lst =
            root.findViewById<com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView>(
                R.id.trips_list
            )
        lstAdapter = TripsListAdapter(emptyList())
        lst.layoutManager = LinearLayoutManager(context)
        lst.adapter = lstAdapter
        lst.disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.RIGHT)
        lst.swipeListener = onItemSwipeListener
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allTrips.observe(viewLifecycleOwner) {
            Timber.d("Refresh Recyclerview ${it?.size}")
            it?.let {
                lstAdapter.dataSet = it
                lstAdapter.notifyDataSetChanged()
            }
        }

    }

    private val onItemSwipeListener = object : OnItemSwipeListener<Trip> {
        override fun onItemSwiped(
            position: Int,
            direction: OnItemSwipeListener.SwipeDirection,
            item: Trip
        ): Boolean {
            when (direction) {
                OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT -> onItemSwipedLeft(item, position)

            }
            return false
        }
    }

    private fun onItemSwipedLeft(item: Trip, position: Int) {
        Timber.d("$item (position $position) swiped to the left")
        viewModel.deleteTrip(item)
        //removeItem(item, position)
    }
}