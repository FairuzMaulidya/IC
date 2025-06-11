package com.example.test.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProjectDao {

    @Query("SELECT * FROM project ORDER BY id DESC")
    fun getAllProjects(): LiveData<List<Project>>

    @Query("SELECT ProjectName FROM project")
    fun getAllProjectNames(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT * FROM project WHERE id = :id")
    fun getProjectById(id: Int): LiveData<Project?>
}
