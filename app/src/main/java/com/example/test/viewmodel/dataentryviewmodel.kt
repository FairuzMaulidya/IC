package com.example.test.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.DataEntry
import com.example.test.data.Project
import com.example.test.data.ProjectRepository // Import ProjectRepository
import com.example.test.network.RetrofitClient // Import RetrofitClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataEntryViewModel(application: Application) : AndroidViewModel(application) {
    // Inisialisasi semua DAO yang diperlukan oleh ProjectRepository
    private val appDatabase = AppDatabase.getDatabase(application)
    private val projectDao = appDatabase.projectDao()
    private val dataEntryDao = appDatabase.dataEntryDao()
    private val dataProcessingDao = appDatabase.dataProcessingDao()
    private val modelTrainingDao = appDatabase.modelTrainingDao()
    private val apiService = RetrofitClient.apiService

    // Inisialisasi ProjectRepository
    private val projectRepository = ProjectRepository(
        projectDao, apiService, dataEntryDao, dataProcessingDao, modelTrainingDao
    )

    // Mengambil semua DataEntry melalui ProjectRepository
    val allEntries: StateFlow<List<DataEntry>> = projectRepository.getAllDataEntries() // Menggunakan ProjectRepository
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mengambil semua Projects melalui ProjectRepository (jika diperlukan di sini)
    val allProjects: StateFlow<List<Project>> = projectRepository.getCombinedProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var selectedEntry = mutableStateOf<DataEntry?>(null)

    fun insertEntry(entry: DataEntry) = viewModelScope.launch {
        // Panggil fungsi di ProjectRepository
        val generatedId = projectRepository.insertDataEntry(entry)
        selectedEntry.value = entry.copy(id = generatedId.toInt())
    }

    fun updateEntry(entry: DataEntry) = viewModelScope.launch {
        projectRepository.updateDataEntry(entry)
    }

    fun deleteEntry(entry: DataEntry) = viewModelScope.launch {
        projectRepository.deleteDataEntry(entry)
    }

    fun setSelectedEntry(entry: DataEntry) {
        selectedEntry.value = entry
    }

    suspend fun insertEntryAndGetId(entry: DataEntry): Long {
        return projectRepository.insertDataEntry(entry)
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DataEntryViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return DataEntryViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}