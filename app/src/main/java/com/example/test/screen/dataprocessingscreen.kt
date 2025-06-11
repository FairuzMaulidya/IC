package com.example.test.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.test.viewmodel.DataProcessingViewModel
import com.example.test.viewmodel.ProjectViewModel
import com.example.test.data.DataProcessing
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clipToBounds // Import for clipToBounds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataProcessingScreen(
    navController: NavHostController,
    projectViewModel: ProjectViewModel = viewModel(),
    dataProcessingViewModel: DataProcessingViewModel = viewModel()
) {
    val allProjects by projectViewModel.allProjects.observeAsState(emptyList())
    val allData by dataProcessingViewModel.allDataProcessing.observeAsState(emptyList())

    var showForm by remember { mutableStateOf(false) } // State to control form visibility
    var selectedDataForEdit by remember { mutableStateOf<DataProcessing?>(null) }
    var selectedDataForView by remember { mutableStateOf<DataProcessing?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Daftar Pemrosesan Data", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Title
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                selectedDataForEdit = null // Ensure no data is pre-filled for a new entry
                showForm = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28a745)), // Green color from screenshot
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Buat Entri Pemrosesan Baru", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        if (showForm) {
            // Data Processing Form - now conditionally shown
            DataProcessingForm(
                allProjects = allProjects,
                onSubmit = { data ->
                    if (selectedDataForEdit != null) {
                        dataProcessingViewModel.update(data.copy(id = selectedDataForEdit!!.id))
                    } else {
                        dataProcessingViewModel.insert(data)
                    }
                    showForm = false // Hide form after submission
                    selectedDataForEdit = null // Clear edit selection
                },
                onCancel = {
                    showForm = false // Hide form on cancel
                    selectedDataForEdit = null // Clear edit selection
                },
                initialData = selectedDataForEdit
            )
        } else {
            // Data Processing Table - only shown when form is hidden
            Text("Pemrosesan Data Tersedia:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (allData.isNotEmpty()) {
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
                        // Adjusted widths to match screenshot and allow for content
                        Text("Proyek", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp)) //
                        Text("Sumber Data", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp)) //
                        Text("Langkah Pembersihan", fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp)) //
                        Text("Langkah Transformasi", fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp)) //
                        Text("Rekayasa Fitur", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp)) //
                        Text("Metrik Kualitas Data", fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp)) //
                        Text("Lokasi Data yang Diproses", fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp)) //
                        Text("Diproses Oleh", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp)) //
                        Text("Tanggal Dibuat", fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp)) //
                        Text("Status", fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp)) //
                        Text("Aksi", fontWeight = FontWeight.Bold, modifier = Modifier.width(200.dp), textAlign = TextAlign.End) // Wider for 3 buttons
                    }
                }

                // Table Content (LazyColumn for rows)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Makes LazyColumn take remaining height
                ) {
                    itemsIndexed(allData) { index, data ->
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Data cells - match header widths
                                Text(data.projectName, modifier = Modifier.width(100.dp))
                                Text(data.sourceData, modifier = Modifier.width(120.dp))
                                Text(data.cleaningSteps, modifier = Modifier.width(150.dp))
                                Text(data.transformationSteps, modifier = Modifier.width(150.dp))
                                Text(data.featureEngineering, modifier = Modifier.width(120.dp))
                                Text(data.qualityMetrics, modifier = Modifier.width(150.dp))
                                Text(data.processedLocation, modifier = Modifier.width(180.dp))
                                Text(data.processedBy, modifier = Modifier.width(120.dp))
                                Text(data.createdAt, modifier = Modifier.width(130.dp))

                                // Status (assuming you might add status to DataProcessing data class later)
                                // For now, defaulting to "Completed" or similar if not available
                                val status = "Completed" // Placeholder for status
                                val statusColor = when (status) {
                                    "Completed" -> Color(0xFF4CAF50) // Green
                                    "In Progress" -> Color(0xFFFFC107) // Yellow (like "Pending" in dataset request)
                                    else -> Color(0xFF03A9F4) // Blue
                                }
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .background(statusColor, shape = MaterialTheme.shapes.small)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = status, // Display the status
                                        color = Color.White,
                                        fontSize = 10.sp // Smaller font for status
                                    )
                                }


                                Row(
                                    modifier = Modifier.width(200.dp), // Actions column width
                                    horizontalArrangement = Arrangement.End // Align buttons to the end
                                ) {
                                    Button(
                                        onClick = { selectedDataForView = data },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6c757d)), // Grey
                                        modifier = Modifier.width(60.dp).height(30.dp) // Smaller button
                                    ) {
                                        Text("View", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = {
                                            selectedDataForEdit = data
                                            showForm = true // Show form in edit mode
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007bff)), // Blue
                                        modifier = Modifier.width(60.dp).height(30.dp)
                                    ) {
                                        Text("Edit", fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Button(
                                        onClick = { dataProcessingViewModel.delete(data) },
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
                    "Belum ada data pemrosesan.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }


        // Detail Dialog (remains unchanged)
        if (selectedDataForView != null) {
            AlertDialog(
                onDismissRequest = { selectedDataForView = null },
                confirmButton = {
                    TextButton(onClick = { selectedDataForView = null }) {
                        Text("Tutup")
                    }
                },
                title = { Text("Detail Data Pemrosesan") },
                text = {
                    Column {
                        Text("Proyek: ${selectedDataForView!!.projectName}")
                        Text("Sumber: ${selectedDataForView!!.sourceData}")
                        Text("Pembersihan: ${selectedDataForView!!.cleaningSteps}")
                        Text("Transformasi: ${selectedDataForView!!.transformationSteps}")
                        Text("Fitur: ${selectedDataForView!!.featureEngineering}")
                        Text("Metrik: ${selectedDataForView!!.qualityMetrics}")
                        Text("Lokasi: ${selectedDataForView!!.processedLocation}")
                        Text("Oleh: ${selectedDataForView!!.processedBy}")
                        Text("Tanggal: ${selectedDataForView!!.createdAt}")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataProcessingForm(
    allProjects: List<com.example.test.data.Project>,
    onSubmit: (DataProcessing) -> Unit,
    onCancel: () -> Unit,
    initialData: DataProcessing? = null
) {
    var selectedProject by remember { mutableStateOf(initialData?.projectName ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var sourceData by remember { mutableStateOf(initialData?.sourceData ?: "") }
    var cleaningSteps by remember { mutableStateOf(initialData?.cleaningSteps ?: "") }
    var transformationSteps by remember { mutableStateOf(initialData?.transformationSteps ?: "") }
    var featureEngineering by remember { mutableStateOf(initialData?.featureEngineering ?: "") }
    var qualityMetrics by remember { mutableStateOf(initialData?.qualityMetrics ?: "") }
    var processedLocation by remember { mutableStateOf(initialData?.processedLocation ?: "") }
    var processedBy by remember { mutableStateOf(initialData?.processedBy ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()) // Allow the form to scroll if content is long
    ) {
        Text(
            if (initialData != null) "Edit Pemrosesan Data" else "Form Pemrosesan Data Baru",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedProject,
                onValueChange = {},
                readOnly = true,
                label = { Text("Proyek Terkait") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = sourceData, onValueChange = { sourceData = it }, label = { Text("Sumber Data") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = cleaningSteps, onValueChange = { cleaningSteps = it }, label = { Text("Langkah Pembersihan") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = transformationSteps, onValueChange = { transformationSteps = it }, label = { Text("Transformasi Data") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = featureEngineering, onValueChange = { featureEngineering = it }, label = { Text("Rekayasa Fitur") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = qualityMetrics, onValueChange = { qualityMetrics = it }, label = { Text("Metrik Kualitas") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = processedLocation, onValueChange = { processedLocation = it }, label = { Text("Lokasi Data") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = processedBy, onValueChange = { processedBy = it }, label = { Text("Diproses Oleh") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val now = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
                    onSubmit(
                        DataProcessing(
                            projectName = selectedProject,
                            sourceData = sourceData,
                            cleaningSteps = cleaningSteps,
                            transformationSteps = transformationSteps,
                            featureEngineering = featureEngineering,
                            qualityMetrics = qualityMetrics,
                            processedLocation = processedLocation,
                            processedBy = processedBy,
                            createdAt = initialData?.createdAt ?: now // Keep original creation date if editing
                        )
                    )
                    // Clear fields after submission
                    sourceData = ""
                    cleaningSteps = ""
                    transformationSteps = ""
                    featureEngineering = ""
                    qualityMetrics = ""
                    processedLocation = ""
                    processedBy = ""
                    selectedProject = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (initialData != null) "Update" else "Simpan")
            }
            OutlinedButton(
                onClick = {
                    onCancel()
                    // Clear fields on cancel
                    sourceData = ""
                    cleaningSteps = ""
                    transformationSteps = ""
                    featureEngineering = ""
                    qualityMetrics = ""
                    processedLocation = ""
                    processedBy = ""
                    selectedProject = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Batal")
            }
        }
    }
}