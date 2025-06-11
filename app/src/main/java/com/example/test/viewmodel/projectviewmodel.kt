package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.test.data.AppDatabase
import com.example.test.data.Project
import com.example.test.data.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    // Inisialisasi repository
    private val repository: ProjectRepository

    // Data LiveData dari repository
    val allProjects: LiveData<List<Project>>

    init {
        val projectDao = AppDatabase.getDatabase(application).projectDao()
        repository = ProjectRepository(projectDao)
        allProjects = repository.allProjects
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            repository.insert(project)
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.update(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.delete(project)
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProjectViewModel(application) as T
                }
            }
    }
}
