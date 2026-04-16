package com.example.voyagetime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.voyagetime.data.local.converters.RoomTypeConverters
import com.example.voyagetime.data.local.entities.ItineraryItemEntity
import com.example.voyagetime.data.local.entities.TripEntity

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class VoyageTimeDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "voyagetime.db"
    }
}
