package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.Activity
import com.kaloree.data.entity.Meal
import kotlinx.coroutines.flow.*
import java.util.*

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val mealDao = KaloreeDatabase.getDatabase(application).mealDao()
    private val activityDao = KaloreeDatabase.getDatabase(application).activityDao()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis)
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private fun getDayRange(timestamp: Long): Pair<Long, Long> {
        val start = timestamp
        val end = start + 24 * 60 * 60 * 1000
        return Pair(start, end)
    }

    val mealsForSelectedDate: StateFlow<List<Meal>> = _selectedDate
        .flatMapLatest { date ->
            val (start, end) = getDayRange(date)
            mealDao.getMealsForDateRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activitiesForSelectedDate: StateFlow<List<Activity>> = _selectedDate
        .flatMapLatest { date ->
            val (start, end) = getDayRange(date)
            activityDao.getActivitiesForDateRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCaloriesConsumed: StateFlow<Double> = mealsForSelectedDate
        .map { meals -> meals.sumOf { it.totalCalories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCaloriesBurned: StateFlow<Double> = activitiesForSelectedDate
        .map { activities -> activities.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun goToPreviousDay() {
        _selectedDate.value -= 24 * 60 * 60 * 1000
    }

    fun goToNextDay() {
        _selectedDate.value += 24 * 60 * 60 * 1000
    }

    fun goToToday() {
        _selectedDate.value = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
