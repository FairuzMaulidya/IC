package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectName: String,
    val description: String,
    val status: String = "Ongoing",
    val createdBy: String = "Admin",
    val startDate: String,
    val endDate: String,
    val clientName: String,
    val location: String
)
