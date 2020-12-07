package com.yk.tripinfo.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "address")
data class Address(
    @PrimaryKey  @ColumnInfo(name="address_id") val id : Long,
    @ColumnInfo(name="address_street") val street: String?,
    @ColumnInfo(name="address_unit") val unit: String?,
    @ColumnInfo(name="address_city") val city: String?,
    @ColumnInfo(name="address_state") val state: String?,
    @ColumnInfo(name="address_country") val country: String?,
    @ColumnInfo(name="address_postalcode") val postalCode: String?
)