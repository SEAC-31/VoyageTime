package com.example.voyagetime.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.voyagetime.data.local.dao.UserDao
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
    private lateinit var userDao: UserDao

    private fun buildUser(
        uid: String = "uid_1",
        username: String = "alice",
        email: String = "alice@test.com"
    ) = UserEntity(firebaseUid = uid, username = username, email = email)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VoyageTimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    // ── Insert & Get ──────────────────────────────────────────────────────────

    @Test
    fun insertUser_andGetById_returnsUser() = runTest {
        userDao.insertUser(buildUser())
        val user = userDao.getUserById("uid_1")
        assertNotNull(user)
        assertEquals("alice", user?.username)
    }

    @Test
    fun getUserById_returnsNull_ifNotExists() = runTest {
        val user = userDao.getUserById("nonexistent")
        assertNull(user)
    }

    @Test
    fun getUserByUsername_returnsCorrectUser() = runTest {
        userDao.insertUser(buildUser(username = "alice"))
        val user = userDao.getUserByUsername("alice")
        assertNotNull(user)
        assertEquals("uid_1", user?.firebaseUid)
    }

    @Test
    fun getUserByUsername_returnsNull_ifNotExists() = runTest {
        val user = userDao.getUserByUsername("nobody")
        assertNull(user)
    }

    // ── isUsernameTaken ───────────────────────────────────────────────────────

    @Test
    fun isUsernameTaken_returnsFalse_ifUsernameDoesNotExist() = runTest {
        val taken = userDao.isUsernameTaken("alice")
        assertFalse(taken)
    }

    @Test
    fun isUsernameTaken_returnsTrue_ifUsedByAnotherUser() = runTest {
        userDao.insertUser(buildUser(uid = "uid_1", username = "alice"))
        val taken = userDao.isUsernameTaken("alice", excludeUid = "uid_2")
        assertTrue(taken)
    }

    @Test
    fun isUsernameTaken_returnsFalse_ifUsedBySameUser() = runTest {
        userDao.insertUser(buildUser(uid = "uid_1", username = "alice"))
        // el mismo usuario editando su perfil no debe bloquearse
        val taken = userDao.isUsernameTaken("alice", excludeUid = "uid_1")
        assertFalse(taken)
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    fun updateUser_changesEmail() = runTest {
        userDao.insertUser(buildUser())
        val user = userDao.getUserById("uid_1")!!
        userDao.updateUser(user.copy(email = "newemail@test.com"))
        val updated = userDao.getUserById("uid_1")
        assertEquals("newemail@test.com", updated?.email)
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    fun deleteUser_removesUser() = runTest {
        userDao.insertUser(buildUser())
        userDao.deleteUser("uid_1")
        val user = userDao.getUserById("uid_1")
        assertNull(user)
    }

    @Test
    fun deleteUser_doesNotAffectOtherUsers() = runTest {
        userDao.insertUser(buildUser(uid = "uid_1", username = "alice"))
        userDao.insertUser(buildUser(uid = "uid_2", username = "bob", email = "bob@test.com"))
        userDao.deleteUser("uid_1")
        val bob = userDao.getUserById("uid_2")
        assertNotNull(bob)
    }
}