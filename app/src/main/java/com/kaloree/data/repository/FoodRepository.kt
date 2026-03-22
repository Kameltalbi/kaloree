package com.kaloree.data.repository

import com.kaloree.data.dao.FoodDao
import com.kaloree.data.entity.Food
import com.kaloree.data.remote.OpenFoodFactsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

data class FoodSearchResult(
    val food: Food,
    val isOnline: Boolean = false
)

class FoodRepository(
    private val foodDao: FoodDao,
    private val api: OpenFoodFactsApi = OpenFoodFactsApi.create()
) {
    fun searchLocal(query: String): Flow<List<Food>> =
        if (query.isBlank()) foodDao.getAllFoods() else foodDao.searchFoods(query)

    suspend fun searchWithOnline(query: String): List<FoodSearchResult> {
        val localFoods = searchLocal(query).firstOrNull() ?: emptyList()
        val results = localFoods.map { FoodSearchResult(it, isOnline = false) }.toMutableList()

        if (query.length >= 2) {
            try {
                val response = api.searchFoods(query)
                val onlineFoods = response.products
                    .filter { product ->
                        product.kcalPer100g > 0 && product.displayName != "Produit inconnu"
                    }
                    .map { product ->
                        val servingGrams = product.servingQuantity ?: 100.0
                        val servingLabel = product.servingSize?.takeIf { it.isNotBlank() } ?: "portion"
                        FoodSearchResult(
                            food = Food(
                                id = 0,
                                name = product.displayName,
                                caloriesPer100g = product.kcalPer100g,
                                country = null,
                                portionSize = if (servingGrams > 0) servingGrams else 100.0,
                                portionUnit = servingLabel
                            ),
                            isOnline = true
                        )
                    }
                    .take(15)
                results.addAll(onlineFoods)
            } catch (e: Exception) {
                // Network unavailable - local results only
            }
        }

        return results
    }

    suspend fun saveFood(food: Food): Long = foodDao.insertFood(food)
}
