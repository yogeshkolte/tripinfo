package com.yk.tripinfo.trips

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.yk.tripinfo.R
import com.yk.tripinfo.data.model.Trip
import timber.log.Timber
import java.sql.Time

class TripsListAdapter(dataSet: List<Trip> = emptyList()) :
    DragDropSwipeAdapter<Trip, TripsListAdapter.ViewHolder>(dataSet) {

    class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val container: View = itemView.findViewById(R.id.trip_list_item)
        val itemText: TextView = itemView.findViewById(R.id.trip_name)
        val dragIcon: ImageView = itemView.findViewById(R.id.drag_icon)
        val startDate: TextView = itemView.findViewById(R.id.start_date)
        val endDate: TextView = itemView.findViewById(R.id.end_date)

    }

    override fun getViewHolder(itemLayout: View) = ViewHolder(itemLayout)

    override fun onBindViewHolder(
        item: Trip,
        viewHolder: ViewHolder,
        position: Int
    ) {
        // Here we update the contents of the view holder's views to reflect the item's data
        viewHolder.apply {
            itemText.text = item.name
            startDate.text = if(item.start_date != null) item.start_date.toString() else ""
            endDate.text = if(item.end_date != null) item.end_date.toString() else ""
            container.tag = item.id
            container.setOnClickListener (
                Navigation.createNavigateOnClickListener(R.id.toTripMap, bundleOf("trip_id" to item.id))
            )
        }
    }

    override fun getViewToTouchToStartDraggingItem(
        item: Trip,
        viewHolder: ViewHolder,
        position: Int
    ): View? {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon
    }

    class DataComparator : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            Timber.d("areItemsTheSame ${oldItem === newItem}")
            return oldItem === newItem

        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            Timber.d("areItemsTheSame ${oldItem.id == newItem.id}")
            return oldItem.id == newItem.id
        }
    }
}