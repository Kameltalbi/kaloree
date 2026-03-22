package com.kaloree.data.dao

import androidx.room.*
import com.kaloree.data.entity.WeightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs ORDER BY date DESC")
    fun getAllWeightLogs(): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getWeightLogsForDateRange(startDate: Long, endDate: Long): List<WeightLog>

    @Query("SELECT * FROM weight_logs ORDER BY date DESC LIMIT 1")
    suspend fun getLatestWeightLog(): WeightLog?

    @Insert
    suspend fun insertWeightLog(weightLog: WeightLog): Long

    @Update
    suspend fun updateWeightLog(weightLog: WeightLog)

    @Delete
    suspend fun deleteWeightLog(weightLog: WeightLog)
}
