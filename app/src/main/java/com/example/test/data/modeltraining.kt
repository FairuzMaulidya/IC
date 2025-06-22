package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_training")
data class ModelTraining(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String,
    val modelName: String,
    val modelType: String,
    val algorithm: String,
    val trainingData: String,
    val performance: String,
    val modelPath: String, // Tetap gunakan nama ini untuk "File Model Terlatih"
    val refinementStrategy: String, // Kolom baru
    val performanceAfterRefinement: String, // Kolom baru
)