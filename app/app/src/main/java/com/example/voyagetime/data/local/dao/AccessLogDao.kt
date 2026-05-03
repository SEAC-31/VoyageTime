package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.voyagetime.data.local.entity.AccessLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessLogDao {

    @Insert
    suspend fun insertLog(log: AccessLogEntity)

    @Query("SELECT * FROM access_log WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getLogsForUser(userId: String): Flow<List<AccessLogEntity>>

    @Query("SELECT * FROM access_log WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(userId: String, limit: Int = 10): List<AccessLogEntity>

    @Query("DELETE FROM access_log WHERE user_id = :userId")
    suspend fun clearLogsForUser(userId: String)
}