package com.yk.tripinfo.app

import androidx.lifecycle.*
import com.google.android.gms.location.LocationRequest
import com.yk.tripinfo.data.model.AppLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CancellationException

class LocationViewModel(private val application: TripInfoApp) : AndroidViewModel(application) {

    private val repository: LocationRepository
        get() = application.locationRepository

    private lateinit var locationUpdateJob: Job

    private val _currentKnownLocation: MutableLiveData<AppLocation> by lazy { MutableLiveData<AppLocation>() }

    val currentKnownLocation: LiveData<AppLocation>
        get() = _currentKnownLocation

//    private val _dbLocationUpdates: Flow<List<AppLocation>> by lazy { repository.tripLocations }
//
//    val dbLocationUpdates
//        get() = _dbLocationUpdates.asLiveData()


    private val _updatedLocation = repository.getLocationUpdates(
        getApplication(),
        LocationRequest.PRIORITY_HIGH_ACCURACY
    ).conflate().catch { e -> Timber.e(e) }.asLiveData()

    private val locationUpdateObserver =
        Observer<AppLocation> { location ->
            Timber.d("LocationViewModel LocationUpdates ${location.latitude} ${location.longitude}")
            _currentKnownLocation.value = location
            viewModelScope.launch {
                repository.insertLocation(location)
            }

        }

    fun getLastLocation() {
        Timber.d("getLastLocation")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.getLastKnownLocation(getApplication())?.let {
                        withContext(Dispatchers.Main) {
                            val id = System.currentTimeMillis()
                            _currentKnownLocation.value =
                                AppLocation(id, it.latitude, it.longitude, "", 0)
                        }
                    }
                    Timber.d("getLastLocation ${_currentKnownLocation.value?.toString()}")
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    fun getCurrentLocation() {
        Timber.d("getCurrentLocation")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    repository.getCurrentLocation(getApplication())
                        .let {
                            withContext(Dispatchers.Main) {
                                _currentKnownLocation.value = it
                                Timber.d("getCurrentLocation  ${it.toString()}")
                            }
                        }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    fun startLocationUpdates() {
        Timber.d("startLocationUpdates")
        locationUpdateJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getLastLocation()
                getCurrentLocation()
            }
            _updatedLocation.observeForever(locationUpdateObserver)
        }
    }

    fun stopLocationUpdates() {
        Timber.d("stopLocationUpdates")
        if(this::locationUpdateJob.isInitialized) {
            locationUpdateJob.cancel(CancellationException("User Cancelled"))
        }
        _updatedLocation.removeObserver(locationUpdateObserver)
    }

}