package com.example.healthmate

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.viewmodel.SettingsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var fabAdd: FloatingActionButton

    private val primaryPurple =
        Color.rgb(91, 75, 196)       // #5B4BC4

    private val darkPurple =
        Color.rgb(68, 56, 168)       // #4438A8

    private val lightPurple =
        Color.rgb(244, 245, 255)     // #F4F5FF

    private val dark =
        Color.rgb(15, 23, 42)

    private val grey =
        Color.rgb(100, 116, 139)

    private val lightGrey =
        Color.rgb(248, 250, 252)

    private val red =
        Color.rgb(220, 38, 38)


    // CREATE

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_settings
        )

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        fabAdd =
            findViewById(R.id.fabAdd)

        setupAddButton()

        setupSettingsOptions()

        setupBottomNavigation()
    }


    // CENTER ADD BUTTON

    private fun setupAddButton() {

        fabAdd.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RecordFormActivity::class.java
                )
            )
        }
    }


    // SETTINGS 

    private fun setupSettingsOptions() {

        // HEALTH GOALS

        findViewById<View>(
            R.id.layoutHealthGoals
        ).setOnClickListener {

            showHealthGoalsDialog()
        }


        // UNITS & PREFERENCES

        findViewById<View>(
            R.id.layoutUnits
        ).setOnClickListener {

            showUnitsDialog()
        }


        // CLEAR ALL RECORDS

        findViewById<View>(
            R.id.layoutClearRecords
        ).setOnClickListener {

            showClearRecordsDialog()
        }
    }


    // BOTTOM NAVIGATION

    private fun setupBottomNavigation() {

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


        // RECORDS

        findViewById<View>(
            R.id.navRecordsLayout
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RecordsListActivity::class.java
                )
            )

            finish()
        }


        // ADD

        findViewById<View>(
            R.id.fabAdd
        )?.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RecordFormActivity::class.java
                )
            )
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

            // Already on Settings.
        }
    }


    // HEALTH GOALS

    private fun showHealthGoalsDialog() {

        val goals = viewModel.getGoals()

        val dialog =
            createDialog()

        val content =
            createDialogContent()


        addDialogHeader(
            content,
            "Set Your Daily Goals",
            "Build healthy habits and track your progress.",
            R.drawable.ic_health_goal,
            "#EAE7FF",
            "#4438A8"
        )


        // DAILY STEPS

        val stepsInput =
            addGoalInput(
                content,
                "Daily Steps",
                "Enter your daily steps target",
                goals.steps.toString(),
                "steps"
            )


        // DAILY CALORIES

        val caloriesInput =
            addGoalInput(
                content,
                "Daily Calories",
                "Enter your calorie target",
                goals.calories.toString(),
                "kcal"
            )


        // DAILY WATER

        val waterInput =
            addGoalInput(
                content,
                "Daily Water",
                "Enter your water target",
                goals.water.toString(),
                "ml"
            )


        val buttons =
            createButtonRow(
                content,
                "Save Goals",
                "Cancel",
                primaryPurple
            )


        // SAVE

        buttons.first.setOnClickListener {

            val steps =
                stepsInput.text
                    .toString()
                    .toIntOrNull()

            val calories =
                caloriesInput.text
                    .toString()
                    .toIntOrNull()

            val water =
                waterInput.text
                    .toString()
                    .toIntOrNull()


            if (!viewModel.saveGoals(steps, calories, water)) {

                Toast.makeText(
                    this,
                    "Please enter valid positive goals",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            Toast.makeText(
                this,
                "Health goals saved successfully",
                Toast.LENGTH_SHORT
            ).show()


            dialog.dismiss()
        }


        // CANCEL

        buttons.second.setOnClickListener {

            dialog.dismiss()
        }


        dialog.setContentView(
            content
        )

        showStyledDialog(
            dialog
        )
    }


    // UNITS & PREFERENCES

    private fun showUnitsDialog() {

        val units = viewModel.getUnits()

        val dialog =
            createDialog()

        val content =
            createDialogContent()


        addDialogHeader(
            content,
            "Units & Preferences",
            "Customize how your health data is displayed.",
            R.drawable.ic_units,
            "#E4EFFF",
            "#36A9E8"
        )


        // STEPS

        val stepsGroup =
            addPreferenceGroup(
                content,
                "Steps",
                listOf(
                    "Steps",
                    "Thousands of steps"
                ),
                if (
                    units.steps == "ksteps"
                ) {
                    1
                } else {
                    0
                }
            )


        // CALORIES

        val caloriesGroup =
            addPreferenceGroup(
                content,
                "Calories",
                listOf(
                    "kcal",
                    "kJ"
                ),
                if (
                    units.calories == "kj"
                ) {
                    1
                } else {
                    0
                }
            )


        // WATER

        val waterGroup =
            addPreferenceGroup(
                content,
                "Water",
                listOf(
                    "Millilitres (ml)",
                    "Litres (L)"
                ),
                if (
                    units.water == "l"
                ) {
                    1
                } else {
                    0
                }
            )

        val buttons =
            createButtonRow(
                content,
                "Save Preferences",
                "Cancel",
                primaryPurple
            )


        // SAVE

        buttons.first.setOnClickListener {

            val stepsValue =
                if (
                    stepsGroup.checkedRadioButtonId ==
                    stepsGroup.getChildAt(1).id
                ) {

                    "ksteps"

                } else {

                    "steps"
                }


            val caloriesValue =
                if (
                    caloriesGroup.checkedRadioButtonId ==
                    caloriesGroup.getChildAt(1).id
                ) {

                    "kj"

                } else {

                    "kcal"
                }


            val waterValue =
                if (
                    waterGroup.checkedRadioButtonId ==
                    waterGroup.getChildAt(1).id
                ) {

                    "l"

                } else {

                    "ml"
                }


            viewModel.saveUnits(
                stepsValue,
                caloriesValue,
                waterValue
            )


            Toast.makeText(
                this,
                "Preferences saved successfully",
                Toast.LENGTH_SHORT
            ).show()


            dialog.dismiss()
        }


        // CANCEL

        buttons.second.setOnClickListener {

            dialog.dismiss()
        }


        dialog.setContentView(
            content
        )

        showStyledDialog(
            dialog
        )
    }


    // CLEAR ALL RECORDS

    private fun showClearRecordsDialog() {

        val dialog =
            createDialog()


        val content =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.CENTER_HORIZONTAL

                setPadding(
                    dp(24),
                    dp(28),
                    dp(24),
                    dp(24)
                )

                background =
                    roundedBackground(
                        Color.WHITE,
                        26
                    )
            }


        // DELETE ICON

        val iconBox =
            FrameLayout(this).apply {

                background =
                    roundedBackground(
                        Color.rgb(
                            254,
                            226,
                            226
                        ),
                        50
                    )
            }


        val icon =
            ImageView(this).apply {

                setImageResource(
                    R.drawable.ic_clear_records
                )

                imageTintList =
                    ColorStateList.valueOf(
                        red
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE

                setPadding(
                    dp(20),
                    dp(20),
                    dp(20),
                    dp(20)
                )
            }


        iconBox.addView(
            icon,
            FrameLayout.LayoutParams(
                dp(76),
                dp(76),
                Gravity.CENTER
            )
        )


        content.addView(
            iconBox,
            LinearLayout.LayoutParams(
                dp(76),
                dp(76)
            )
        )


        // TITLE

        addText(
            content,
            "Delete All Records?",
            21f,
            dark,
            true,
            Gravity.CENTER
        ).apply {

            setPadding(
                0,
                dp(20),
                0,
                dp(8)
            )
        }


        // DESCRIPTION

        addText(
            content,
            "Are you sure you want to remove all your health records? This action cannot be undone.",
            14f,
            grey,
            false,
            Gravity.CENTER
        ).apply {

            setPadding(
                0,
                0,
                0,
                dp(22)
            )
        }


        // WARNING

        val warning =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                background =
                    roundedBackground(
                        Color.rgb(
                            254,
                            242,
                            242
                        ),
                        12
                    )

                setPadding(
                    dp(12),
                    dp(12),
                    dp(12),
                    dp(12)
                )
            }


        val warningIcon =
            ImageView(this).apply {

                setImageResource(
                    R.drawable.ic_warning
                )

                imageTintList =
                    ColorStateList.valueOf(
                        red
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE
            }


        warning.addView(
            warningIcon,
            LinearLayout.LayoutParams(
                dp(28),
                dp(28)
            )
        )


        val warningText =
            TextView(this).apply {

                text =
                    "All saved health records will be permanently deleted."

                textSize =
                    12f

                setTextColor(
                    red
                )

                setPadding(
                    dp(8),
                    0,
                    0,
                    0
                )
            }


        warning.addView(
            warningText,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )


        content.addView(
            warning,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val buttons =
            createButtonRow(
                content,
                "Delete All Records",
                "Cancel",
                primaryPurple
            )


        buttons.first.setOnClickListener {

            val deleted =
                viewModel.clearAllRecords()


            if (deleted) {

                Toast.makeText(
                    this,
                    "All records deleted successfully",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "No records to delete",
                    Toast.LENGTH_SHORT
                ).show()
            }


            dialog.dismiss()
        }


        buttons.second.setOnClickListener {

            dialog.dismiss()
        }


        dialog.setContentView(
            content
        )

        showStyledDialog(
            dialog
        )
    }


    // DIALOG HEADER

    private fun addDialogHeader(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        icon: Int,
        iconBackground: String,
        iconColor: String
    ) {

        val header =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.CENTER
            }


        val iconBox =
            FrameLayout(this).apply {

                background =
                    roundedBackground(
                        Color.parseColor(
                            iconBackground
                        ),
                        50
                    )
            }


        val iconView =
            ImageView(this).apply {

                setImageResource(
                    icon
                )

                imageTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            iconColor
                        )
                    )

                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE

                setPadding(
                    dp(17),
                    dp(17),
                    dp(17),
                    dp(17)
                )
            }


        iconBox.addView(
            iconView,
            FrameLayout.LayoutParams(
                dp(68),
                dp(68),
                Gravity.CENTER
            )
        )


        header.addView(
            iconBox,
            LinearLayout.LayoutParams(
                dp(68),
                dp(68)
            )
        )


        addText(
            header,
            title,
            20f,
            dark,
            true,
            Gravity.CENTER
        ).apply {

            setPadding(
                0,
                dp(16),
                0,
                dp(6)
            )
        }


        addText(
            header,
            subtitle,
            13f,
            grey,
            false,
            Gravity.CENTER
        )


        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.bottomMargin =
            dp(22)


        parent.addView(
            header,
            params
        )
    }


    // GOAL INPUT

    private fun addGoalInput(
        parent: LinearLayout,
        label: String,
        hint: String,
        value: String,
        unit: String
    ): EditText {

        addText(
            parent,
            label,
            13f,
            dark,
            true,
            Gravity.START
        ).apply {

            setPadding(
                0,
                dp(10),
                0,
                dp(8)
            )
        }


        val row =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                background =
                    roundedBackground(
                        lightGrey,
                        13,
                        Color.rgb(
                            226,
                            232,
                            240
                        )
                    )

                setPadding(
                    dp(12),
                    dp(2),
                    dp(12),
                    dp(2)
                )
            }


        val input =
            EditText(this).apply {

                setText(
                    value
                )

                this.hint =
                    hint

                textSize =
                    16f

                setTextColor(
                    dark
                )

                setHintTextColor(
                    Color.LTGRAY
                )

                inputType =
                    InputType.TYPE_CLASS_NUMBER

                setSingleLine(
                    true
                )

                background =
                    null

                setPadding(
                    dp(4),
                    dp(12),
                    dp(4),
                    dp(12)
                )
            }


        row.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )


        val unitText =
            TextView(this).apply {

                text =
                    unit

                textSize =
                    12f

                setTypeface(
                    null,
                    Typeface.BOLD
                )

                setTextColor(
                    grey
                )
            }


        row.addView(
            unitText
        )


        parent.addView(
            row,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )


        return input
    }


    // PREFERENCE GROUP

    private fun addPreferenceGroup(
        parent: LinearLayout,
        title: String,
        options: List<String>,
        selected: Int
    ): RadioGroup {

        addText(
            parent,
            title,
            14f,
            dark,
            true,
            Gravity.START
        ).apply {

            setPadding(
                0,
                dp(12),
                0,
                dp(8)
            )
        }


        val group =
            RadioGroup(this).apply {

                orientation =
                    RadioGroup.VERTICAL

                background =
                    roundedBackground(
                        lightGrey,
                        13
                    )

                setPadding(
                    dp(12),
                    dp(5),
                    dp(12),
                    dp(5)
                )
            }


        options.forEachIndexed {
                index,
                option ->

            val radio =
                RadioButton(this).apply {

                    id =
                        View.generateViewId()

                    text =
                        option

                    textSize =
                        14f

                    setTextColor(
                        dark
                    )

                    buttonTintList =
                        ColorStateList.valueOf(
                            primaryPurple
                        )

                    setPadding(
                        dp(4),
                        dp(5),
                        dp(4),
                        dp(5)
                    )
                }


            group.addView(
                radio,
                RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(44)
                )
            )


            if (index == selected) {

                group.check(
                    radio.id
                )
            }
        }


        parent.addView(
            group,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        return group
    }


    // BUTTON ROW

    private fun createButtonRow(
        parent: LinearLayout,
        positiveText: String,
        negativeText: String,
        positiveColor: Int
    ): Pair<Button, Button> {

        val row =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER
            }


        val cancel =
            createActionButton(
                negativeText,
                Color.rgb(
                    241,
                    245,
                    249
                ),
                grey
            )


        val positive =
            createActionButton(
                positiveText,
                positiveColor,
                Color.WHITE
            )


        row.addView(
            cancel,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                0.85f
            ).apply {

                marginEnd =
                    dp(8)
            }
        )


        row.addView(
            positive,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1.15f
            )
        )


        val params =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )

        params.topMargin =
            dp(24)


        parent.addView(
            row,
            params
        )


        return Pair(
            positive,
            cancel
        )
    }


    // ACTION BUTTON

    private fun createActionButton(
        textValue: String,
        backgroundColor: Int,
        textColor: Int
    ): Button {

        return Button(this).apply {

            text =
                textValue

            textSize =
                12f

            isAllCaps =
                false

            setTypeface(
                null,
                Typeface.BOLD
            )

            setTextColor(
                textColor
            )

            background =
                roundedBackground(
                    backgroundColor,
                    13
                )

            stateListAnimator =
                null

            minHeight =
                0

            minimumHeight =
                0

            minWidth =
                0

            minimumWidth =
                0

            setPadding(
                dp(8),
                0,
                dp(8),
                0
            )
        }
    }


    // DIALOG

    private fun createDialog(): Dialog {

        return Dialog(this).apply {

            requestWindowFeature(
                Window.FEATURE_NO_TITLE
            )

            setCancelable(
                true
            )

            window?.setBackgroundDrawableResource(
                android.R.color.transparent
            )
        }
    }


    private fun createDialogContent():
            LinearLayout {

        return LinearLayout(this).apply {

            orientation =
                LinearLayout.VERTICAL

            setPadding(
                dp(24),
                dp(24),
                dp(24),
                dp(24)
            )

            background =
                roundedBackground(
                    Color.WHITE,
                    24
                )
        }
    }


    private fun showStyledDialog(
        dialog: Dialog
    ) {

        dialog.show()

        dialog.window?.apply {

            setBackgroundDrawableResource(
                android.R.color.transparent
            )

            setLayout(
                (
                        resources
                            .displayMetrics
                            .widthPixels * 0.90
                        ).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setGravity(
                Gravity.CENTER
            )

            addFlags(
                android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )

            attributes =
                attributes.apply {

                    dimAmount =
                        0.55f
                }
        }
    }


    // TEXT HELPER

    private fun addText(
        parent: LinearLayout,
        value: String,
        size: Float,
        color: Int,
        bold: Boolean,
        gravity: Int
    ): TextView {

        val textView =
            TextView(this).apply {

                text =
                    value

                textSize =
                    size

                setTextColor(
                    color
                )

                this.gravity =
                    gravity

                if (bold) {

                    setTypeface(
                        null,
                        Typeface.BOLD
                    )
                }
            }


        parent.addView(
            textView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        return textView
    }


    // ROUNDED BACKGROUND

    private fun roundedBackground(
        color: Int,
        radius: Int,
        strokeColor: Int? = null
    ): GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                color
            )

            cornerRadius =
                dp(radius).toFloat()


            if (strokeColor != null) {

                setStroke(
                    dp(1),
                    strokeColor
                )
            }
        }
    }


    // DP

    private fun dp(
        value: Int
    ): Int {

        return (
                value *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}