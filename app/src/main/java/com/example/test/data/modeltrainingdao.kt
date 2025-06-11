package com.example.test.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // Import the Update annotation
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelTrainingDao {
    @Query("SELECT * FROM model_training ORDER BY id DESC")
    fun getAll(): Flow<List<ModelTraining>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: ModelTraining)

    @Update // Add the update function
    suspend fun update(training: ModelTraining)

    @Delete
    suspend fun delete(training: ModelTraining)
}