package com.example.test.screen

import android.app.DatePickerDialog
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.test.data.ModelTraining
import com.example.test.data.Project
import com.example.test.viewmodel.ModelTrainingViewModel
import com.example.test.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.foundation.shape.RoundedCornerShape // Added import for RoundedCornerShape
import androidx.compose.ui.Alignment // Added import for Alignment

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TrainModelScreen(
    navController: NavHostController
) {
    val modelTrainingViewModel: ModelTrainingViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()

    val trainings by modelTrainingViewModel.modelTrainings.collectAsState(initial = emptyList())
    val localProjects by projectViewModel.allLocalProjects.observeAsState(emptyList())
    val apiProjects by projectViewModel.apiProjects.observeAsState(emptyList())
    val projectList = remember(localProjects, apiProjects) {
        (localProjects + apiProjects).sortedBy { it.projectName }
    }

    var showForm by remember { mutableStateOf(false) }
    var editingTraining by remember { mutableStateOf<ModelTraining?>(null) }
    var viewingTraining by remember { mutableStateOf<ModelTraining?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog visibility
    var trainingToDelete by remember { mutableStateOf<ModelTraining?>(null) } // State to hold item to delete

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Daftar Pelatihan Model", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                editingTraining = null
                showForm = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28a745)),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Buat Model Training Baru", color = Color.White)
        }

        if (showForm) {
            TrainModelForm(
                allProjects = projectList,
                initialData = editingTraining,
                onSubmit = { submittedTraining ->
                    if (editingTraining != null) {
                        modelTrainingViewModel.updateTraining(submittedTraining)
                    } else {
                        modelTrainingViewModel.addTraining(submittedTraining)
                    }
                    viewingTraining = submittedTraining // Tampilkan detail setelah submit/update
                    showForm = false
                    editingTraining = null
                },
                onCancel = {
                    showForm = false
                    editingTraining = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!showForm) {
            Spacer(modifier = Modifier.height(8.dp))

            if (trainings.isNotEmpty()) {
                val sharedHorizontalScrollState = rememberScrollState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(sharedHorizontalScrollState)
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        Text("No", fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                        Text("Proyek", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        Text("Nama Model", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Performa Model", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Aksi", fontWeight = FontWeight.Bold, modifier = Modifier.width(230.dp), textAlign = TextAlign.End)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = trainings,
                        key = { training -> training.id }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clipToBounds(),
                            elevation = CardDefaults.cardElevation(1.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(sharedHorizontalScrollState)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text("${trainings.indexOf(item) + 1}", modifier = Modifier.width(30.dp))
                                Text(item.projectName, modifier = Modifier.width(100.dp))
                                Text(item.modelName, modifier = Modifier.width(120.dp))
                                Text(item.performance, modifier = Modifier.width(120.dp))

                                Row(
                                    modifier = Modifier.width(230.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { viewingTraining = item },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c757d)),
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(30.dp)
                                    ) {
                                        Text("View", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            editingTraining = item
                                            showForm = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007bff)),
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(30.dp)
                                    ) {
                                        Text("Edit", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            trainingToDelete = item // Set the item to be deleted
                                            showDeleteConfirmationDialog = true // Show the dialog
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFdc3545)),
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(30.dp)
                                    ) {
                                        Text("Delete", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Belum ada pelatihan model.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }

        // Tampilan Dialog untuk View (kini menampilkan semua field yang relevan dari DAO yang baru)
        if (viewingTraining != null) {
            Dialog(onDismissRequest = { viewingTraining = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Judul: "Train Model: [Nama Proyek]"
                        Text(
                            "Detail Pelatihan Model: ${viewingTraining!!.projectName}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Nama Model
                        Text("Nama Model", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.modelName,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Tipe Model
                        Text("Tipe Model", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.modelType,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Algoritma
                        Text("Algoritma", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.algorithm,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Data Latih
                        Text("Data Pelatihan", style = MaterialTheme.typography.bodyLarge) // Disesuaikan dengan form
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.trainingData,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Performa Model
                        Text("Performa Model", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.performance,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // File Model Terlatih (nama label disesuaikan)
                        Text("File Model Terlatih", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.modelPath,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Strategi Penyempurnaan (NEW)
                        Text("Strategi Penyempurnaan", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.refinementStrategy,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                        Spacer(Modifier.height(12.dp))
                        // Performa Setelah Penyempurnaan (NEW)
                        Text("Performa Setelah Penyempurnaan", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewingTraining!!.performanceAfterRefinement,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewingTraining = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007bff)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tutup", color = Color.White)
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmationDialog && trainingToDelete != null) {
            DeleteConfirmationDialog(
                entry = trainingToDelete!!,
                onDeleteConfirm = {
                    trainingToDelete?.let { modelTrainingViewModel.deleteTraining(it) }
                    showDeleteConfirmationDialog = false
                    trainingToDelete = null
                },
                onDismiss = {
                    showDeleteConfirmationDialog = false
                    trainingToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainModelForm(
    allProjects: List<Project>,
    onSubmit: (ModelTraining) -> Unit,
    onCancel: () -> Unit,
    initialData: ModelTraining? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedProject by remember { mutableStateOf(initialData?.projectName ?: "") }
    var expanded by remember { mutableStateOf(false) }

    var modelName by remember { mutableStateOf(initialData?.modelName ?: "") }
    var modelType by remember { mutableStateOf(initialData?.modelType ?: "") }
    var algorithm by remember { mutableStateOf(initialData?.algorithm ?: "") }
    var trainingData by remember { mutableStateOf(initialData?.trainingData ?: "") }
    var performance by remember { mutableStateOf(initialData?.performance ?: "") }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var trainedModelFileDisplayName by remember { mutableStateOf(initialData?.modelPath ?: "") }

    LaunchedEffect(initialData) {
        initialData?.modelPath?.let { path ->
            if (path.isNotEmpty()) {
                try {
                    val uri = Uri.parse(path)
                    selectedFileUri = uri
                    val contentResolver = context.contentResolver
                    val cursor = contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex != -1) {
                                trainedModelFileDisplayName = it.getString(displayNameIndex)
                            } else {
                                trainedModelFileDisplayName = uri.lastPathSegment ?: path
                            }
                        }
                    } ?: run {
                        trainedModelFileDisplayName = path
                    }
                } catch (e: Exception) {
                    trainedModelFileDisplayName = path
                }
            }
        }
    }


    var refinementStrategy by remember { mutableStateOf(initialData?.refinementStrategy ?: "") }
    var performanceAfterRefinement by remember { mutableStateOf(initialData?.performanceAfterRefinement ?: "") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        trainedModelFileDisplayName = c.getString(nameIndex)
                    } else {
                        trainedModelFileDisplayName = it.lastPathSegment ?: it.toString()
                    }
                }
            } ?: run {
                trainedModelFileDisplayName = it.lastPathSegment ?: it.toString()
            }
        }
    }

    Column(
        modifier = modifier
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Form Train Model",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Nama Proyek
        Text("Nama Proyek", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedProject,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allProjects.forEach { project ->
                    DropdownMenuItem(
                        text = { Text(project.projectName) },
                        onClick = {
                            selectedProject = project.projectName
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        // Nama Model
        Text("Nama Model", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = modelName, onValueChange = { modelName = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Tipe Model
        Text("Tipe Model", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = modelType, onValueChange = { modelType = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Algoritma
        Text("Algoritma", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = algorithm, onValueChange = { algorithm = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Data Pelatihan
        Text("Data Pelatihan", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = trainingData, onValueChange = { trainingData = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Performa Model
        Text("Performa Model", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = performance, onValueChange = { performance = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // File Model Terlatih (dengan fungsi upload)
        Text("File Model Terlatih", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = trainedModelFileDisplayName, // Menampilkan nama file
            onValueChange = { trainedModelFileDisplayName = it }, // Bisa diubah manual jika perlu
            readOnly = true, // Disarankan read-only saat menggunakan file picker
            modifier = Modifier
                .fillMaxWidth()
                .clickable { filePickerLauncher.launch("*/*") }, // Membuka pemilih file saat diklik
            trailingIcon = {
                IconButton(onClick = { filePickerLauncher.launch("*/*") }) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Pilih File Model")
                }
            }
        )

        Spacer(Modifier.height(12.dp))
        // Strategi Penyempurnaan
        Text("Strategi Penyempurnaan", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = refinementStrategy, onValueChange = { refinementStrategy = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        // Performa Setelah Penyempurnaan
        Text("Performa Setelah Penyempurnaan", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(value = performanceAfterRefinement, onValueChange = { performanceAfterRefinement = it }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onCancel() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007bff)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Return", color = Color.White)
            }
            Button(
                onClick = {
                    val newTraining = ModelTraining(
                        id = initialData?.id ?: 0,
                        projectName = selectedProject,
                        modelName = modelName,
                        modelType = modelType,
                        algorithm = algorithm,
                        trainingData = trainingData,
                        performance = performance,
                        modelPath = selectedFileUri?.toString() ?: "", // Simpan URI sebagai string
                        refinementStrategy = refinementStrategy,
                        performanceAfterRefinement = performanceAfterRefinement
                    )
                    onSubmit(newTraining)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28a745)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Save Changes", color = Color.White)
            }
        }
    }
}

// Reused and adapted from your previous request for delete confirmation
@Composable
fun DeleteConfirmationDialog(
    entry: ModelTraining, // Changed parameter type to ModelTraining
    onDeleteConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Apakah Ingin menghapus ?",
                    color = Color(0xFFD87AB2), // soft pink
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.modelName, // Displaying modelName for context
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC66)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Return", color = Color.White)
                    }
                    Button(
                        onClick = {
                            onDeleteConfirm()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F6F)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}