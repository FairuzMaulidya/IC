package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.DataProcessing
import kotlinx.coroutines.launch

class DataProcessingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataProcessingDao()

    val allDataProcessing: LiveData<List<DataProcessing>> = dao.getAll()

    fun insert(data: DataProcessing) = viewModelScope.launch {
        dao.insert(data)
    }

    fun update(data: DataProcessing) = viewModelScope.launch {
        dao.update(data)
    }

    fun delete(data: DataProcessing) = viewModelScope.launch {
        dao.delete(data)
    }
}
