package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "data_processing")
data class DataProcessing(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectName: String,
    val sourceData: String,
    val cleaningSteps: String,
    val transformationSteps: String,
    val featureEngineering: String,
    val qualityMetrics: String,
    val processedLocation: String, // GANTI dari dataLocation agar konsisten
    val processedBy: String,
    val createdAt: String = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
)
