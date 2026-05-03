package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.data.local.dao.AccessLogDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.entity.AccessLogEntity
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val accessLogDao: AccessLogDao
) : UserRepository {

    override suspend fun createUser(user: UserEntity) {
        if (userDao.isUsernameTaken(user.username, "")) {
            Log.e(TAG, "createUser: username already taken username=${user.username}")
            throw Exception("Username '${user.username}' is already taken")
        }
        userDao.insertUser(user)
        Log.i(TAG, "User created: uid=${user.firebaseUid} username=${user.username}")
    }

    override suspend fun updateUser(user: UserEntity) {
        if (userDao.isUsernameTaken(user.username, excludeUid = user.firebaseUid)) {
            Log.e(TAG, "updateUser: username already taken username=${user.username}")
            throw Exception("Username '${user.username}' is already taken")
        }
        userDao.updateUser(user)
        Log.i(TAG, "User updated: uid=${user.firebaseUid}")
    }

    override suspend fun getUserById(uid: String): UserEntity? {
        return userDao.getUserById(uid)
    }

    override suspend fun isUsernameTaken(username: String, excludeUid: String): Boolean {
        return userDao.isUsernameTaken(username.trim(), excludeUid)
    }

    override suspend fun logAccess(userId: String, eventType: String) {
        try {
            val existingUser = userDao.getUserById(userId)
            if (existingUser == null) {
                Log.w(TAG, "Access log skipped because local user does not exist yet: userId=$userId event=$eventType")
                return
            }

            val log = AccessLogEntity(
                userId = userId,
                eventType = eventType.uppercase()
            )
            accessLogDao.insertLog(log)
            Log.i(TAG, "Access logged: userId=$userId event=$eventType")
        } catch (error: Exception) {
            Log.e(TAG, "Access log failed: userId=$userId event=$eventType", error)
        }
    }

    companion object {
        private const val TAG = "UserRepository"
        const val EVENT_LOGIN = "LOGIN"
        const val EVENT_LOGOUT = "LOGOUT"
    }
}
