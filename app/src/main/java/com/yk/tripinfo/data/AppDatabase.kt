package com.yk.tripinfo.data

import android.content.Context
import androidx.room.*
import com.yk.tripinfo.data.model.Address
import com.yk.tripinfo.data.model.AppLocation
import com.yk.tripinfo.data.model.PlaceOfInterest
import com.yk.tripinfo.data.model.Trip

private const val DATABASE_NAME = "tripinfo-db"
@Database(entities = [Trip::class, AppLocation::class, PlaceOfInterest::class, Address::class], version = 1, exportSchema = false)
@TypeConverters(DBTypeConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun tripInfoDao(): TripInfoDao

    companion object{
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}