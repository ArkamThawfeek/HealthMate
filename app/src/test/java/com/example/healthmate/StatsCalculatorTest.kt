package com.example.healthmate

import com.example.healthmate.models.HealthRecord
import com.example.healthmate.util.DashboardCalculator
import com.example.healthmate.util.DateUtils
import com.example.healthmate.util.StatsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class StatsCalculatorTest {

    private val now = Calendar.getInstance().apply { set(2026, Calendar.NOVEMBER, 9, 12, 0, 0) }.time

    private val records = listOf(
        HealthRecord(1, "9 Nov 2026", 8000, 400, 2000),
        HealthRecord(2, "8 Nov 2026", 6000, 200, 1000)
    )

    @Test
    fun averagesAreCalculated() {
        val stats = StatsCalculator.calculate(records, "Week", now)
        assertEquals(2, stats.totalRecords)
        assertEquals(7000, stats.avgSteps)
        assertEquals(300.0, stats.avgCalories, 0.001)
        assertEquals(1500.0, stats.avgWater, 0.001)
    }

    @Test
    fun weekView_hasSevenDays_andTodayIsLast() {
        val stats = StatsCalculator.calculate(records, "Week", now)
        assertEquals(7, stats.values.size)
        assertEquals(8000f, stats.values[6], 0.001f) // today
        assertEquals(6000f, stats.values[5], 0.001f) // yesterday
        assertEquals(0f, stats.values[0], 0.001f)
    }

    @Test
    fun noRecords_returnsEmptyState() {
        val stats = StatsCalculator.calculate(emptyList(), "Week", now)
        assertEquals(0, stats.totalRecords)
        assertTrue(stats.values.isEmpty())
        assertEquals("No health records available", stats.subtitle)
    }

    @Test
    fun goalPercent_isCappedAt100() {
        assertEquals(50, DashboardCalculator.goalPercent(5000, 10000))
        assertEquals(100, DashboardCalculator.goalPercent(15000, 10000))
        assertEquals(0, DashboardCalculator.goalPercent(500, 0))
    }

    @Test
    fun dateUtils_understandsPickerAndLongFormats() {
        assertTrue(DateUtils.isToday("9 Nov 2026", now))
        assertTrue(DateUtils.isToday("Monday, 9 November 2026", now))
        assertFalse(DateUtils.isToday("8 Nov 2026", now))
        assertNull(DateUtils.parse("not a date"))
    }
}
