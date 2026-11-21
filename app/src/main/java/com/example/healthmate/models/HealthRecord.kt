package com.example.healthmate.models

data class HealthRecord(
    val id: Int = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val waterMl: Int
)