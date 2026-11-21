package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.healthmate.repository.HealthGoals
import com.example.healthmate.repository.HealthRepository
import com.example.healthmate.repository.SettingsRepository
import com.example.healthmate.repository.UnitPreferences
import com.example.healthmate.util.RecordValidator

/**
 * ViewModel for the Settings screen: health goals, unit preferences and clearing all records.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsRepository(application)
    private val records = HealthRepository(application)

    fun getGoals(): HealthGoals = settings.getGoals()

    /** Returns false (and saves nothing) when any goal is missing or not positive. */
    fun saveGoals(steps: Int?, calories: Int?, water: Int?): Boolean {
        val valid = RecordValidator.validateGoals(steps, calories, water) ?: return false
        settings.saveGoals(HealthGoals(valid.first, valid.second, valid.third))
        return true
    }

    fun getUnits(): UnitPreferences = settings.getUnits()

    fun saveUnits(steps: String, calories: String, water: String) {
        settings.saveUnits(UnitPreferences(steps, calories, water))
    }

    /** Deletes every record. Returns false when there was nothing to delete. */
    fun clearAllRecords(): Boolean = records.deleteAllRecords()
}
