package com.example.healthmate.util

import com.example.healthmate.models.HealthRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Everything the Stats screen shows. [labels] / [values] feed the line graph. */
data class StatsResult(
    val totalRecords: Int,
    val avgSteps: Int,
    val avgCalories: Double,
    val avgWater: Double,
    val subtitle: String,
    val labels: List<String>,
    val values: List<Float>
)

/**
 * Pure statistics logic (no Android dependencies, so it can be unit tested).
 */
object StatsCalculator {

    fun calculate(records: List<HealthRecord>, filter: String, now: Date = Date()): StatsResult {
        if (records.isEmpty()) {
            return StatsResult(0, 0, 0.0, 0.0, "No health records available", emptyList(), emptyList())
        }

        val count = records.size
        val avgSteps = records.sumOf { it.steps } / count
        val avgCalories = records.sumOf { it.calories }.toDouble() / count
        val avgWater = records.sumOf { it.waterMl }.toDouble() / count

        // Calendar field used both to step back in time and to match records to a bucket.
        val field: Int
        val labelPattern: String
        val subtitle: String
        when (filter) {
            "Month" -> {
                field = Calendar.WEEK_OF_YEAR
                labelPattern = "d/M"
                subtitle = "Weekly steps · Last 7 weeks"
            }
            "Year" -> {
                field = Calendar.MONTH
                labelPattern = "MMM"
                subtitle = "Monthly steps · Last 7 months"
            }
            else -> {
                field = Calendar.DAY_OF_YEAR
                labelPattern = "EEE"
                subtitle = "Daily steps · Last 7 days"
            }
        }

        val dated = records.mapNotNull { record ->
            DateUtils.parse(record.date)?.let { it to record.steps }
        }
        val labelFormat = SimpleDateFormat(labelPattern, Locale.ENGLISH)

        val labels = mutableListOf<String>()
        val values = mutableListOf<Float>()

        for (i in 6 downTo 0) {
            val target = Calendar.getInstance()
            target.time = now
            target.add(field, -i)
            val targetYear = target.get(Calendar.YEAR)
            val targetValue = target.get(field)

            labels.add(labelFormat.format(target.time))

            var steps = 0.0
            for ((date, recordSteps) in dated) {
                val recordCal = Calendar.getInstance()
                recordCal.time = date
                if (recordCal.get(Calendar.YEAR) == targetYear && recordCal.get(field) == targetValue) {
                    steps += recordSteps
                }
            }
            values.add(steps.toFloat())
        }

        return StatsResult(count, avgSteps, avgCalories, avgWater, subtitle, labels, values)
    }
}
