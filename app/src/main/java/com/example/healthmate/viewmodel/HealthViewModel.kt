package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthmate.models.HealthRecord
import com.example.healthmate.repository.HealthRepository
import com.example.healthmate.repository.SettingsRepository
import com.example.healthmate.util.DashboardCalculator
import com.example.healthmate.util.DateUtils
import com.example.healthmate.util.RecordValidator
import com.example.healthmate.util.StatsCalculator
import com.example.healthmate.util.StatsResult
import com.example.healthmate.util.ValidationResult
import com.example.healthmate.util.FormField

/** Outcome of a save (add / update) request. [field] tells which input to flag on a validation error. */
data class SaveResult(
    val success: Boolean,
    val message: String,
    val field: FormField? = null,
    val longMessage: Boolean = false
)

/** Everything the Home dashboard shows. [today] is null when nothing is recorded for today. */
data class DashboardState(
    val today: HealthRecord?,
    val goalPercent: Int,
    val recent: List<HealthRecord>,
    val hasRecords: Boolean
)

/**
 * ViewModel: holds the screen state as LiveData and contains the app logic
 * (validation, today's summary, statistics). Activities only observe and display.
 */
class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthRepository(application)
    private val settings = SettingsRepository(application)

    private var searchQuery = ""

    private val _records = MutableLiveData<List<HealthRecord>>(emptyList())
    val records: LiveData<List<HealthRecord>> = _records

    private val _dashboard = MutableLiveData<DashboardState>()
    val dashboard: LiveData<DashboardState> = _dashboard

    private val _stats = MutableLiveData<StatsResult>()
    val stats: LiveData<StatsResult> = _stats

    /** Reloads the record list. Pass a query to change the date search, or null to keep the current one. */
    fun loadRecords(query: String? = null) {
        if (query != null) searchQuery = query
        _records.value = repository.search(searchQuery)
    }

    fun loadDashboard() {
        val all = repository.getAllRecords()
        val today = all.firstOrNull { DateUtils.isToday(it.date) }
        val goal = settings.getGoals().steps
        _dashboard.value = DashboardState(
            today = today,
            goalPercent = DashboardCalculator.goalPercent(today?.steps ?: 0, goal),
            recent = all.take(3),
            hasRecords = all.isNotEmpty()
        )
    }

    fun loadStats(filter: String) {
        _stats.value = StatsCalculator.calculate(repository.getAllRecords(), filter)
    }

    /** [recordId] = -1 adds a new record, otherwise the record with that id is updated. */
    fun saveRecord(
        recordId: Int,
        date: String,
        steps: String,
        calories: String,
        water: String
    ): SaveResult {
        val cleanDate = date.trim()
        val validation = RecordValidator.validate(cleanDate, steps, calories, water)
        if (validation is ValidationResult.Invalid) {
            return SaveResult(false, validation.message, validation.field)
        }
        validation as ValidationResult.Valid

        val record = HealthRecord(
            id = recordId,
            date = cleanDate,
            steps = validation.steps,
            calories = validation.calories,
            waterMl = validation.water
        )

        return if (recordId == -1) {
            if (repository.addRecord(record)) {
                SaveResult(true, "Record saved successfully!")
            } else {
                SaveResult(false, "A record for this date already exists!", longMessage = true)
            }
        } else {
            if (repository.updateRecord(record)) {
                SaveResult(true, "Record updated successfully!")
            } else {
                SaveResult(false, "Failed to update record")
            }
        }
    }

    fun deleteRecord(id: Int) {
        repository.deleteRecord(id)
        loadRecords()
        loadDashboard()
    }
}
