package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.DataProcessing
import kotlinx.coroutines.launch

class DataProcessingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).dataProcessingDao()

    // Perbaikan di sini:
    // 1. Ganti dao.getAll() menjadi dao.getAllDataProcessing()
    // 2. Gunakan .asLiveData() untuk mengonversi Flow ke LiveData
    val allDataProcessing: LiveData<List<DataProcessing>> = dao.getAllDataProcessing().asLiveData()

    fun insert(data: DataProcessing) = viewModelScope.launch {
        dao.insert(data)
    }

    fun update(data: DataProcessing) = viewModelScope.launch {
        dao.update(data)
    }

    fun delete(data: DataProcessing) = viewModelScope.launch {
        dao.delete(data)
    }

    // Factory untuk instansiasi ViewModel tanpa Hilt
    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DataProcessingViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return DataProcessingViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}