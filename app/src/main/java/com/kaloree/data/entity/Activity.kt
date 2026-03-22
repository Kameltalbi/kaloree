package com.kaloree.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val duration: Int,
    val distance: Double? = null,
    val calories: Double,
    val date: Long = System.currentTimeMillis()
)

enum class ActivityType {
    WALKING, RUNNING, SWIMMING, WEIGHTLIFTING, CYCLING, OTHER
}
