package com.yk.tripinfo.data.model

//@Entity(tableName = "triplocations")
data class TripLocations(
    val triplocations_trip: Trip
    //@Embedded val triplocations_trip: Trip,
//    @Relation(parentColumn = "trip_id", entityColumn = "applocation_id")
//    val triplocations_locations: List<AppLocation>
)