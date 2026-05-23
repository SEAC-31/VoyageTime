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
        ReservationEntity::class,  // T2.3 — nueva tabla
        TripImageEntity::class      // T3.1/T3.2 — imágenes por viaje
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class VoyageTimeDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun reservationDao(): ReservationDao   // T2.3
    abstract fun tripImageDao(): TripImageDao       // T3.1/T3.2

    companion object {
        const val DATABASE_NAME = "voyagetime.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        firebase_uid TEXT NOT NULL PRIMARY KEY,
                        username TEXT NOT NULL,
                        email TEXT NOT NULL,
                        birthdate TEXT,
                        address TEXT NOT NULL DEFAULT '',
                        country TEXT NOT NULL DEFAULT '',
                        phone TEXT NOT NULL DEFAULT '',
                        accept_emails INTEGER NOT NULL DEFAULT 0,
                        created_at TEXT NOT NULL
                    )
                """.trimIndent())

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_username ON users(username)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_firebase_uid ON users(firebase_uid)")

                db.execSQL("ALTER TABLE trips ADD COLUMN user_id TEXT REFERENCES users(firebase_uid) ON DELETE CASCADE")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_trips_user_id ON trips(user_id)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS access_log (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        user_id TEXT NOT NULL,
                        event_type TEXT NOT NULL,
                        timestamp TEXT NOT NULL,
                        FOREIGN KEY(user_id) REFERENCES users(firebase_uid) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS index_access_log_user_id ON access_log(user_id)")
            }
        }

        // T2.3 — Migración versión 2 → 3: añade tabla reservations
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS reservations (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        api_reservation_id TEXT NOT NULL,
                        trip_id INTEGER NOT NULL,
                        user_id TEXT NOT NULL,
                        hotel_id TEXT NOT NULL,
                        hotel_name TEXT NOT NULL,
                        hotel_image_url TEXT NOT NULL,
                        room_id TEXT NOT NULL,
                        room_type TEXT NOT NULL,
                        room_price REAL NOT NULL,
                        room_images_json TEXT NOT NULL,
                        start_date TEXT NOT NULL,
                        end_date TEXT NOT NULL,
                        guest_name TEXT NOT NULL,
                        guest_email TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE,
                        FOREIGN KEY(user_id) REFERENCES users(firebase_uid) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS index_reservations_trip_id ON reservations(trip_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reservations_user_id ON reservations(user_id)")
            }
        }

        // T3.1/T3.2 — Migración versión 3 → 4: imágenes vinculadas a viajes
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS trip_images (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        trip_id INTEGER NOT NULL,
                        user_id TEXT NOT NULL,
                        image_uri TEXT NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE,
                        FOREIGN KEY(user_id) REFERENCES users(firebase_uid) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS index_trip_images_trip_id ON trip_images(trip_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_trip_images_user_id ON trip_images(user_id)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_trip_images_trip_id_image_uri ON trip_images(trip_id, image_uri)")
            }
        }
    }
}