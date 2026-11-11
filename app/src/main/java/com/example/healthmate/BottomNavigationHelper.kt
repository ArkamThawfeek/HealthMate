package com.example.healthmate

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView


object BottomNavigationHelper {

    enum class Tab {
        HOME,
        RECORDS,
        INSIGHTS,
        PROFILE
    }

    private val activeColor =
        Color.parseColor("#5146B8")

    private val inactiveColor =
        Color.parseColor("#9299B2")

    fun setActive(
        root: View,
        activeTab: Tab
    ) {

        val items = listOf(

            Triple(
                R.id.navHomeLayout,
                R.id.ivNavHome,
                R.id.tvNavHome
            ) to Tab.HOME,

            Triple(
                R.id.navRecordsLayout,
                R.id.ivNavRecords,
                R.id.tvNavRecords
            ) to Tab.RECORDS,

            Triple(
                R.id.navStatsLayout,
                R.id.ivNavStats,
                R.id.tvNavStats
            ) to Tab.INSIGHTS,

            Triple(
                R.id.navSettingsLayout,
                R.id.ivNavSettings,
                R.id.tvNavSettings
            ) to Tab.PROFILE
        )

        items.forEach { (ids, tab) ->

            val (_, iconId, labelId) = ids

            val isActive =
                tab == activeTab

            root.findViewById<ImageView>(
                iconId
            )?.imageTintList =
                ColorStateList.valueOf(
                    if (isActive) {
                        activeColor
                    } else {
                        inactiveColor
                    }
                )

            root.findViewById<TextView>(
                labelId
            )?.setTextColor(
                inactiveColor
            )
        }
    }
}