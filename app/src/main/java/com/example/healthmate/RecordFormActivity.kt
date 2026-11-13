package com.example.healthmate

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.viewmodel.HealthViewModel
import com.example.healthmate.models.HealthRecord
import com.example.healthmate.util.FormField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordFormActivity : AppCompatActivity() {

    companion object {
        /** Intent that opens the form pre-filled for editing [record]. */
        fun editIntent(context: Context, record: HealthRecord): Intent =
            Intent(context, RecordFormActivity::class.java).apply {
                putExtra("RECORD_ID", record.id)
                putExtra("RECORD_DATE", record.date)
                putExtra("RECORD_STEPS", record.steps)
                putExtra("RECORD_CALORIES", record.calories)
                putExtra("RECORD_WATER", record.waterMl)
            }
    }

    private lateinit var viewModel: HealthViewModel

    private lateinit var etDate: EditText
    private lateinit var etSteps: EditText
    private lateinit var etCalories: EditText
    private lateinit var etWater: EditText

    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView
    private lateinit var btnCancel: Button

    private var recordId: Int = -1

    private val dateFormat = SimpleDateFormat(
        "d MMM yyyy",
        Locale.ENGLISH
    )

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_form)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        // Initialize Views
        etDate = findViewById(R.id.etDate)
        etSteps = findViewById(R.id.etSteps)
        etCalories = findViewById(R.id.etCalories)
        etWater = findViewById(R.id.etWater)

        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
        btnCancel = findViewById(R.id.btnCancel)

        // Set Default Date
        etDate.setText(dateFormat.format(calendar.time))

        // Date Picker
        findViewById<android.view.View>(
            R.id.dateClickArea
        ).setOnClickListener {
            showDatePicker()
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        // Back Button
        btnBack.setOnClickListener {
            finish()
        }

        // Cancel Button
        btnCancel.setOnClickListener {
            finish()
        }

        // Check Edit Mode
        if (intent.hasExtra("RECORD_ID")) {

            recordId = intent.getIntExtra(
                "RECORD_ID",
                -1
            )

            val savedDate = intent.getStringExtra(
                "RECORD_DATE"
            ) ?: ""

            etDate.setText(savedDate)

            try {
                val parsedDate = dateFormat.parse(savedDate)
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            } catch (_: Exception) {
            }

            etSteps.setText(
                intent.getIntExtra(
                    "RECORD_STEPS",
                    0
                ).toString()
            )

            etCalories.setText(
                intent.getIntExtra(
                    "RECORD_CALORIES",
                    0
                ).toString()
            )

            etWater.setText(
                intent.getIntExtra(
                    "RECORD_WATER",
                    0
                ).toString()
            )

            btnSave.text = "Update Record"
        }

        // Save / Update
        btnSave.setOnClickListener {
            saveRecord()
        }
    }

    // DATE PICKER

    private fun showDatePicker() {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                calendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                etDate.setText(
                    dateFormat.format(calendar.time)
                )
            },
            year,
            month,
            day
        )

        dialog.show()
    }

    // SAVE RECORD (validation and database work happen in the ViewModel)

    private fun saveRecord() {

        val result = viewModel.saveRecord(
            recordId,
            etDate.text.toString(),
            etSteps.text.toString(),
            etCalories.text.toString(),
            etWater.text.toString()
        )

        if (!result.success && result.field != null && result.field != FormField.GENERAL) {
            // Field specific validation error
            when (result.field) {
                FormField.STEPS -> etSteps.error = result.message
                FormField.CALORIES -> etCalories.error = result.message
                else -> etWater.error = result.message
            }
            return
        }

        Toast.makeText(
            this,
            result.message,
            if (result.longMessage) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()

        if (result.success) finish()
    }
}
