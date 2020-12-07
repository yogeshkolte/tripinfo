package com.yk.tripinfo.app

import androidx.lifecycle.*
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.data.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripViewModel(private val application: TripInfoApp) : AndroidViewModel(application) {

    private val tripRepository: TripRespository
        get() = application.tripRepository


    private val _allTrips: Flow<List<Trip>> by lazy { tripRepository.allTrips }

    val allTrips
        get() = _allTrips.asLiveData()


    private val _openTrip = tripRepository.openTrip

    val openTrip
        get() = _openTrip.asLiveData()

    private val _displayLocations = tripRepository.displayLocations.distinctUntilChanged()

    val displayLocations
        get() = _displayLocations.asLiveData()


    //private val _selectedLocations = tripRepository.selectedLocations
    private val _selectedLocations = MutableLiveData<List<AppLocation>>()

    val selectedLocations: LiveData<List<AppLocation>>
        get() = _selectedLocations


    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.insertTrip(trip)
        }
    }

    fun updatTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.updateTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.deleteTrip(trip)
        }
    }

    fun deleteTrackedLocations() {
        viewModelScope.launch {
            tripRepository.deleteTripLocations(0)
        }
    }

    fun getLocationsForTrip(tripId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripRepository.getLocationsForTrip(tripId)?.let {
                    withContext(Dispatchers.Main){
                        _selectedLocations.value = it
                    }
                }
            }
        }
    }
}