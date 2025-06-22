package com.example.test.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelTrainingDao {
    @Query("SELECT * FROM model_training ORDER BY id DESC")
    fun getAll(): Flow<List<ModelTraining>> // Mengembalikan Flow untuk semua

    @Query("SELECT * FROM model_training WHERE projectName = :projectName ORDER BY id DESC LIMIT 1")
    suspend fun getModelTrainingByProjectName(projectName: String): ModelTraining? // <-- Pastikan ini ada

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: ModelTraining)

    @Update
    suspend fun update(training: ModelTraining)

    @Delete
    suspend fun delete(training: ModelTraining)
}