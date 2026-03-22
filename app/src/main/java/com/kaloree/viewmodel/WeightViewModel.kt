package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.WeightLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val weightLogDao = KaloreeDatabase.getDatabase(application).weightLogDao()

    val weightLogs: StateFlow<List<WeightLog>> = weightLogDao.getAllWeightLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestWeight: StateFlow<Double?> = weightLogs
        .map { it.firstOrNull()?.weight }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weightChange: StateFlow<Double?> = weightLogs
        .map { logs ->
            if (logs.size >= 2) {
                logs.first().weight - logs.last().weight
            } else null
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAddingWeight = MutableStateFlow(false)
    val isAddingWeight: StateFlow<Boolean> = _isAddingWeight.asStateFlow()

    fun addWeightLog(weight: Double) {
        viewModelScope.launch {
            val weightLog = WeightLog(weight = weight)
            weightLogDao.insertWeightLog(weightLog)
            _isAddingWeight.value = false
        }
    }

    fun deleteWeightLog(weightLog: WeightLog) {
        viewModelScope.launch {
            weightLogDao.deleteWeightLog(weightLog)
        }
    }

    fun showAddWeightDialog() {
        _isAddingWeight.value = true
    }

    fun hideAddWeightDialog() {
        _isAddingWeight.value = false
    }
}
