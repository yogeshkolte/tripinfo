package com.yk.tripinfo.app

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.yk.tripinfo.data.TripInfoDao
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.data.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

class TripRespository private constructor(private val dataSource: TripInfoDao) {

    val allTrips = dataSource.getAllTrips().conflate()

    val openTrip = dataSource.getOpenTrip().conflate()

    val displayLocations = dataSource.getDisplayLocations().conflate()

    val selectedLocations = MutableLiveData<List<AppLocation>>()

//    suspend fun getLocationsForTrip(tripid: Long): List<AppLocation>{
//       val deferred = CoroutineScope(Dispatchers.IO).async {
//            dataSource.getLocationsForTrip(tripid)
//        }
//        withContext(Dispatchers.Main) {
//            try {
//                selectedLocations.value = deferred.await()
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//        }
//    }

    suspend fun getLocationsForTrip(tripId: Long): List<AppLocation> =
        suspendCancellableCoroutine { continuation ->
            continuation.resume(dataSource.getLocationsForTrip(tripId)) {emptyList<AppLocation>() }
        }


    suspend fun insertTrip(trip: Trip) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.insertTrip(trip)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun deleteTrip(trip: Trip) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.deleteTrip(trip)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun updateTrip(trip: Trip) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.updateTrip(trip)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun deleteTripLocations(trip_id: Long) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.deleteTripLocations(trip_id)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: TripRespository? = null

        fun getInstance(context: Context, dataSource: TripInfoDao): TripRespository {
            return instance ?: synchronized(this) {
                instance
                    ?: create(
                        context, dataSource
                    ).also { instance = it }
            }
        }

        private fun create(context: Context, dataSource: TripInfoDao): TripRespository {
            return TripRespository(
                dataSource
            )
        }
    }
}