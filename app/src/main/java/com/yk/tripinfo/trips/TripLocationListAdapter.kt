package com.yk.tripinfo.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.databinding.TripLocationItemBinding
import timber.log.Timber

class TripLocationListAdapter: ListAdapter<AppLocation, TripLocationListAdapter.ListItemViewHolder>(
    DataComparator()
) {

    class ListItemViewHolder(private var binding: TripLocationItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(appLocation: AppLocation){
            binding.appLocation = appLocation
            binding.executePendingBindings()
        }
        companion object{
            fun create(parent: ViewGroup): ListItemViewHolder {
                val binding = TripLocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ListItemViewHolder(binding)
            }
        }
    }

    class DataComparator : DiffUtil.ItemCallback<AppLocation>() {
        override fun areItemsTheSame(oldItem: AppLocation, newItem: AppLocation): Boolean {
            Timber.d("areItemsTheSame ${oldItem === newItem}")
            return oldItem === newItem

        }
        override fun areContentsTheSame(oldItem: AppLocation, newItem: AppLocation): Boolean {
            Timber.d("areItemsTheSame ${oldItem.id == newItem.id}")
            return oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListItemViewHolder {
        return ListItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val newItem = getItem(position)
        holder.bind(newItem)
    }
}