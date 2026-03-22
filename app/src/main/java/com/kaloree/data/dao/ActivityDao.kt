package com.kaloree.data.dao

import androidx.room.*
import com.kaloree.data.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getActivitiesForDateRange(startDate: Long, endDate: Long): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getActivitiesForDateRangeSync(startDate: Long, endDate: Long): List<Activity>

    @Query("SELECT SUM(calories) FROM activities WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalBurnedCaloriesForDateRange(startDate: Long, endDate: Long): Double?

    @Insert
    suspend fun insertActivity(activity: Activity): Long

    @Update
    suspend fun updateActivity(activity: Activity)

    @Delete
    suspend fun deleteActivity(activity: Activity)
}
