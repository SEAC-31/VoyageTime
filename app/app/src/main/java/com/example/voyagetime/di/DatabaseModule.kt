package com.example.voyagetime.di

import android.content.Context
import androidx.room.Room
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.TripDao
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
    fun provideDatabase(@ApplicationContext context: Context): VoyageTimeDatabase {
        return Room.databaseBuilder(
            context,
            VoyageTimeDatabase::class.java,
            VoyageTimeDatabase.DATABASE_NAME
        )
            .addMigrations(VoyageTimeDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideTripDao(db: VoyageTimeDatabase): TripDao = db.tripDao()

    @Provides
    fun provideItineraryItemDao(db: VoyageTimeDatabase): ItineraryItemDao = db.itineraryItemDao()

    @Provides
    fun provideUserDao(db: VoyageTimeDatabase): UserDao = db.userDao()

    @Provides
    fun provideAccessLogDao(db: VoyageTimeDatabase): AccessLogDao = db.accessLogDao()
}