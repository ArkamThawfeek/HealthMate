package com.example.healthmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.util.StatsResult
import com.example.healthmate.viewmodel.HealthViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class StatsActivity : AppCompatActivity() {

    private lateinit var viewModel: HealthViewModel

    private lateinit var tvAvgSteps: TextView
    private lateinit var tvAvgCalories: TextView
    private lateinit var tvAvgWater: TextView
    private lateinit var tvTotalRecords: TextView
    private lateinit var tvGraphSubtitle: TextView

    private lateinit var lineGraph: LineGraphView

    private lateinit var btnWeek: Button
    private lateinit var btnMonth: Button
    private lateinit var btnYear: Button

    private lateinit var fabAdd: FloatingActionButton

    private var currentFilter = "Week"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        BottomNavigationHelper.setActive(findViewById(android.R.id.content), BottomNavigationHelper.Tab.INSIGHTS)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]
        viewModel.stats.observe(this) { stats -> showStats(stats) }

        // Summary Views
        tvAvgSteps = findViewById(R.id.tvAvgSteps)
        tvAvgCalories = findViewById(R.id.tvAvgCalories)
        tvAvgWater = findViewById(R.id.tvAvgWater)
        tvTotalRecords = findViewById(R.id.tvTotalRecords)

        // Graph
        lineGraph = findViewById(R.id.lineGraph)
        tvGraphSubtitle = findViewById(R.id.tvGraphSubtitle)

        // Filter Buttons
        btnWeek = findViewById(R.id.btnWeek)
        btnMonth = findViewById(R.id.btnMonth)
        btnYear = findViewById(R.id.btnYear)

        // Add Button
        fabAdd = findViewById(R.id.fabAdd)

        // Initial Data
        updateFilterButtons()
        viewModel.loadStats(currentFilter)

        // Week Filter
        btnWeek.setOnClickListener {
            currentFilter = "Week"
            updateFilterButtons()
            viewModel.loadStats(currentFilter)
        }

        // Month Filter
        btnMonth.setOnClickListener {
            currentFilter = "Month"
            updateFilterButtons()
            viewModel.loadStats(currentFilter)
        }

        // Year Filter
        btnYear.setOnClickListener {
            currentFilter = "Year"
            updateFilterButtons()
            viewModel.loadStats(currentFilter)
        }

        // Add Record
        fabAdd.setOnClickListener {
            startActivity(
                Intent(this, RecordFormActivity::class.java)
            )
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        }

        findViewById<View>(R.id.navStatsLayout)?.setOnClickListener {
        }

        findViewById<View>(R.id.navRecordsLayout)?.setOnClickListener {
            startActivity(
                Intent(this, RecordsListActivity::class.java)
            )
            finish()
        }

        findViewById<View>(R.id.navSettingsLayout)?.setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::lineGraph.isInitialized) {
            viewModel.loadStats(currentFilter)
        }
    }

    // FILTER BUTTON DESIGN

    private fun updateFilterButtons() {

        val activeColor = Color.parseColor("#5748BD")
        val inactiveColor = Color.TRANSPARENT
        val inactiveText = Color.parseColor("#64748B")

        val buttons = listOf(
            btnWeek,
            btnMonth,
            btnYear
        )

        buttons.forEach { button ->

            val selected = when (button) {
                btnWeek -> currentFilter == "Week"
                btnMonth -> currentFilter == "Month"
                else -> currentFilter == "Year"
            }

            button.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12 * resources.displayMetrics.density
                setColor(
                    if (selected) activeColor else inactiveColor
                )
            }

            button.setTextColor(
                if (selected) Color.WHITE else inactiveText
            )

            button.isSelected = selected
        }
    }

    // SHOW STATISTICS (numbers are calculated in the ViewModel)

    private fun showStats(stats: StatsResult) {

        tvAvgSteps.text = String.format(Locale.US, "%,d", stats.avgSteps)

        tvAvgCalories.text = String.format(Locale.US, "%.1f kcal", stats.avgCalories)

        tvAvgWater.text = String.format(Locale.US, "%.1f ml", stats.avgWater)

        tvTotalRecords.text = stats.totalRecords.toString()

        tvGraphSubtitle.text = stats.subtitle

        lineGraph.setData(
            stats.labels,
            stats.values
        )
    }
}
