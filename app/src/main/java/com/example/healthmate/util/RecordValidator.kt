package com.example.healthmate.util

import kotlin.math.roundToInt

/** Which input a validation error belongs to. */
enum class FormField { GENERAL, STEPS, CALORIES, WATER }

sealed class ValidationResult {
    data class Valid(val steps: Int, val calories: Int, val water: Int) : ValidationResult()
    data class Invalid(val field: FormField, val message: String) : ValidationResult()
}

/**
 * Pure form validation (no Android dependencies, so it can be unit tested).
 */
object RecordValidator {

    fun validate(date: String, steps: String, calories: String, water: String): ValidationResult {
        if (date.isBlank() || steps.isBlank() || calories.isBlank() || water.isBlank()) {
            return ValidationResult.Invalid(FormField.GENERAL, "Please fill out all fields")
        }

        val stepsValue = steps.trim().toIntOrNull()
        if (stepsValue == null || stepsValue < 0) {
            return ValidationResult.Invalid(FormField.STEPS, "Enter valid steps")
        }

        val caloriesValue = calories.trim().toDoubleOrNull()
        if (caloriesValue == null || caloriesValue < 0.0 || !caloriesValue.isFinite()) {
            return ValidationResult.Invalid(FormField.CALORIES, "Enter valid calories")
        }

        val waterValue = water.trim().toDoubleOrNull()
        if (waterValue == null || waterValue < 0.0 || !waterValue.isFinite()) {
            return ValidationResult.Invalid(FormField.WATER, "Enter valid water amount")
        }

        // Calories and water are stored as whole numbers (int), decimals typed by the user are rounded.
        return ValidationResult.Valid(stepsValue, caloriesValue.roundToInt(), waterValue.roundToInt())
    }

    /** Goals must be positive whole numbers. Returns null when any value is invalid. */
    fun validateGoals(steps: Int?, calories: Int?, water: Int?): Triple<Int, Int, Int>? {
        if (steps == null || calories == null || water == null) return null
        if (steps <= 0 || calories <= 0 || water <= 0) return null
        return Triple(steps, calories, water)
    }
}
