package com.yk.tripinfo.data

import androidx.room.*
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.data.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged


@Dao
interface TripInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Transaction
    suspend fun insertAppLocation(appLocation: AppLocation) {
        val tripId = getCurrentTripid()

        var location: AppLocation? = AppLocation(
            appLocation.id,
            appLocation.latitude,
            appLocation.longitude,
            appLocation.description,
            tripId
        )
        insertLocation(location!!)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLocation(appLocation: AppLocation)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)

    @Transaction
    suspend fun deleteTrip(trip: Trip) {
        deleteTripLocations(trip.id)
        delete(trip)
    }

    @Query("DELETE FROM applocation WHERE applocation_tripId = :tripId ")
    suspend fun deleteTripLocations(tripId: Long)

    @Delete
    suspend fun deleteLocation(appLocation: AppLocation)

    @Query("SELECT * FROM trip ORDER BY trip_id DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM Trip WHERE trip_id = :tripId ")
    suspend fun getTrip(tripId: Long): Trip

    @Query("SELECT * FROM trip WHERE trip_end_date is null ORDER BY trip_id DESC LIMIT 1")
    fun getOpenTrip(): Flow<Trip>

    @Query("SELECT * FROM applocation WHERE applocation_tripId = :tripId ORDER BY appLocation_id DESC")
    fun getLocationsForTrip(tripId: Long): List<AppLocation>

    @Query("Select 0 as trip_id union SELECT  trip_id FROM trip WHERE trip_end_date is null ORDER BY trip_id DESC LIMIT 1")
    fun getCurrentTripid(): Long

    @Query("Select * from applocation where applocation_tripId = (Select 0 as trip_id union SELECT  trip_id FROM trip WHERE trip_end_date is null ORDER BY trip_id DESC LIMIT 1)")
    fun getDisplayLocations(): Flow<List<AppLocation>>

}