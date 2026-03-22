package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.Goal
import com.kaloree.data.entity.User
import com.kaloree.util.CalorieCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = KaloreeDatabase.getDatabase(application).userDao()

    val user: StateFlow<User?> = userDao.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _bmr = MutableStateFlow(0.0)
    val bmr: StateFlow<Double> = _bmr.asStateFlow()

    init {
        viewModelScope.launch {
            user.collect { user ->
                user?.let {
                    val isMale = it.gender.equals("M", ignoreCase = true)
                    _bmr.value = CalorieCalculator.calculateBMR(
                        it.weight, it.height, it.age, isMale
                    )
                }
            }
        }
    }

    fun updateUser(
        age: Int? = null,
        weight: Double? = null,
        height: Double? = null,
        goal: String? = null
    ) {
        viewModelScope.launch {
            user.value?.let { currentUser ->
                val newAge = age ?: currentUser.age
                val newWeight = weight ?: currentUser.weight
                val newHeight = height ?: currentUser.height
                val newGoal = goal ?: currentUser.goal

                val isMale = currentUser.gender.equals("M", ignoreCase = true)
                val goalEnum = when (newGoal.lowercase()) {
                    "perte" -> Goal.LOSE_WEIGHT
                    "maintien" -> Goal.MAINTAIN
                    "prise" -> Goal.GAIN_MUSCLE
                    else -> Goal.MAINTAIN
                }

                val newBmr = CalorieCalculator.calculateBMR(newWeight, newHeight, newAge, isMale)
                val newCalorieTarget = CalorieCalculator.calculateDailyCalories(newBmr, goalEnum)

                val updatedUser = currentUser.copy(
                    age = newAge,
                    weight = newWeight,
                    height = newHeight,
                    goal = newGoal,
                    calorieTarget = newCalorieTarget
                )

                userDao.updateUser(updatedUser)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            userDao.deleteAllUsers()
        }
    }

    fun showEditDialog() {
        _isEditing.value = true
    }

    fun hideEditDialog() {
        _isEditing.value = false
    }
}
