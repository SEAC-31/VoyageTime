package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.voyagetime.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE firebase_uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    /** Devuelve true si el username ya está en uso por otro usuario. */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username AND firebase_uid != :excludeUid")
    suspend fun isUsernameTaken(username: String, excludeUid: String = ""): Boolean

    @Query("DELETE FROM users WHERE firebase_uid = :uid")
    suspend fun deleteUser(uid: String)
}