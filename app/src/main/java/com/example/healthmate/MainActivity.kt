package com.example.healthmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.adapters.RecordsAdapter
import com.example.healthmate.data.HealthDatabaseHelper
import com.example.healthmate.models.HealthRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: HealthDatabaseHelper

    private lateinit var tvDate: TextView
    private lateinit var tvStepsCount: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvWater: TextView
    private lateinit var tvGoalPercent: TextView

    private lateinit var progressGoal: CircularProgressIndicator

    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnViewAll: TextView
    private lateinit var btnRefresh: ImageView

    private lateinit var rvRecentRecords: RecyclerView

    private val dailyStepGoal = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BottomNavigationHelper.setActive(findViewById(android.R.id.content), BottomNavigationHelper.Tab.HOME)

        dbHelper = HealthDatabaseHelper(this)

        // Initialize Dashboard Views
        tvDate = findViewById(R.id.tvDate)
        tvStepsCount = findViewById(R.id.tvStepsCount)
        tvCalories = findViewById(R.id.tvCalories)
        tvWater = findViewById(R.id.tvWater)

        // Daily Goal
        tvGoalPercent = findViewById(R.id.tvGoalPercent)
        progressGoal = findViewById(R.id.progressGoal)

        // Other Views
        fabAdd = findViewById(R.id.fabAdd)
        btnViewAll = findViewById(R.id.btnViewAll)
        btnRefresh = findViewById(R.id.btnRefresh)

        rvRecentRecords = findViewById(R.id.rvRecentRecords)

        rvRecentRecords.layoutManager =
            LinearLayoutManager(this)

        //  Date
        val dateFormat = SimpleDateFormat(
            "EEEE, d MMMM yyyy",
            Locale.getDefault()
        )

        tvDate.text = dateFormat.format(Date())

        // 1. Add Record
        findViewById<View>(R.id.btnQuickAdd).setOnClickListener {
            openAddRecord()
        }

        // 2. View Records
        findViewById<View>(R.id.btnQuickRecords).setOnClickListener {
            openRecords()
        }

        // 3. Search Records
        findViewById<View>(R.id.btnQuickSearch).setOnClickListener {
            openRecords()
        }

        // MAIN ADD BUTTON

        fabAdd.setOnClickListener {
            openAddRecord()
        }

        // VIEW ALL RECORDS

        btnViewAll.setOnClickListener {
            openRecords()
        }

        // REFRESH DASHBOARD

        btnRefresh.setOnClickListener {
            loadDashboardData()
        }

        // BOTTOM NAVIGATION

        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener {
            loadDashboardData()
        }

        findViewById<View>(R.id.navRecordsLayout)?.setOnClickListener {
            openRecords()
        }

        findViewById<View>(R.id.navStatsLayout)?.setOnClickListener {
            startActivity(
                Intent(this, StatsActivity::class.java)
            )
        }

        findViewById<View>(R.id.navSettingsLayout)?.setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
        }
    }

    // OPEN ADD RECORD SCREEN

    private fun openAddRecord() {

        val intent = Intent(
            this,
            RecordFormActivity::class.java
        )

        startActivity(intent)
    }

    // OPEN RECORDS SCREEN

    private fun openRecords() {

        val intent = Intent(
            this,
            RecordsListActivity::class.java
        )

        startActivity(intent)
    }

    // REFRESH DASHBOARD

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    // DASHBOARD DATA

    private fun loadDashboardData() {

        val records = dbHelper.getAllRecords()

        if (records.isNotEmpty()) {

            val latest = records[0]

            // Steps
            tvStepsCount.text = latest.steps.toString()

            // Calories
            tvCalories.text = "${latest.calories} kcal"

            // Water
            tvWater.text = "${latest.waterMl} ml"

            // Daily Goal Calculation
            val percentage = (
                    latest.steps.toFloat() / dailyStepGoal * 100
                    ).toInt().coerceIn(0, 100)

            // Update Circular Progress
            tvGoalPercent.text = "$percentage%"

            progressGoal.setProgressCompat(
                percentage,
                true
            )

            // Recent Records
            val recentList = records.take(3)

            val adapter = RecordsAdapter(
                recentList,

                // Edit Record
                onEditClick = { record: HealthRecord ->

                    val intent = Intent(
                        this,
                        RecordFormActivity::class.java
                    )

                    intent.putExtra("RECORD_ID", record.id)
                    intent.putExtra("RECORD_DATE", record.date)
                    intent.putExtra("RECORD_STEPS", record.steps)
                    intent.putExtra("RECORD_CALORIES", record.calories)
                    intent.putExtra("RECORD_WATER", record.waterMl)

                    startActivity(intent)
                },

                // Delete Record
                onDeleteClick = { record: HealthRecord ->

                    AlertDialog.Builder(this)
                        .setTitle("Delete Record")
                        .setMessage(
                            "Are you sure you want to delete this record?"
                        )
                        .setPositiveButton("Yes") { _, _ ->

                            dbHelper.deleteRecord(record.id)

                            loadDashboardData()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            )

            rvRecentRecords.adapter = adapter

        } else {

            // Empty Dashboard
            tvStepsCount.text = "0"
            tvCalories.text = "0 kcal"
            tvWater.text = "0 ml"

            // Reset Daily Goal
            tvGoalPercent.text = "0%"

            progressGoal.setProgressCompat(
                0,
                false
            )

            rvRecentRecords.adapter = null
        }
    }
}
