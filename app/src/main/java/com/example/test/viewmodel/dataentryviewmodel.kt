import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.AppDatabase
import com.example.test.data.DataEntry
import com.example.test.data.DataEntryRepository
import com.example.test.data.ProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataEntryRepository(AppDatabase.getDatabase(application).dataEntryDao())
    private val projectRepository = ProjectRepository(AppDatabase.getDatabase(application).projectDao())

    val allEntries: StateFlow<List<DataEntry>> = repository.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gunakan LiveData langsung
    val projectList = projectRepository.allProjectNames  // LiveData<List<String>>

    var selectedEntry = mutableStateOf<DataEntry?>(null)

    fun insertEntry(entry: DataEntry) = viewModelScope.launch {
        val generatedId = repository.insert(entry)
        selectedEntry.value = entry.copy(id = generatedId.toInt())
    }

    fun updateEntry(entry: DataEntry) = viewModelScope.launch {
        repository.update(entry)
    }

    fun deleteEntry(entry: DataEntry) = viewModelScope.launch {
        repository.delete(entry)
    }

    fun setSelectedEntry(entry: DataEntry) {
        selectedEntry.value = entry
    }

    suspend fun insertEntryAndGetId(entry: DataEntry): Long {
        return repository.insert(entry)
    }
}
