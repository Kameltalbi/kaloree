package com.kaloree.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val caloriesPer100g: Double,
    val country: String? = null,
    val portionSize: Double = 100.0,
    val portionUnit: String = "portion",
    val isCustom: Boolean = false
)
