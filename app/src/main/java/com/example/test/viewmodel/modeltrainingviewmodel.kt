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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModelTrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).modelTrainingDao()

    // StateFlow untuk memantau perubahan daftar model training secara real-time
    val modelTrainings: StateFlow<List<ModelTraining>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Fungsi untuk menambahkan model training baru
    fun addTraining(training: ModelTraining) {
        viewModelScope.launch {
            val today = getTodayDate()
            dao.insert(
                training.copy(
                    createdDate = today,
                    lastUpdated = today
                )
            )
        }
    }

    // Fungsi untuk mengupdate model training
    fun updateTraining(training: ModelTraining) {
        viewModelScope.launch {
            val today = getTodayDate()
            dao.update(
                training.copy(
                    lastUpdated = today // Update lastUpdated date
                )
            )
        }
    }

    // Fungsi untuk menghapus model training
    fun deleteTraining(training: ModelTraining) {
        viewModelScope.launch {
            dao.delete(training)
        }
    }

    // Mendapatkan tanggal hari ini dalam format "dd MMM yyyy"
    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(Date())
    }
}