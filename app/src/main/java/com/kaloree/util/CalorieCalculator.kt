package com.kaloree.util

import com.kaloree.data.entity.Goal

object CalorieCalculator {

    /**
     * Calculate BMR (Basal Metabolic Rate) using Mifflin-St Jeor Equation
     * BMR = 10 × poids + 6.25 × taille - 5 × âge + s
     * where s = +5 for males, -161 for females
     */
    fun calculateBMR(
        weight: Double,  // in kg
        height: Double,  // in cm
        age: Int,
        isMale: Boolean
    ): Double {
        val s = if (isMale) 5 else -161
        return (10 * weight) + (6.25 * height) - (5 * age) + s
    }

    /**
     * Calculate daily calorie target based on BMR and goal
     * Using sedentary multiplier (1.2) as base
     */
    fun calculateDailyCalories(
        bmr: Double,
        goal: Goal
    ): Int {
        val tdee = bmr * 1.2 // Sedentary multiplier
        return when (goal) {
            Goal.LOSE_WEIGHT -> (tdee - 500).toInt()
            Goal.MAINTAIN -> tdee.toInt()
            Goal.GAIN_MUSCLE -> (tdee + 300).toInt()
        }
    }

    /**
     * Calculate calories for a specific food quantity
     */
    fun calculateFoodCalories(
        caloriesPer100g: Double,
        quantity: Double  // in grams
    ): Double {
        return (caloriesPer100g * quantity) / 100.0
    }

    /**
     * Calculate burned calories for an activity
     * Using MET (Metabolic Equivalent of Task) values
     */
    fun calculateActivityCalories(
        activityType: String,
        duration: Int,  // in minutes
        weight: Double  // in kg
    ): Double {
        val met = when (activityType.lowercase()) {
            "walking" -> 3.5
            "running" -> 9.0
            "swimming" -> 8.0
            "weightlifting" -> 4.0
            "cycling" -> 7.0
            else -> 4.0
        }
        // Calories = MET * weight(kg) * duration(hours)
        return met * weight * (duration / 60.0)
    }

    /**
     * Calculate net calories (consumed - burned)
     */
    fun calculateNetCalories(
        consumed: Double,
        burned: Double
    ): Double {
        return consumed - burned
    }

    /**
     * Get calorie status message
     */
    fun getCalorieStatusMessage(
        remaining: Double,
        target: Int
    ): String {
        return when {
            remaining > target * 0.5 -> "Il te reste ${remaining.toInt()} calories"
            remaining > 0 -> "Encore ${remaining.toInt()} calories"
            remaining == 0.0 -> "Objectif atteint !"
            else -> "Tu es en surplus de ${(-remaining).toInt()} calories"
        }
    }
}
