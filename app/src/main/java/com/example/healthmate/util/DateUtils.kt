package com.example.healthmate.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Helpers for the text dates stored in the database. The date picker saves
 * "9 Nov 2026"; older formats ("9 November 2026", "Monday, 9 November 2026") are also understood.
 */
object DateUtils {

    private const val RECORD_FORMAT = "d MMM yyyy"
    private val SUPPORTED_FORMATS = listOf("EEEE, d MMMM yyyy", RECORD_FORMAT, "d MMMM yyyy")

    /** Parses a stored date string, returns null if the text is not a valid date. */
    fun parse(text: String): Date? {
        val clean = text.trim()
        for (pattern in SUPPORTED_FORMATS) {
            try {
                val format = SimpleDateFormat(pattern, Locale.ENGLISH)
                format.isLenient = false
                val parsed = format.parse(clean)
                if (parsed != null) return parsed
            } catch (e: Exception) {
                // try the next format
            }
        }
        return null
    }

    /** Formats a date the same way the date picker does. */
    fun format(date: Date): String =
        SimpleDateFormat(RECORD_FORMAT, Locale.ENGLISH).format(date)

    /** Date string for "today minus [days]" days. */
    fun daysAgo(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return format(cal.time)
    }

    fun isToday(text: String, now: Date = Date()): Boolean {
        val parsed = parse(text) ?: return false
        return format(parsed) == format(now)
    }
}
