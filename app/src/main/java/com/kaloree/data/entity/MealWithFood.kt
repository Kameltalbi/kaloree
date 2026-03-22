package com.kaloree.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithFood(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "foodId",
        entityColumn = "id"
    )
    val food: Food?
)
