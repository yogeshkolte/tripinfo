package com.yk.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "trip")
data class Trip(
    @PrimaryKey @ColumnInfo(name="trip_id") val id: Long,
    @ColumnInfo(name="trip_name") val name: String,
    @ColumnInfo(name="trip_start_date") val start_date: Date?,
    @ColumnInfo(name="trip_end_date")  val end_date: Date?,
    @ColumnInfo(name="trip_description") val description: String?,
    @ColumnInfo(name="trip_destination") val trip_destination: String?,
    @ColumnInfo(name="trip_tracking") val trip_tracking: Int?
)