package com.example.test.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

// --- 1. MODEL
@Serializable
data class Content(
    val id: Int,
    @SerialName("data_created") val dataCreated: String,
    val artikel: String,
    val author: Int
)

// --- 2. RETROFIT SERVICE
interface ContentApiService {
    @GET("content/")
    suspend fun getContents(): List<Content>
}

// --- 3. SINGLETON API OBJECT
object ContentApi {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.24.80.135:8000/api-content/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val service: ContentApiService by lazy {
        retrofit.create(ContentApiService::class.java)
    }
}

// --- 4. UI STATE
sealed interface ContentUiState {
    object Loading : ContentUiState
    data class Success(val contents: List<Content>) : ContentUiState
    data class Error(val message: String) : ContentUiState
}

// --- 5. VIEWMODEL
class ContentViewModel : ViewModel() {
    var uiState by mutableStateOf<ContentUiState>(ContentUiState.Loading)
        private set

    init {
        fetchContents()
    }

    private fun fetchContents() {
        viewModelScope.launch {
            try {
                val contents = ContentApi.service.getContents()
                uiState = ContentUiState.Success(contents)
            } catch (e: Exception) {
                uiState = ContentUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

// --- 6. UI COMPOSABLE
@Composable
fun ContentScreen(viewModel: ContentViewModel = viewModel()) {
    val state = viewModel.uiState

    when (state) {
        is ContentUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ContentUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.message}")
            }
        }

        is ContentUiState.Success -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(state.contents) { content ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Artikel: ${content.artikel}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Author: ${content.author}")
                            Text(text = "Created: ${content.dataCreated}")
                        }
                    }
                }
            }
        }
    }
}
