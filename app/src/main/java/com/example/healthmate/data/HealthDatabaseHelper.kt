package com.example.healthmate.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.healthmate.models.HealthRecord

class HealthDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "healthmate.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "health_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_STEPS = "steps"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_WATER = "water"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_DATE TEXT UNIQUE, " +
                        "$COLUMN_STEPS INTEGER, " +
                        "$COLUMN_CALORIES REAL, " +
                        "$COLUMN_WATER REAL)"
                )

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addRecord(record: HealthRecord): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_DATE, record.date)
            put(COLUMN_STEPS, record.steps)
            put(COLUMN_CALORIES, record.calories)
            put(COLUMN_WATER, record.waterMl)
        }

        val result = db.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        return result != -1L
    }

    fun updateRecord(record: HealthRecord): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_DATE, record.date)
            put(COLUMN_STEPS, record.steps)
            put(COLUMN_CALORIES, record.calories)
            put(COLUMN_WATER, record.waterMl)
        }

        val rows = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(record.id.toString())
        )

        return rows > 0
    }

    fun deleteRecord(id: Int) {
        val db = writableDatabase

        db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Delete all health records
    fun deleteAllRecords(): Boolean {
        val db = writableDatabase

        return try {
            db.delete(TABLE_NAME, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun getAllRecords(): List<HealthRecord> {
        val records = mutableListOf<HealthRecord>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COLUMN_ID)
                )

                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_DATE)
                )

                val steps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COLUMN_STEPS)
                )

                val calories = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_CALORIES)
                )

                val water = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_WATER)
                )

                records.add(
                    HealthRecord(
                        id,
                        date,
                        steps,
                        calories,
                        water
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        return records
    }

    fun searchRecords(query: String): List<HealthRecord> {
        val records = mutableListOf<HealthRecord>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE LIKE ? ORDER BY $COLUMN_ID DESC",
            arrayOf("%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COLUMN_ID)
                )

                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_DATE)
                )

                val steps = cursor.getInt(
                    cursor.getColumnIndexOrThrow(COLUMN_STEPS)
                )

                val calories = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_CALORIES)
                )

                val water = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(COLUMN_WATER)
                )

                records.add(
                    HealthRecord(
                        id,
                        date,
                        steps,
                        calories,
                        water
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        return records
    }
}
