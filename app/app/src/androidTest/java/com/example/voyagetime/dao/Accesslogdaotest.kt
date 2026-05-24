package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.local.entity.AccessLogEntity
import com.example.voyagetime.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class AccessLogDaoTest {

    private lateinit var db: VoyageTimeDatabase
    private lateinit var accessLogDao: AccessLogDao
    private lateinit var userDao: UserDao

    private val testUser = UserEntity(
        firebaseUid = "uid_test",
        username    = "testuser",
        email       = "test@test.com"
    )

    private fun buildLog(
        userId: String = "uid_test",
        eventType: String = "LOGIN",
        timestamp: LocalDateTime = LocalDateTime.now()
    ) = AccessLogEntity(userId = userId, eventType = eventType, timestamp = timestamp)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        accessLogDao = db.accessLogDao()
        userDao = db.userDao()
        runBlocking { userDao.insertUser(testUser) }
    }

    @After
    fun teardown() {
        db.close()
    }

    // ── Insert & Get ──────────────────────────────────────────────────────────

    @Test
    fun insertLog_andGetLogsForUser_returnsLog() = runTest {
        accessLogDao.insertLog(buildLog(eventType = "LOGIN"))
        val logs = accessLogDao.getLogsForUser("uid_test").first()
        assertEquals(1, logs.size)
        assertEquals("LOGIN", logs[0].eventType)
    }

    @Test
    fun insertMultipleLogs_returnsAllInDescOrder() = runTest {
        val base = LocalDateTime.now()
        accessLogDao.insertLog(buildLog(eventType = "LOGIN",  timestamp = base))
        accessLogDao.insertLog(buildLog(eventType = "LOGOUT", timestamp = base.plusMinutes(30)))
        val logs = accessLogDao.getLogsForUser("uid_test").first()
        assertEquals(2, logs.size)
        // orden DESC: el más reciente primero
        assertEquals("LOGOUT", logs[0].eventType)
        assertEquals("LOGIN",  logs[1].eventType)
    }

    // ── getRecentLogs ─────────────────────────────────────────────────────────

    @Test
    fun getRecentLogs_respectsLimit() = runTest {
        repeat(5) { i ->
            accessLogDao.insertLog(buildLog(eventType = "LOGIN", timestamp = LocalDateTime.now().plusMinutes(i.toLong())))
        }
        val recent = accessLogDao.getRecentLogs("uid_test", limit = 3)
        assertEquals(3, recent.size)
    }

    // ── Filtrado por usuario ──────────────────────────────────────────────────

    @Test
    fun getLogsForUser_doesNotReturnOtherUserLogs() = runTest {
        val otherUser = UserEntity(firebaseUid = "uid_other", username = "other", email = "o@o.com")
        userDao.insertUser(otherUser)
        accessLogDao.insertLog(buildLog(userId = "uid_test",  eventType = "LOGIN"))
        accessLogDao.insertLog(buildLog(userId = "uid_other", eventType = "LOGIN"))
        val logs = accessLogDao.getLogsForUser("uid_test").first()
        assertEquals(1, logs.size)
        assertEquals("uid_test", logs[0].userId)
    }

    // ── clearLogs ─────────────────────────────────────────────────────────────

    @Test
    fun clearLogsForUser_removesAllLogs() = runTest {
        accessLogDao.insertLog(buildLog(eventType = "LOGIN"))
        accessLogDao.insertLog(buildLog(eventType = "LOGOUT"))
        accessLogDao.clearLogsForUser("uid_test")
        val logs = accessLogDao.getLogsForUser("uid_test").first()
        assertTrue(logs.isEmpty())
    }

    @Test
    fun clearLogsForUser_doesNotAffectOtherUsers() = runTest {
        val otherUser = UserEntity(firebaseUid = "uid_other", username = "other", email = "o@o.com")
        userDao.insertUser(otherUser)
        accessLogDao.insertLog(buildLog(userId = "uid_test",  eventType = "LOGIN"))
        accessLogDao.insertLog(buildLog(userId = "uid_other", eventType = "LOGIN"))
        accessLogDao.clearLogsForUser("uid_test")
        val otherLogs = accessLogDao.getLogsForUser("uid_other").first()
        assertEquals(1, otherLogs.size)
    }

    // ── CASCADE delete ────────────────────────────────────────────────────────

    @Test
    fun deleteUser_cascadeDeletesLogs() = runTest {
        accessLogDao.insertLog(buildLog(eventType = "LOGIN"))
        userDao.deleteUser("uid_test")
        val logs = accessLogDao.getLogsForUser("uid_test").first()
        assertTrue(logs.isEmpty())
    }
}