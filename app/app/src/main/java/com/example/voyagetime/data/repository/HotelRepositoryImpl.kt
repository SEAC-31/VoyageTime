package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.data.remote.HotelApiService
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.ReserveRequest
import com.example.voyagetime.di.NetworkModule
import com.example.voyagetime.domain.repository.HotelRepository
import com.google.gson.Gson
import com.google.gson.JsonElement

class HotelRepositoryImpl(
    private val apiService: HotelApiService
) : HotelRepository {

    private val gson = Gson()

    override suspend fun searchAvailableHotels(
        city: String,
        startDate: String,
        endDate: String
    ): Result<List<HotelDto>> = try {
        val response = apiService.checkAvailability(
            groupId = NetworkModule.GROUP_ID,
            startDate = startDate,
            endDate = endDate,
            city = city
        )

        if (response.isSuccessful) {
            Result.success(parseHotels(response.body()))
        } else {
            Result.failure(IllegalStateException("Error ${response.code()}: ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "searchAvailableHotels failed", e)
        Result.failure(e)
    }


    override suspend fun reserveRoom(request: ReserveRequest): Result<JsonElement?> = try {
        val response = apiService.reserveRoom(NetworkModule.GROUP_ID, request)
        if (response.isSuccessful) {
            Result.success(response.body())
        } else {
            Result.failure(IllegalStateException("Booking failed: ${response.code()} ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "reserveRoom failed", e)
        Result.failure(e)
    }

    override suspend fun cancelReservation(request: ReserveRequest): Result<Unit> = try {
        val response = apiService.cancelReservation(NetworkModule.GROUP_ID, request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Cancel failed: ${response.code()} ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "cancelReservation failed", e)
        Result.failure(e)
    }

    override suspend fun cancelReservationById(reservationId: String): Result<Unit> = try {
        val response = apiService.cancelReservationById(reservationId)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Cancel failed: ${response.code()} ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "cancelReservationById failed", e)
        Result.failure(e)
    }

    private fun parseHotels(body: JsonElement?): List<HotelDto> {
        if (body == null || body.isJsonNull) return emptyList()

        return try {
            when {
                body.isJsonArray -> gson.fromJson(body, Array<HotelDto>::class.java).toList()
                body.isJsonObject -> {
                    val obj = body.asJsonObject
                    val arrayElement = listOf("hotels", "available_hotels", "data", "results", "items")
                        .firstNotNullOfOrNull { key -> obj.get(key)?.takeIf { it.isJsonArray } }

                    when {
                        arrayElement != null -> gson.fromJson(arrayElement, Array<HotelDto>::class.java).toList()
                        obj.has("rooms") -> listOf(gson.fromJson(obj, HotelDto::class.java))
                        else -> emptyList()
                    }
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "parseHotels: could not parse API response $body", e)
            emptyList()
        }
    }

    companion object {
        private const val TAG = "HotelRepository"
    }
}
