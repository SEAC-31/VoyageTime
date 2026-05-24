package com.example.voyagetime.di

import android.content.Context
import androidx.room.Room
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.TripImageDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VoyageTimeDatabase =
        Room.databaseBuilder(
            context,
            VoyageTimeDatabase::class.java,
            VoyageTimeDatabase.DATABASE_NAME
        )
            // During Sprint 04 development the Room schema changed several times.
            // Some devices/emulators can still contain an old, partially migrated
            // voyagetime.db whose tables do not exactly match the current entities.
            // For this academic sprint we prefer a clean local database instead of
            // crashing when opening Trips/Reservations. Room will recreate the DB
            // from the current entities if it detects an incompatible old version.
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideTripDao(db: VoyageTimeDatabase): TripDao = db.tripDao()
    @Provides fun provideItineraryItemDao(db: VoyageTimeDatabase): ItineraryItemDao = db.itineraryItemDao()
    @Provides fun provideUserDao(db: VoyageTimeDatabase): UserDao = db.userDao()
    @Provides fun provideAccessLogDao(db: VoyageTimeDatabase): AccessLogDao = db.accessLogDao()
    @Provides fun provideReservationDao(db: VoyageTimeDatabase): ReservationDao = db.reservationDao()  // T2.3
    @Provides fun provideTripImageDao(db: VoyageTimeDatabase): TripImageDao = db.tripImageDao()  // T3.1/T3.2
}