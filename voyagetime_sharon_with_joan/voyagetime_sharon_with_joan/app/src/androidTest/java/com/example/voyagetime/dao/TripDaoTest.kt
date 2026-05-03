package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TripDaoTest {
    private lateinit var db: VoyageTimeDatabase

    private fun trip(
        userId: String = "uid_1",
        destination: String = "Paris",
        status: String = "PLANNED",
        startOffset: Long = 1
    ) = TripEntity(
        userId = userId,
        destination = destination,
        country = "France",
        startDateTime = LocalDateTime.now().plusDays(startOffset),
        endDateTime = LocalDateTime.now().plusDays(startOffset + 3),
        durationDays = 4,
        budgetAmount = 800,
        statusLabel = status,
        imageRes = 0,
        coverImageUri = "content://test/image"
    )

    @Before
    fun setup() {
        runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_2", username = "bob", email = "bob@test.com"))
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertTrip_andGetAllTripsOnce_returnsTripForUser() = runTest {
        db.tripDao().insertTrip(trip())
        val trips = db.tripDao().getAllTripsOnce("uid_1")
        assertEquals(1, trips.size)
        assertEquals("Paris", trips[0].destination)
    }

    @Test
    fun getAllTrips_filtersByLoggedUser() = runTest {
        db.tripDao().insertTrip(trip(userId = "uid_1", destination = "Paris"))
        db.tripDao().insertTrip(trip(userId = "uid_2", destination = "Rome"))
        val trips = db.tripDao().getAllTripsOnce("uid_1")
        assertEquals(1, trips.size)
        assertEquals("Paris", trips[0].destination)
    }

    @Test
    fun observeTripById_returnsNullForWrongUser() = runTest {
        val id = db.tripDao().insertTrip(trip(userId = "uid_2", destination = "Rome"))
        assertNull(db.tripDao().observeTripById(id, "uid_1").first())
    }

    @Test
    fun updateTrip_changesCoverImageUri() = runTest {
        val id = db.tripDao().insertTrip(trip())
        val inserted = db.tripDao().getTripById(id, "uid_1")!!
        db.tripDao().updateTrip(inserted.copy(coverImageUri = "content://new/image"))
        assertEquals("content://new/image", db.tripDao().getTripById(id, "uid_1")?.coverImageUri)
    }

    @Test
    fun deleteTripById_removesTrip() = runTest {
        val id = db.tripDao().insertTrip(trip())
        db.tripDao().deleteTripById(id, "uid_1")
        assertTrue(db.tripDao().getAllTripsOnce("uid_1").isEmpty())
    }

    @Test
    fun isTripDestinationTakenForUser_detectsDuplicateForSameUserOnly() = runTest {
        db.tripDao().insertTrip(trip(userId = "uid_1", destination = "Paris"))
        assertTrue(db.tripDao().isTripDestinationTakenForUser("uid_1", "paris"))
        assertTrue(!db.tripDao().isTripDestinationTakenForUser("uid_2", "paris"))
    }

    @Test
    fun getUpcomingTrips_returnsPlannedAndUpcoming() = runTest {
        db.tripDao().insertTrip(trip(status = "PLANNED"))
        db.tripDao().insertTrip(trip(destination = "Tokyo", status = "COMPLETED"))
        val upcoming = db.tripDao().getUpcomingTrips("uid_1").first()
        assertEquals(1, upcoming.size)
        assertEquals("PLANNED", upcoming[0].statusLabel)
    }

    @Test
    fun observeTripById_returnsCorrectTrip() = runTest {
        val id = db.tripDao().insertTrip(trip(destination = "Paris"))
        val observed = db.tripDao().observeTripById(id, "uid_1").first()
        assertNotNull(observed)
        assertEquals("Paris", observed?.destination)
    }
}
