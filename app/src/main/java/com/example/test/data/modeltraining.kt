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
    val hyperparameters: String,
    val trainingData: String,
    val evaluationMetric: String,
    val performance: String,
    val trainedBy: String,
    val trainingDate: String,
    val modelPath: String,
    val createdDate: String,
    val lastUpdated: String
)
