package com.example.healthmate.repository

import android.content.Context

data class HealthGoals(val steps: Int, val calories: Int, val water: Int)

data class UnitPreferences(val steps: String, val calories: String, val water: String)

/**
 * Repository for the user's goals and unit preferences (stored in SharedPreferences).
 */
class SettingsRepository(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences("healthmate_settings", Context.MODE_PRIVATE)

    fun getGoals() = HealthGoals(
        steps = prefs.getInt("goal_steps", 10000),
        calories = prefs.getInt("goal_calories", 2000),
        water = prefs.getInt("goal_water", 2000)
    )

    fun saveGoals(goals: HealthGoals) {
        prefs.edit()
            .putInt("goal_steps", goals.steps)
            .putInt("goal_calories", goals.calories)
            .putInt("goal_water", goals.water)
            .apply()
    }

    fun getUnits() = UnitPreferences(
        steps = prefs.getString("unit_steps", "steps") ?: "steps",
        calories = prefs.getString("unit_calories", "kcal") ?: "kcal",
        water = prefs.getString("unit_water", "ml") ?: "ml"
    )

    fun saveUnits(units: UnitPreferences) {
        prefs.edit()
            .putString("unit_steps", units.steps)
            .putString("unit_calories", units.calories)
            .putString("unit_water", units.water)
            .apply()
    }
}
