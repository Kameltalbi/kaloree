package com.kaloree.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = Food::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodId: Long,
    val quantity: Double,
    val totalCalories: Double,
    val mealType: String = MealType.DEJEUNER.name,
    val date: Long = System.currentTimeMillis()
)

enum class MealType(val label: String, val emoji: String) {
    PETIT_DEJ("Petit-déjeuner", "🌅"),
    DEJEUNER("Déjeuner", "🍽️"),
    DINER("Dîner", "🌙"),
    COLLATION("Collation", "🍎");

    companion object {
        fun fromName(name: String): MealType =
            values().firstOrNull { it.name == name } ?: DEJEUNER
    }
}
