package com.example.voyagetime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.voyagetime.utils.RoomTypeConverters
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import com.example.voyagetime.data.local.entity.TripEntity

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
