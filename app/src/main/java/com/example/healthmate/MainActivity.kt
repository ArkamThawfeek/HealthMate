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
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.viewmodel.DashboardState
import com.example.healthmate.viewmodel.HealthViewModel
import com.example.healthmate.models.HealthRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HealthViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BottomNavigationHelper.setActive(findViewById(android.R.id.content), BottomNavigationHelper.Tab.HOME)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]
        viewModel.dashboard.observe(this) { state -> showDashboard(state) }

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
            viewModel.loadDashboard()
        }

        // BOTTOM NAVIGATION

        findViewById<View>(R.id.navHomeLayout)?.setOnClickListener {
            viewModel.loadDashboard()
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
        viewModel.loadDashboard()
    }

    // DASHBOARD DATA (state comes from the ViewModel)

    private fun showDashboard(state: DashboardState) {

        val today = state.today

        // Today's totals (zeros when nothing is recorded for today)
        tvStepsCount.text = (today?.steps ?: 0).toString()
        tvCalories.text = "${today?.calories ?: 0} kcal"
        tvWater.text = "${today?.waterMl ?: 0} ml"

        // Daily goal ring
        tvGoalPercent.text = "${state.goalPercent}%"
        progressGoal.setProgressCompat(state.goalPercent, today != null)

        // Recent records
        if (!state.hasRecords) {
            rvRecentRecords.adapter = null
            return
        }

        rvRecentRecords.adapter = RecordsAdapter(
            state.recent,

            // Edit Record
            onEditClick = { record: HealthRecord ->
                startActivity(RecordFormActivity.editIntent(this, record))
            },

            // Delete Record
            onDeleteClick = { record: HealthRecord ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage(
                        "Are you sure you want to delete this record?"
                    )
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.deleteRecord(record.id)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )
    }
}
