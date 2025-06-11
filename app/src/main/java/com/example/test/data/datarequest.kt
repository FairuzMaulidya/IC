package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "dataset_request")
data class DatasetRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String,
    val description: String,
    val featureCount: Int,
    val datasetSize: String,
    val expectedFileFormat: String,
    val descriptionn: String,
    val dataType: String,
    val dataProcessing: String,
    val startDate: String,
    val target: String,
    val requestedBy: String,
    val status: String,
    val endDate: String
)
