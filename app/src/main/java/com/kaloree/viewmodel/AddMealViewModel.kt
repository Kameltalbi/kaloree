package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.Food
import com.kaloree.data.entity.Meal
import com.kaloree.data.entity.MealType
import com.kaloree.data.repository.FoodRepository
import com.kaloree.data.repository.FoodSearchResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class AddMealViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(KaloreeDatabase.getDatabase(application).foodDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<FoodSearchResult>>(emptyList())
    val searchResults: StateFlow<List<FoodSearchResult>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .collect { query ->
                    _isSearching.value = true
                    _searchResults.value = repository.searchWithOnline(query)
                    _isSearching.value = false
                }
        }
    }

    private val db = KaloreeDatabase.getDatabase(application)
    private val mealDao = db.mealDao()

    private val _selectedFood = MutableStateFlow<Food?>(null)
    val selectedFood: StateFlow<Food?> = _selectedFood.asStateFlow()

    private val _mealType = MutableStateFlow(MealType.DEJEUNER)
    val mealType: StateFlow<MealType> = _mealType.asStateFlow()

    private val _mealSaved = MutableStateFlow(false)
    val mealSaved: StateFlow<Boolean> = _mealSaved.asStateFlow()

    fun setMealType(type: MealType) { _mealType.value = type }

    private val _usePortions = MutableStateFlow(true)
    val usePortions: StateFlow<Boolean> = _usePortions.asStateFlow()

    private val _portionCount = MutableStateFlow(1.0)
    val portionCount: StateFlow<Double> = _portionCount.asStateFlow()

    private val _grams = MutableStateFlow(100.0)
    val grams: StateFlow<Double> = _grams.asStateFlow()

    val effectiveGrams: StateFlow<Double> = combine(_selectedFood, _usePortions, _portionCount, _grams) { food, usePortion, count, g ->
        if (usePortion && food != null) food.portionSize * count else g
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100.0)

    val calculatedCalories: StateFlow<Double> = combine(_selectedFood, effectiveGrams) { food, grams ->
        food?.let { (it.caloriesPer100g * grams) / 100.0 } ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectFood(food: Food) {
        _selectedFood.value = food
        _usePortions.value = true
        _portionCount.value = 1.0
        _grams.value = food.portionSize
    }

    fun setUsePortions(value: Boolean) {
        _usePortions.value = value
        _selectedFood.value?.let { food ->
            if (value) _portionCount.value = 1.0
            else _grams.value = food.portionSize
        }
    }

    fun setPortionCount(count: Double) {
        _portionCount.value = count
    }

    fun setGrams(g: Double) {
        _grams.value = g
    }

    fun saveMeal(onSaved: () -> Unit) {
        val food = _selectedFood.value ?: return
        val calories = calculatedCalories.value
        val grams = effectiveGrams.value
        viewModelScope.launch {
            val savedFoodId = if (food.id == 0L) {
                repository.saveFood(food)
            } else {
                food.id
            }
            mealDao.insertMeal(
                Meal(
                    foodId = savedFoodId,
                    quantity = grams,
                    totalCalories = calories,
                    mealType = _mealType.value.name
                )
            )
            _mealSaved.value = true
            onSaved()
        }
    }

    fun clearSelection() {
        _selectedFood.value = null
        _usePortions.value = true
        _portionCount.value = 1.0
        _grams.value = 100.0
    }
}
