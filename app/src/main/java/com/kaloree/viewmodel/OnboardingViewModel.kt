package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.*
import com.kaloree.util.CalorieCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = KaloreeDatabase.getDatabase(application).userDao()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete.asStateFlow()

    val existingUser: StateFlow<User?> = userDao.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveUser(
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goal: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isMale = gender.equals("M", ignoreCase = true)
                val goalEnum = when (goal.lowercase()) {
                    "perte" -> Goal.LOSE_WEIGHT
                    "maintien" -> Goal.MAINTAIN
                    "prise" -> Goal.GAIN_MUSCLE
                    else -> Goal.MAINTAIN
                }

                val bmr = CalorieCalculator.calculateBMR(weight, height, age, isMale)
                val calorieTarget = CalorieCalculator.calculateDailyCalories(bmr, goalEnum)

                // Delete existing user first
                userDao.deleteAllUsers()

                val user = User(
                    gender = gender,
                    age = age,
                    weight = weight,
                    height = height,
                    goal = goal,
                    calorieTarget = calorieTarget
                )
                userDao.insertUser(user)
                _onboardingComplete.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calculatePreviewCalories(
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goal: String
    ): Int {
        val isMale = gender.equals("M", ignoreCase = true)
        val goalEnum = when (goal.lowercase()) {
            "perte" -> Goal.LOSE_WEIGHT
            "maintien" -> Goal.MAINTAIN
            "prise" -> Goal.GAIN_MUSCLE
            else -> Goal.MAINTAIN
        }
        val bmr = CalorieCalculator.calculateBMR(weight, height, age, isMale)
        return CalorieCalculator.calculateDailyCalories(bmr, goalEnum)
    }
}
