package com.yk.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class PlaceOfInterest(
    @PrimaryKey @ColumnInfo(name = "poi_id") val id: String,
    @ColumnInfo(name = "poi_name") val name: String,
    @ColumnInfo(name = "poi_description") val description: String?,
//    @Embedded val poi_address: Address?,
//    @Embedded val poi_location: AppLocation?,
    @ColumnInfo(name = "poi_tripId") val tripId: Long?
) {
    override fun toString(): String {
        return name
    }
}