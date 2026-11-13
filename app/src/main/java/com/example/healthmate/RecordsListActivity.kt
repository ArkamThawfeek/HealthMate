package com.example.healthmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.adapters.RecordsAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.viewmodel.HealthViewModel
import com.example.healthmate.models.HealthRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecordsListActivity : AppCompatActivity() {

    private lateinit var viewModel: HealthViewModel

    private lateinit var rvRecords: RecyclerView
    private lateinit var etSearch: EditText

    private lateinit var adapter: RecordsAdapter

    private lateinit var fabAdd: FloatingActionButton


    // CREATE

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_records_list
        )


        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]


        rvRecords =
            findViewById(
                R.id.rvRecords
            )


        etSearch =
            findViewById(
                R.id.etSearch
            )


        fabAdd =
            findViewById(
                R.id.fabAdd
            )


        rvRecords.layoutManager =
            LinearLayoutManager(this)


        viewModel.records.observe(this) { records -> showRecords(records) }


        // SEARCH

        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }


                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    viewModel.loadRecords(
                        s.toString().trim()
                    )
                }


                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )


        // CENTER ADD BUTTON

        fabAdd.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RecordFormActivity::class.java
                )
            )
        }


        // BOTTOM NAVIGATION


        // HOME

        findViewById<View>(
            R.id.navHomeLayout
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
        }


        // RECORDS - CURRENT SCREEN

        findViewById<View>(
            R.id.navRecordsLayout
        )?.setOnClickListener {

        }


        // INSIGHTS

        findViewById<View>(
            R.id.navStatsLayout
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StatsActivity::class.java
                )
            )

            finish()
        }


        // SETTINGS

        findViewById<View>(
            R.id.navSettingsLayout
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )

            finish()
        }
    }


    // RESUME

    override fun onResume() {

        super.onResume()

        if (::etSearch.isInitialized) {

            viewModel.loadRecords(
                etSearch.text
                    .toString()
                    .trim()
            )
        }
    }


    // SHOW RECORDS (the list comes from the ViewModel)

    private fun showRecords(
        records: List<HealthRecord>
    ) {

        adapter =
            RecordsAdapter(

                records,

                // EDIT

                onEditClick = {
                        record: HealthRecord ->

                    startActivity(
                        RecordFormActivity.editIntent(
                            this,
                            record
                        )
                    )
                },


                // DELETE

                onDeleteClick = {
                        record: HealthRecord ->

                    AlertDialog.Builder(this)

                        .setTitle(
                            "Delete Record"
                        )

                        .setMessage(
                            "Are you sure you want to delete this record?"
                        )

                        .setPositiveButton(
                            "Yes"
                        ) { _, _ ->

                            viewModel.deleteRecord(
                                record.id
                            )
                        }

                        .setNegativeButton(
                            "No",
                            null
                        )

                        .show()
                }
            )


        rvRecords.adapter =
            adapter
    }
}
