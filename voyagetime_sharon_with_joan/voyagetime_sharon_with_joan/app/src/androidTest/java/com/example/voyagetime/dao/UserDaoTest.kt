package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var db: VoyageTimeDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertUser_andGetById_returnsUser() = runTest {
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        val user = db.userDao().getUserById("uid_1")
        assertNotNull(user)
        assertEquals("alice", user?.username)
    }

    @Test
    fun isUsernameTaken_detectsOtherUserButIgnoresSameUser() = runTest {
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        assertTrue(db.userDao().isUsernameTaken("alice", excludeUid = "uid_2"))
        assertFalse(db.userDao().isUsernameTaken("alice", excludeUid = "uid_1"))
    }

    @Test
    fun updateUser_changesProfileData() = runTest {
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        val user = db.userDao().getUserById("uid_1")!!
        db.userDao().updateUser(user.copy(phone = "123456789"))
        assertEquals("123456789", db.userDao().getUserById("uid_1")?.phone)
    }

    @Test
    fun deleteUser_removesUser() = runTest {
        db.userDao().insertUser(UserEntity(firebaseUid = "uid_1", username = "alice", email = "alice@test.com"))
        db.userDao().deleteUser("uid_1")
        assertNull(db.userDao().getUserById("uid_1"))
    }
}
