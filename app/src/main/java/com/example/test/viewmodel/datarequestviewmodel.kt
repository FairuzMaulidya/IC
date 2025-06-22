package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.DatasetRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DatasetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).reqDatasetDao()

    val allReqDatasets: StateFlow<List<DatasetRequest>> = dao.getAllReqDatasets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReqDataset(req: DatasetRequest) {
        viewModelScope.launch {
            dao.insertReqDataset(req)
        }
    }

    fun updateReqDataset(req: DatasetRequest) {
        viewModelScope.launch {
            dao.updateReqDataset(req)
        }
    }

    fun deleteReqDataset(req: DatasetRequest) {
        viewModelScope.launch {
            dao.deleteReqDataset(req)
        }
    }

    suspend fun getReqDatasetById(id: Int): DatasetRequest? {
        return dao.getReqDatasetById(id)
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DatasetViewModel(application) as T
                }
            }
    }
}
