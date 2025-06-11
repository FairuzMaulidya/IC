package com.example.test.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DataProcessingDao {
    @Query("SELECT * FROM data_processing WHERE projectName = :projectName ORDER BY createdAt DESC")
    fun getByProjectName(projectName: String): LiveData<List<DataProcessing>>

    @Query("SELECT * FROM data_processing ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<DataProcessing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataProcessing)

    @Update
    suspend fun update(data: DataProcessing)

    @Delete
    suspend fun delete(data: DataProcessing)

    @Query("SELECT * FROM data_processing WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): DataProcessing?
}
