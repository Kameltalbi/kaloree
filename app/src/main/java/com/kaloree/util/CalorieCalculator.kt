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
     * Activity multipliers (PAL - Physical Activity Level)
     */
    fun activityMultiplier(activityLevel: String): Double = when (activityLevel) {
        "leger"       -> 1.375  // exercice léger 1-3j/sem
        "modere"      -> 1.55   // exercice modéré 3-5j/sem
        "actif"       -> 1.725  // exercice intense 6-7j/sem
        "tres_actif"  -> 1.9    // sport + travail physique
        else          -> 1.2    // sédentaire (défaut)
    }

    /**
     * Calculate TDEE (Total Daily Energy Expenditure)
     */
    fun calculateTDEE(bmr: Double, activityLevel: String = "sedentaire"): Double =
        bmr * activityMultiplier(activityLevel)

    /**
     * Calculate daily calorie target based on TDEE and goal
     * Uses fixed offset only for MAINTAIN and fallback
     */
    fun calculateDailyCalories(
        bmr: Double,
        goal: Goal,
        activityLevel: String = "sedentaire"
    ): Int {
        val tdee = calculateTDEE(bmr, activityLevel)
        return when (goal) {
            Goal.LOSE_WEIGHT -> (tdee - 500).toInt()
            Goal.MAINTAIN -> tdee.toInt()
            Goal.GAIN_MUSCLE -> (tdee + 300).toInt()
        }
    }

    /**
     * Calculate dynamic daily calorie target based on target weight and duration.
     * Formula:
     *   1 kg fat ≈ 7700 kcal
     *   dailyDeficit = (weightDiff × 7700) / (durationMonths × 30)
     *
     * @param currentWeight in kg
     * @param targetWeight  in kg
     * @param durationMonths duration to reach the goal
     * @param bmr base metabolic rate
     * @return Pair(dailyCalorieTarget, dailyDeficit)
     */
    fun calculateDailyCaloriesWithTarget(
        currentWeight: Double,
        targetWeight: Double,
        durationMonths: Int,
        bmr: Double,
        activityLevel: String = "sedentaire"
    ): Pair<Int, Int> {
        val tdee = calculateTDEE(bmr, activityLevel)
        val weightDiff = kotlin.math.abs(currentWeight - targetWeight)
        val days = durationMonths * 30.0
        val dailyDelta = (weightDiff * 7700.0 / days).toInt()

        val target = if (currentWeight > targetWeight) {
            (tdee - dailyDelta).coerceAtLeast(1200.0).toInt() // min 1200 kcal
        } else {
            (tdee + dailyDelta).toInt()
        }
        return Pair(target, dailyDelta)
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
