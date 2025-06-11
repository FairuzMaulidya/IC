package com.example.test.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview // Keep this import for preview functionality

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TrainModelScreen(
    navController: NavHostController // This parameter is here but not used in the provided logic for navigation
) {
    val modelTrainingViewModel: ModelTrainingViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()

    val trainings by modelTrainingViewModel.modelTrainings.collectAsState(initial = emptyList()) // Ensure initial value for flow
    val projects by projectViewModel.allProjects.observeAsState(initial = emptyList())

    var showForm by remember { mutableStateOf(false) }
    var editingTraining by remember { mutableStateOf<ModelTraining?>(null) }
    var viewingTraining by remember { mutableStateOf<ModelTraining?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Daftar Pelatihan Model", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp)) // Space after title

        Button(
            onClick = {
                editingTraining = null
                showForm = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28a745)), // Green color from screenshot
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Model Training Baru", color = Color.White)
        }

        // --- Start of the corrected section for showing the form ---
        if (showForm) {
            ModelTrainingForm(
                projects = projects,
                initialTraining = editingTraining,
                onSave = {
                    if (editingTraining != null) {
                        modelTrainingViewModel.updateTraining(it)
                    } else {
                        modelTrainingViewModel.addTraining(it)
                    }
                    showForm = false
                    editingTraining = null
                },
                onCancel = {
                    showForm = false
                    editingTraining = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Crucial: Allows the form to take available space and scroll internally
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        // --- End of the corrected section ---


        if (!showForm) {
            Text("Pelatihan Model Tersedia:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp)) // Space before table

            if (trainings.isNotEmpty()) {
                // Shared horizontal scroll state for header and rows
                val sharedHorizontalScrollState = rememberScrollState()

                // Table Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)) // Light grey background
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(sharedHorizontalScrollState) // Apply shared scroll state here
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        // Weights adjusted to roughly match screenshot, might need fine-tuning
                        Text("#", fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                        Text("Proyek", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        Text("Nama Model", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Tipe Model", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        Text("Algoritma", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        Text("Hyperparameter", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Data Latih", fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        Text("Metrik Evaluasi", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Performa Model", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Dilatih Oleh", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                        Text("Tanggal Pelatihan", fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
                        Text("Path Model Tersimpan", fontWeight = FontWeight.Bold, modifier = Modifier.width(160.dp))
                        Text("Tanggal Dibuat", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Terakhir Update", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                        Text("Aksi", fontWeight = FontWeight.Bold, modifier = Modifier.width(200.dp), textAlign = TextAlign.End) // Wider for 3 buttons
                    }
                }

                // Table Content (LazyColumn for rows)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Makes LazyColumn take remaining height
                ) {
                    items(
                        items = trainings,
                        key = { training -> training.id }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp) // Smaller padding between rows
                                .clipToBounds(), // Clip content if it tries to overflow
                            elevation = CardDefaults.cardElevation(1.dp), // Less prominent elevation
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(sharedHorizontalScrollState) // Apply shared scroll state here too
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                // Data cells - match header widths
                                Text("${trainings.indexOf(item) + 1}", modifier = Modifier.width(30.dp)) // # column
                                Text(item.projectName, modifier = Modifier.width(100.dp))
                                Text(item.modelName, modifier = Modifier.width(120.dp))
                                Text(item.modelType, modifier = Modifier.width(100.dp))
                                Text(item.algorithm, modifier = Modifier.width(100.dp))
                                Text(item.hyperparameters, modifier = Modifier.width(120.dp))
                                Text(item.trainingData, modifier = Modifier.width(90.dp))
                                Text(item.evaluationMetric, modifier = Modifier.width(120.dp))
                                Text(item.performance, modifier = Modifier.width(120.dp))
                                Text(item.trainedBy, modifier = Modifier.width(100.dp))
                                Text(item.trainingDate, modifier = Modifier.width(130.dp))
                                Text(item.modelPath, modifier = Modifier.width(160.dp))
                                Text(item.createdDate, modifier = Modifier.width(120.dp)) // Tanggal Dibuat
                                Text(item.lastUpdated, modifier = Modifier.width(120.dp)) // Terakhir Update

                                Row(
                                    modifier = Modifier.width(200.dp), // Actions column width
                                    horizontalArrangement = Arrangement.End // Align buttons to the end
                                ) {
                                    Button(
                                        onClick = { viewingTraining = item },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c757d)), // Grey
                                        modifier = Modifier.width(60.dp).height(30.dp) // Smaller button
                                    ) {
                                        Text("View", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            editingTraining = item
                                            showForm = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007bff)), // Blue
                                        modifier = Modifier.width(60.dp).height(30.dp)
                                    ) {
                                        Text("Edit", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = { modelTrainingViewModel.deleteTraining(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFdc3545)), // Red
                                        modifier = Modifier.width(60.dp).height(30.dp)
                                    ) {
                                        Text("Hapus", fontSize = 10.sp, color = Color.White)
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


            // View Dialog (unchanged)
            if (viewingTraining != null) {
                AlertDialog(
                    onDismissRequest = { viewingTraining = null },
                    title = { Text("Detail Model") },
                    text = {
                        Column {
                            Text("Nama Model: ${viewingTraining!!.modelName}")
                            Text("Tipe: ${viewingTraining!!.modelType}")
                            Text("Algoritma: ${viewingTraining!!.algorithm}")
                            Text("Performa: ${viewingTraining!!.performance}")
                            Text("Data Latih: ${viewingTraining!!.trainingData}")
                            Text("Metrik Evaluasi: ${viewingTraining!!.evaluationMetric}")
                            Text("Trained By: ${viewingTraining!!.trainedBy}")
                            Text("Path: ${viewingTraining!!.modelPath}")
                            Text("Tanggal Pelatihan: ${viewingTraining!!.trainingDate}")
                            Text("Tanggal Dibuat: ${viewingTraining!!.createdDate}") // Display createdDate
                            Text("Terakhir Update: ${viewingTraining!!.lastUpdated}") // Display lastUpdated
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewingTraining = null }) {
                            Text("Tutup")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ModelTrainingForm(
    projects: List<Project>,
    initialTraining: ModelTraining? = null,
    onSave: (ModelTraining) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier // Add a modifier parameter
) {
    // Corrected date format from "dd MMMADIUM" to "dd MMM yyyy" or "dd MMM yyyy HH:mm:ss"
    // Using "dd MMM yyyy" to match common date display without time.
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = dateFormat.format(Date())

    var projectName by rememberSaveable { mutableStateOf(initialTraining?.projectName ?: "") }
    var modelName by rememberSaveable { mutableStateOf(initialTraining?.modelName ?: "") }
    var modelType by rememberSaveable { mutableStateOf(initialTraining?.modelType ?: "") }
    var algorithm by rememberSaveable { mutableStateOf(initialTraining?.algorithm ?: "") }
    var hyperparameters by rememberSaveable { mutableStateOf(initialTraining?.hyperparameters ?: "") }
    var trainingData by rememberSaveable { mutableStateOf(initialTraining?.trainingData ?: "") }
    var evaluationMetric by rememberSaveable { mutableStateOf(initialTraining?.evaluationMetric ?: "") }
    var performance by rememberSaveable { mutableStateOf(initialTraining?.performance ?: "") }
    var trainedBy by rememberSaveable { mutableStateOf(initialTraining?.trainedBy ?: "") }
    var modelPath by rememberSaveable { mutableStateOf(initialTraining?.modelPath ?: "") }

    Column(modifier = modifier // Use the passed modifier from TrainModelScreen
        .fillMaxWidth()
        .padding(8.dp)
        .verticalScroll(rememberScrollState()) // The form itself uses verticalScroll for its content
    ) {
        ProjectDropdown("Pilih Proyek", projectName, projects.map { it.projectName }) {
            projectName = it
        }
        CustomTextField("Nama Model", modelName) { modelName = it }
        CustomTextField("Tipe Model", modelType) { modelType = it }
        CustomTextField("Algoritma", algorithm) { algorithm = it }
        CustomTextField("Hyperparameter", hyperparameters) { hyperparameters = it }
        CustomTextField("Data Latih", trainingData) { trainingData = it }
        CustomTextField("Metrik Evaluasi", evaluationMetric) { evaluationMetric = it }
        CustomTextField("Performa Model", performance) { performance = it }
        CustomTextField("Dilatih Oleh", trainedBy) { trainedBy = it }
        CustomTextField("Path Model", modelPath) { modelPath = it }

        Row(modifier = Modifier.padding(top = 8.dp)) {
            Button(onClick = {
                if (projectName.isNotBlank() && modelName.isNotBlank()) {
                    onSave(
                        ModelTraining(
                            id = initialTraining?.id ?: 0,
                            projectName, modelName, modelType, algorithm,
                            hyperparameters, trainingData, evaluationMetric,
                            performance, trainedBy,
                            trainingDate = initialTraining?.trainingDate ?: today, // Keep original trainingDate if editing
                            modelPath = modelPath,
                            createdDate = initialTraining?.createdDate ?: today, // Keep original createdDate if editing
                            lastUpdated = today // Always update lastUpdated to today
                        )
                    )
                }
            }) {
                Text("Simpan")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onCancel) {
                Text("Batal")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDropdown(
    label: String,
    selected: String,
    items: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {

        OutlinedTextField(
            value = selected,
            onValueChange = { /* Read-only. Value is set via DropdownMenuItem click. */ },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}