package com.example.healthmate

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView

class HealthMateApplication : Application() {

    private val activeColor = Color.parseColor("#5B4BC4")
    private val defaultColor = Color.parseColor("#8D91AE")

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {

                override fun onActivityResumed(activity: Activity) {
                    updateBottomNavigation(activity)
                }

                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: android.os.Bundle?
                ) {}

                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: android.os.Bundle
                ) {}

                override fun onActivityDestroyed(activity: Activity) {}
            }
        )
    }

    private fun updateBottomNavigation(activity: Activity) {

        val selectedItem = when (activity) {
            is MainActivity -> "home"
            is StatsActivity -> "stats"
            is RecordsListActivity -> "records"
            is SettingsActivity -> "settings"
            else -> return
        }

        updateItem(
            activity,
            R.id.ivNavHome,
            R.id.tvNavHome,
            selectedItem == "home"
        )

        updateItem(
            activity,
            R.id.ivNavStats,
            R.id.tvNavStats,
            selectedItem == "stats"
        )

        updateItem(
            activity,
            R.id.ivNavRecords,
            R.id.tvNavRecords,
            selectedItem == "records"
        )

        updateItem(
            activity,
            R.id.ivNavSettings,
            R.id.tvNavSettings,
            selectedItem == "settings"
        )
    }

    private fun updateItem(
        activity: Activity,
        iconId: Int,
        textId: Int,
        isSelected: Boolean
    ) {
        val color = if (isSelected) activeColor else defaultColor

        activity.findViewById<ImageView>(iconId)
            ?.setColorFilter(color)

        activity.findViewById<TextView>(textId)
            ?.setTextColor(color)

        activity.findViewById<TextView>(textId)
            ?.setTypeface(
                null,
                if (isSelected) android.graphics.Typeface.BOLD
                else android.graphics.Typeface.NORMAL
            )
    }
}
