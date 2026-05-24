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
        // Validación username único antes de insertar
        if (userDao.isUsernameTaken(user.username)) {
            throw Exception("Username '${user.username}' is already taken")
        }
        userDao.insertUser(user)
        Log.i(TAG, "User created: uid=${user.firebaseUid} username=${user.username}")
    }

    override suspend fun updateUser(user: UserEntity) {
        // Al editar perfil excluimos al propio usuario de la validación
        if (userDao.isUsernameTaken(user.username, excludeUid = user.firebaseUid)) {
            throw Exception("Username '${user.username}' is already taken")
        }
        userDao.updateUser(user)
        Log.i(TAG, "User updated: uid=${user.firebaseUid}")
    }

    override suspend fun getUserById(uid: String): UserEntity? =
        userDao.getUserById(uid)

    override suspend fun isUsernameTaken(username: String, excludeUid: String): Boolean =
        userDao.isUsernameTaken(username, excludeUid)

    override suspend fun logAccess(userId: String, eventType: String) {
        val log = AccessLogEntity(userId = userId, eventType = eventType.uppercase())
        accessLogDao.insertLog(log)
        Log.i(TAG, "Access logged: userId=$userId event=$eventType")
    }

    companion object {
        private const val TAG = "UserRepository"
        const val EVENT_LOGIN  = "LOGIN"
        const val EVENT_LOGOUT = "LOGOUT"
    }
}