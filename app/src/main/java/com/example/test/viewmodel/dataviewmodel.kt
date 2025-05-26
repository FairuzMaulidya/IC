package com.example.test.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.model.Content
import com.example.test.network.ContentApi
import kotlinx.coroutines.launch

sealed interface ContentUiState {
    data class Success(val contents: List<Content>) : ContentUiState
    data class Error(val message: String) : ContentUiState
    object Loading : ContentUiState
}

class ContentViewModel : ViewModel() {
    var uiState: ContentUiState = ContentUiState.Loading
        private set

    init {
        getContents()
    }

    private fun getContents() {
        viewModelScope.launch {
            try {
                val result = ContentApi.retrofitService.getContents()
                uiState = ContentUiState.Success(result)
            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
                uiState = ContentUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
