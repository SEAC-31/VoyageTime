package com.example.voyagetime.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.TripImageDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.entity.AccessLogEntity
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.data.local.entity.TripImageEntity
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.utils.RoomTypeConverters

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class,
        UserEntity::class,
        AccessLogEntity::class,
        ReservationEntity::class,
        TripImageEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class VoyageTimeDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun reservationDao(): ReservationDao
    abstract fun tripImageDao(): TripImageDao

    companion object {
        const val DATABASE_NAME = "voyagetime.db"

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE trips ADD COLUMN cover_image_uri TEXT")
            }
        }
    }
}
