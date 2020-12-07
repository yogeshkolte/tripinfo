package com.yk.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applocation")
data class AppLocation(
    @PrimaryKey @ColumnInfo(name = "applocation_id") val id: Long,
    @ColumnInfo(name = "applocation_latitude") val latitude: Double,
    @ColumnInfo(name = "applocation_longitude") val longitude: Double,
    @ColumnInfo(name = "applocation_description") val description: String?,
    @ColumnInfo(name = "applocation_tripId") val tripId: Long
)
