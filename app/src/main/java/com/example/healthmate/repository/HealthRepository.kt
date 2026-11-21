package com.example.healthmate.repository

import android.content.Context
import com.example.healthmate.data.HealthDatabaseHelper
import com.example.healthmate.models.HealthRecord

/**
 * Repository: the single place the ViewModels get health records from.
 * Hides the SQLite implementation from the rest of the app.
 */
class HealthRepository(context: Context) {

    private val database = HealthDatabaseHelper(context.applicationContext)

    fun getAllRecords(): List<HealthRecord> = database.getAllRecords()

    fun search(query: String): List<HealthRecord> =
        if (query.isBlank()) database.getAllRecords() else database.searchRecords(query)

    fun addRecord(record: HealthRecord): Boolean = database.addRecord(record)

    fun updateRecord(record: HealthRecord): Boolean = database.updateRecord(record)

    fun deleteRecord(id: Int) = database.deleteRecord(id)

    fun deleteAllRecords(): Boolean = database.deleteAllRecords()
}
