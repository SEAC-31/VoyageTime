package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Before
    fun setup() {
        runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertLog_andGetLogsForUser_returnsLog() = runTest {
        db.accessLogDao().insertLog(AccessLogEntity(userId = "uid_1", eventType = "LOGIN"))
        val logs = db.accessLogDao().getLogsForUser("uid_1").first()
        assertEquals(1, logs.size)
        assertEquals("LOGIN", logs[0].eventType)
    }

    @Test
    fun getRecentLogs_respectsLimit() = runTest {
        repeat(5) { index ->
            db.accessLogDao().insertLog(
                AccessLogEntity(
                    userId = "uid_1",
                    eventType = "LOGIN",
                    timestamp = LocalDateTime.now().plusMinutes(index.toLong())
                )
            )
        }
        val logs = db.accessLogDao().getRecentLogs("uid_1", limit = 3)
        assertEquals(3, logs.size)
    }

    @Test
    fun clearLogsForUser_removesLogs() = runTest {
        db.accessLogDao().insertLog(AccessLogEntity(userId = "uid_1", eventType = "LOGIN"))
        db.accessLogDao().clearLogsForUser("uid_1")
        assertTrue(db.accessLogDao().getLogsForUser("uid_1").first().isEmpty())
    }
}
