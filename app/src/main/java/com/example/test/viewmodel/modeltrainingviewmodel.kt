package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.ModelTraining
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
// Removed unused date imports
// import java.text.SimpleDateFormat
// import java.util.Date
// import java.util.Locale

class ModelTrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).modelTrainingDao()

    // StateFlow to observe real-time changes in the list of model trainings
    val modelTrainings: StateFlow<List<ModelTraining>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Function to add a new model training
    fun addTraining(training: ModelTraining) {
        viewModelScope.launch {
            // No date fields to set anymore
            dao.insert(training)
        }
    }

    // Function to update a model training
    fun updateTraining(training: ModelTraining) {
        viewModelScope.launch {
            // No date fields to update anymore
            dao.update(training)
        }
    }

    // Function to delete a model training
    fun deleteTraining(training: ModelTraining) {
        viewModelScope.launch {
            dao.delete(training)
        }
    }

    // Removed getTodayDate() function as it's no longer needed
}