package com.example.test.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReqDatasetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReqDataset(req: DatasetRequest)

    @Query("SELECT * FROM dataset_request ORDER BY id DESC")
    fun getAllReqDatasets(): Flow<List<DatasetRequest>>

    @Query("SELECT * FROM dataset_request WHERE id = :id")
    suspend fun getReqDatasetById(id: Int): DatasetRequest?

    @Update
    suspend fun updateReqDataset(req: DatasetRequest)

    @Delete
    suspend fun deleteReqDataset(req: DatasetRequest)
}
