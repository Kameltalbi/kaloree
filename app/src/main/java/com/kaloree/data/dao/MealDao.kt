package com.kaloree.data.dao

import androidx.room.*
import com.kaloree.data.entity.Meal
import com.kaloree.data.entity.MealWithFood
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getMealsForDateRange(startDate: Long, endDate: Long): Flow<List<Meal>>

    @Transaction
    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY mealType ASC, date ASC")
    fun getMealsWithFoodForDateRange(startDate: Long, endDate: Long): Flow<List<MealWithFood>>

    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getMealsForDateRangeSync(startDate: Long, endDate: Long): List<Meal>

    @Query("SELECT SUM(totalCalories) FROM meals WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCaloriesForDateRange(startDate: Long, endDate: Long): Double?

    @Insert
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)
}
