package com.example.test.data

import androidx.lifecycle.LiveData

class ProjectRepository(private val projectDao: ProjectDao) {

    // Get all projects
    val allProjects: LiveData<List<Project>> = projectDao.getAllProjects()

    // Get all project names (optional)
    val allProjectNames: LiveData<List<String>> = projectDao.getAllProjectNames()

    // Add project
    suspend fun insert(project: Project) {
        projectDao.insertProject(project)
    }

    // Update project
    suspend fun update(project: Project) {
        projectDao.updateProject(project)
    }

    // Delete project
    suspend fun delete(project: Project) {
        projectDao.deleteProject(project)
    }

    // Find project by ID
    fun getProjectById(id: Int): LiveData<Project?> {
        return projectDao.getProjectById(id)
    }
}
