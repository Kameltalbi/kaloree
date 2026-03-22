package com.kaloree.data.dao

import androidx.room.*
import com.kaloree.data.entity.Food
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name ASC")
    fun getAllFoods(): Flow<List<Food>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<Food>>

    @Insert
    suspend fun insertFood(food: Food): Long

    @Insert
    suspend fun insertFoods(foods: List<Food>)

    @Update
    suspend fun updateFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: Long): Food?
}
