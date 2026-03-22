package com.kaloree.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gender: String,
    val age: Int,
    val weight: Double,
    val height: Double,
    val goal: String,
    val calorieTarget: Int,
    val activityLevel: String = "sedentaire",
    val targetWeight: Double? = null,
    val durationMonths: Int? = null,
    val dailyDeficit: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Gender {
    MALE, FEMALE
}

enum class Goal {
    LOSE_WEIGHT, MAINTAIN, GAIN_MUSCLE
}
