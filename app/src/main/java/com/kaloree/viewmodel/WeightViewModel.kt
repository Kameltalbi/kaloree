package com.kaloree.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaloree.data.KaloreeDatabase
import com.kaloree.data.entity.User
import com.kaloree.data.entity.WeightLog
import com.kaloree.util.CalorieCalculator
import com.kaloree.data.entity.Goal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class WeightProgress(
    val currentWeight: Double,
    val startWeight: Double,
    val targetWeight: Double,
    val progressPercent: Float,
    val remainingKg: Double,
    val weeklyTrend: Double?,
    val estimatedDaysLeft: Int?,
    val isOnTrack: Boolean,
    val durationMonths: Int?
)

class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KaloreeDatabase.getDatabase(application)
    private val weightLogDao = db.weightLogDao()
    private val userDao = db.userDao()

    val user: StateFlow<User?> = userDao.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weightLogs: StateFlow<List<WeightLog>> = weightLogDao.getAllWeightLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestWeight: StateFlow<Double?> = weightLogs
        .map { it.firstOrNull()?.weight }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyTrend: StateFlow<Double?> = weightLogs.map { logs ->
        if (logs.size < 2) return@map null
        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val recent = logs.firstOrNull()?.weight ?: return@map null
        val weekAgo = logs.firstOrNull { it.date <= sevenDaysAgo }?.weight
            ?: logs.lastOrNull()?.weight
            ?: return@map null
        recent - weekAgo
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weightProgress: StateFlow<WeightProgress?> = combine(user, weightLogs, weeklyTrend) { u, logs, trend ->
        val target = u?.targetWeight ?: return@combine null
        val current = logs.firstOrNull()?.weight ?: u.weight
        val start = u.weight
        val totalToLose = kotlin.math.abs(start - target)
        val done = kotlin.math.abs(start - current)
        val percent = if (totalToLose > 0) (done / totalToLose * 100).coerceIn(0.0, 100.0).toFloat() else 100f
        val remaining = target - current
        val daysLeft: Int? = if (trend != null && trend != 0.0) {
            val daysPerKg = 7.0 / kotlin.math.abs(trend)
            (kotlin.math.abs(remaining) * daysPerKg).toInt().takeIf { it > 0 }
        } else null
        val expectedDailyChange = if (u.durationMonths != null && u.durationMonths > 0) {
            totalToLose / (u.durationMonths * 30.0)
        } else null
        val isOnTrack = if (trend != null && expectedDailyChange != null) {
            val actualDaily = kotlin.math.abs(trend) / 7.0
            actualDaily >= expectedDailyChange * 0.7
        } else true
        WeightProgress(
            currentWeight = current,
            startWeight = start,
            targetWeight = target,
            progressPercent = percent,
            remainingKg = remaining,
            weeklyTrend = trend,
            estimatedDaysLeft = daysLeft,
            isOnTrack = isOnTrack,
            durationMonths = u.durationMonths
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAddingWeight = MutableStateFlow(false)
    val isAddingWeight: StateFlow<Boolean> = _isAddingWeight.asStateFlow()

    private val _isEditingGoal = MutableStateFlow(false)
    val isEditingGoal: StateFlow<Boolean> = _isEditingGoal.asStateFlow()

    fun addWeightLog(weight: Double) {
        viewModelScope.launch {
            weightLogDao.insertWeightLog(WeightLog(weight = weight))
            _isAddingWeight.value = false
        }
    }

    fun deleteWeightLog(weightLog: WeightLog) {
        viewModelScope.launch { weightLogDao.deleteWeightLog(weightLog) }
    }

    fun updateGoal(targetWeight: Double, durationMonths: Int) {
        viewModelScope.launch {
            val u = user.value ?: return@launch
            val isMale = u.gender.equals("M", ignoreCase = true)
            val bmr = CalorieCalculator.calculateBMR(u.weight, u.height, u.age, isMale)
            val (newTarget, newDeficit) = CalorieCalculator.calculateDailyCaloriesWithTarget(
                u.weight, targetWeight, durationMonths, bmr, u.activityLevel
            )
            userDao.updateUser(
                u.copy(
                    targetWeight = targetWeight,
                    durationMonths = durationMonths,
                    calorieTarget = newTarget,
                    dailyDeficit = newDeficit
                )
            )
            _isEditingGoal.value = false
        }
    }

    fun showAddWeightDialog() { _isAddingWeight.value = true }
    fun hideAddWeightDialog() { _isAddingWeight.value = false }
    fun showEditGoalDialog() { _isEditingGoal.value = true }
    fun hideEditGoalDialog() { _isEditingGoal.value = false }
}
