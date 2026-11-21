package com.example.healthmate.util

/** Pure helper for the dashboard goal ring. */
object DashboardCalculator {

    /** Steps as a percentage of the daily goal, limited to 0..100. */
    fun goalPercent(steps: Int, goal: Int): Int {
        if (goal <= 0) return 0
        return (steps.toFloat() / goal * 100).toInt().coerceIn(0, 100)
    }
}
