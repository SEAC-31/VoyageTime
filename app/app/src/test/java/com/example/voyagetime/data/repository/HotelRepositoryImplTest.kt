package com.example.voyagetime.data.repository

import com.example.voyagetime.data.remote.HotelApiService
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HotelRepositoryImplTest {

    @Test
    fun searchAvailableHotels_parsesHotelAndRoomsFromMockedRemoteApi() = runTest {
        val server = MockWebServer()
        try {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        [
                          {
                            "id": "hotel_1",
                            "name": "Demo Barcelona Hotel",
                            "address": "Barcelona centre",
                            "city": "Barcelona",
                            "rating": 4.5,
                            "image_url": "https://example.com/hotel.jpg",
                            "rooms": [
                              {
                                "id": "room_1",
                                "room_type": "Double",
                                "price": 120.0,
                                "images": ["https://example.com/room.jpg"]
                              }
                            ]
                          }
                        ]
                        """.trimIndent()
                    )
            )
            server.start()

            val api = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HotelApiService::class.java)

            val repository = HotelRepositoryImpl(api)
            val result = repository.searchAvailableHotels(
                city = "Barcelona",
                startDate = "2026-06-01",
                endDate = "2026-06-03"
            )

            assertTrue(result.isSuccess)
            val hotels = result.getOrThrow()
            assertEquals(1, hotels.size)
            assertEquals("Demo Barcelona Hotel", hotels.first().name)
            assertEquals(1, hotels.first().rooms.size)
            assertEquals("Double", hotels.first().rooms.first().roomType)
            assertEquals("/hotels/G01/availability?start_date=2026-06-01&end_date=2026-06-03&city=Barcelona", server.takeRequest().path)
        } finally {
            server.shutdown()
        }
    }
}
