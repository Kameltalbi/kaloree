package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.*
import com.kaloree.data.entity.MealWithFood
import com.kaloree.util.CalorieCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = KaloreeDatabase.getDatabase(application).userDao()
    private val mealDao = KaloreeDatabase.getDatabase(application).mealDao()
    private val activityDao = KaloreeDatabase.getDatabase(application).activityDao()
    private val weightLogDao = KaloreeDatabase.getDatabase(application).weightLogDao()

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val tomorrow = today + 24 * 60 * 60 * 1000

    val user: StateFlow<User?> = userDao.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val consumedCalories: StateFlow<Double> = mealDao.getMealsForDateRange(today, tomorrow)
        .map { meals -> meals.sumOf { it.totalCalories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val mealsWithFoodByType: StateFlow<Map<MealType, List<MealWithFood>>> =
        mealDao.getMealsWithFoodForDateRange(today, tomorrow)
            .map { list ->
                list.groupBy { MealType.fromName(it.meal.mealType) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val burnedCalories: StateFlow<Double> = activityDao.getActivitiesForDateRange(today, tomorrow)
        .map { activities -> activities.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netCalories: StateFlow<Double> = combine(consumedCalories, burnedCalories) { consumed, burned ->
        consumed - burned
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val remainingCalories: StateFlow<Double> = combine(user, netCalories) { user, net ->
        (user?.calorieTarget ?: 2000) - net
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val calorieProgress: StateFlow<Float> = combine(user, netCalories) { user, net ->
        val target = user?.calorieTarget?.toDouble() ?: 2000.0
        ((net / target) * 100).coerceIn(0.0, 100.0).toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val latestWeight: StateFlow<Double?> = weightLogDao.getAllWeightLogs()
        .map { it.firstOrNull()?.weight }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val statusMessage: StateFlow<String> = combine(user, remainingCalories) { user, remaining ->
        val target = user?.calorieTarget ?: 2000
        CalorieCalculator.getCalorieStatusMessage(remaining, target)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun addMeal(foodId: Long, quantity: Double, caloriesPer100g: Double) {
        viewModelScope.launch {
            val totalCalories = CalorieCalculator.calculateFoodCalories(caloriesPer100g, quantity)
            val meal = Meal(
                foodId = foodId,
                quantity = quantity,
                totalCalories = totalCalories,
                date = System.currentTimeMillis()
            )
            mealDao.insertMeal(meal)
        }
    }

    fun addActivity(type: String, duration: Int, distance: Double?, calories: Double?, weight: Double) {
        viewModelScope.launch {
            val calculatedCalories = calories ?: CalorieCalculator.calculateActivityCalories(type, duration, weight)
            val activity = Activity(
                type = type,
                duration = duration,
                distance = distance,
                calories = calculatedCalories,
                date = System.currentTimeMillis()
            )
            activityDao.insertActivity(activity)
        }
    }

    fun addWeightLog(weight: Double) {
        viewModelScope.launch {
            val weightLog = WeightLog(weight = weight)
            weightLogDao.insertWeightLog(weightLog)
        }
    }
}
