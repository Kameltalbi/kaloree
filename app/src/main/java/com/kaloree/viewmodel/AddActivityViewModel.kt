package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.ActivityType
import com.kaloree.util.CalorieCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = KaloreeDatabase.getDatabase(application).userDao()

    private val _selectedType = MutableStateFlow(ActivityType.WALKING.name)
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _duration = MutableStateFlow(30)
    val duration: StateFlow<Int> = _duration.asStateFlow()

    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance.asStateFlow()

    private val _manualCalories = MutableStateFlow<Double?>(null)
    val manualCalories: StateFlow<Double?> = _manualCalories.asStateFlow()

    private val _useManualCalories = MutableStateFlow(false)
    val useManualCalories: StateFlow<Boolean> = _useManualCalories.asStateFlow()

    val userWeight: StateFlow<Double> = userDao.getUser()
        .map { it?.weight ?: 70.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 70.0)

    val calculatedCalories: StateFlow<Double> = combine(
        _selectedType,
        _duration,
        userWeight,
        _manualCalories,
        _useManualCalories
    ) { type, duration, weight, manual, useManual ->
        if (useManual && manual != null) {
            manual
        } else {
            CalorieCalculator.calculateActivityCalories(type, duration, weight)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val activityTypes = ActivityType.entries.map { it.name }

    fun setType(type: String) {
        _selectedType.value = type
    }

    fun setDuration(duration: Int) {
        _duration.value = duration
    }

    fun setDistance(distance: Double?) {
        _distance.value = distance
    }

    fun setManualCalories(calories: Double?) {
        _manualCalories.value = calories
    }

    fun setUseManualCalories(useManual: Boolean) {
        _useManualCalories.value = useManual
    }

    fun reset() {
        _selectedType.value = ActivityType.WALKING.name
        _duration.value = 30
        _distance.value = null
        _manualCalories.value = null
        _useManualCalories.value = false
    }
}
