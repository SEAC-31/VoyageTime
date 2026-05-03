package com.example.voyagetime.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.entity.AccessLogEntity
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.utils.RoomTypeConverters

@Database(
    entities = [
        TripEntity::class,
        ItineraryItemEntity::class,
        UserEntity::class,
        AccessLogEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class VoyageTimeDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao

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

        @Volatile
        private var INSTANCE: VoyageTimeDatabase? = null

        fun getDatabase(context: Context): VoyageTimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoyageTimeDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}