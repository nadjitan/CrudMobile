package com.example.crudmobile.models

import java.text.SimpleDateFormat
import java.util.*

data class IncidentReport(
    val id: Long = Long.MIN_VALUE,
    val reporter: String = "",
    val witnesses: List<String>,
    val incidentType: String,
    val dateTime: Date?,
    val incidentDescription: String = "",
    val location: String = "",
    val createdAt: Date? = SimpleDateFormat(
        "EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
        .parse(
            SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
                .format(Calendar.getInstance().time)
        )
)
