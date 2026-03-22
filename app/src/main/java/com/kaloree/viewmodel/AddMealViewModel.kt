package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.Food
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddMealViewModel(application: Application) : AndroidViewModel(application) {

    private val foodDao = KaloreeDatabase.getDatabase(application).foodDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val foods: StateFlow<List<Food>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                foodDao.getAllFoods()
            } else {
                foodDao.searchFoods(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFood = MutableStateFlow<Food?>(null)
    val selectedFood: StateFlow<Food?> = _selectedFood.asStateFlow()

    private val _quantity = MutableStateFlow(100.0)
    val quantity: StateFlow<Double> = _quantity.asStateFlow()

    val calculatedCalories: StateFlow<Double> = combine(_selectedFood, _quantity) { food, qty ->
        food?.let { (it.caloriesPer100g * qty) / 100.0 } ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFood(food: Food) {
        _selectedFood.value = food
    }

    fun setQuantity(quantity: Double) {
        _quantity.value = quantity
    }

    fun clearSelection() {
        _selectedFood.value = null
        _quantity.value = 100.0
    }
}
