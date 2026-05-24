package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.UserDao
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
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var db: VoyageTimeDatabase
    private lateinit var tripDao: TripDao
    private lateinit var userDao: UserDao

    private val testUser = UserEntity(
        firebaseUid = "uid_test",
        username    = "testuser",
        email       = "test@test.com"
    )

    private val otherUser = UserEntity(
        firebaseUid = "uid_other",
        username    = "otheruser",
        email       = "other@test.com"
    )

    private fun buildTrip(
        userId: String = "uid_test",
        destination: String = "Paris",
        country: String = "France",
        status: String = "UPCOMING",
        startOffset: Long = 1
    ) = TripEntity(
        userId       = userId,
        destination  = destination,
        country      = country,
        startDateTime = LocalDateTime.now().plusDays(startOffset),
        endDateTime  = LocalDateTime.now().plusDays(startOffset + 5),
        durationDays = 5,
        budgetAmount = 1000,
        statusLabel  = status,
        imageRes     = 0
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        tripDao = db.tripDao()
        userDao = db.userDao()
        runBlocking {
            userDao.insertUser(testUser)
            userDao.insertUser(otherUser)
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    // ── Insert & Get ──────────────────────────────────────────────────────────

    @Test
    fun insertTrip_andGetAll_returnsTrip() = runTest {
        tripDao.insertTrip(buildTrip())
        val trips = tripDao.getAllTripsOnce("uid_test")
        assertEquals(1, trips.size)
        assertEquals("Paris", trips[0].destination)
    }

    @Test
    fun insertMultipleTrips_returnsAllForUser() = runTest {
        tripDao.insertTrip(buildTrip(destination = "Paris"))
        tripDao.insertTrip(buildTrip(destination = "Tokyo", country = "Japan"))
        val trips = tripDao.getAllTripsOnce("uid_test")
        assertEquals(2, trips.size)
    }

    // ── Filtrado por usuario ──────────────────────────────────────────────────

    @Test
    fun getTrips_onlyReturnsTripsForRequestedUser() = runTest {
        tripDao.insertTrip(buildTrip(userId = "uid_test",  destination = "Paris"))
        tripDao.insertTrip(buildTrip(userId = "uid_other", destination = "Roma"))
        val trips = tripDao.getAllTripsOnce("uid_test")
        assertEquals(1, trips.size)
        assertEquals("Paris", trips[0].destination)
    }

    @Test
    fun getUpcomingTrips_onlyReturnsUpcomingAndPlanned() = runTest {
        tripDao.insertTrip(buildTrip(status = "UPCOMING"))
        tripDao.insertTrip(buildTrip(status = "PLANNED",   destination = "Tokyo",  country = "Japan"))
        tripDao.insertTrip(buildTrip(status = "COMPLETED", destination = "Berlin", country = "Germany"))
        val upcoming = tripDao.getUpcomingTrips("uid_test").first()
        assertEquals(2, upcoming.size)
        assertTrue(upcoming.none { it.statusLabel == "COMPLETED" })
    }

    @Test
    fun getPastTrips_onlyReturnsCompleted() = runTest {
        tripDao.insertTrip(buildTrip(status = "COMPLETED"))
        tripDao.insertTrip(buildTrip(status = "UPCOMING", destination = "Tokyo", country = "Japan"))
        val past = tripDao.getPastTrips("uid_test").first()
        assertEquals(1, past.size)
        assertEquals("COMPLETED", past[0].statusLabel)
    }

    // ── observeTripById ───────────────────────────────────────────────────────

    @Test
    fun observeTripById_returnsCorrectTrip() = runTest {
        val id = tripDao.insertTrip(buildTrip(destination = "Paris"))
        val trip = tripDao.observeTripById(id, "uid_test").first()
        assertNotNull(trip)
        assertEquals("Paris", trip?.destination)
    }

    @Test
    fun observeTripById_returnsNull_ifWrongUser() = runTest {
        val id = tripDao.insertTrip(buildTrip(userId = "uid_other", destination = "Roma"))
        val trip = tripDao.observeTripById(id, "uid_test").first()
        assertNull(trip)
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    fun updateTrip_changesDestination() = runTest {
        val id = tripDao.insertTrip(buildTrip(destination = "Paris"))
        val inserted = tripDao.getTripById(id, "uid_test")!!
        tripDao.updateTrip(inserted.copy(destination = "Lyon"))
        val updated = tripDao.getTripById(id, "uid_test")
        assertEquals("Lyon", updated?.destination)
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    fun deleteTripById_removesTrip() = runTest {
        val id = tripDao.insertTrip(buildTrip())
        tripDao.deleteTripById(id, "uid_test")
        val trips = tripDao.getAllTripsOnce("uid_test")
        assertTrue(trips.isEmpty())
    }

    @Test
    fun deleteAllTripsForUser_doesNotAffectOtherUsers() = runTest {
        tripDao.insertTrip(buildTrip(userId = "uid_test"))
        tripDao.insertTrip(buildTrip(userId = "uid_other", destination = "Roma"))
        tripDao.deleteAllTripsForUser("uid_test")
        val remaining = tripDao.getAllTripsOnce("uid_other")
        assertEquals(1, remaining.size)
    }

    // ── Orden ─────────────────────────────────────────────────────────────────

    @Test
    fun getAllTrips_orderedByStartDateAsc() = runTest {
        tripDao.insertTrip(buildTrip(destination = "B", startOffset = 10))
        tripDao.insertTrip(buildTrip(destination = "A", startOffset = 2))
        val trips = tripDao.getAllTripsOnce("uid_test")
        assertEquals("A", trips[0].destination)
        assertEquals("B", trips[1].destination)
    }
}