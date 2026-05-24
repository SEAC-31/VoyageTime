package com.example.voyagetime.di

import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.remote.HotelApiService
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.data.repository.ItineraryRepositoryImpl
import com.example.voyagetime.data.repository.HotelRepositoryImpl
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.data.repository.UserRepositoryImpl
import com.example.voyagetime.domain.repository.AuthRepository
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.domain.repository.HotelRepository
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = FirebaseAuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        userDao: UserDao,
        authRepository: AuthRepository
    ): TripRepository = TripRepositoryImpl(tripDao, userDao, authRepository)

    @Provides
    @Singleton
    fun provideItineraryRepository(
        itineraryItemDao: ItineraryItemDao
    ): ItineraryRepository = ItineraryRepositoryImpl(itineraryItemDao)


    @Provides
    @Singleton
    fun provideHotelRepository(
        apiService: HotelApiService
    ): HotelRepository = HotelRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        accessLogDao: AccessLogDao,
        authRepository: AuthRepository
    ): UserRepository = UserRepositoryImpl(userDao, accessLogDao)
}