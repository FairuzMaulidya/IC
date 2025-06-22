package com.example.test.screen

import android.app.Application
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType // Import ini
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.test.data.DatasetRequest
import com.example.test.data.Project
import com.example.test.viewmodel.DatasetViewModel
import com.example.test.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// Reusable Composable for labeled text fields
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        OutlinedTextField( // Changed to OutlinedTextField for better visual
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.LightGray
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDatasetScreen(
    navController: NavController,
    datasetViewModel: DatasetViewModel = viewModel(
        factory = DatasetViewModel.Factory(LocalContext.current.applicationContext as Application)
    ),
    projectViewModel: ProjectViewModel = viewModel(
        factory = ProjectViewModel.Factory(LocalContext.current.applicationContext as Application)
    )
) {
    val context = LocalContext.current

    var currentPage by remember { mutableStateOf(1) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }

    // Form fields
    var deskripsiMasalah by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var tipeData by remember { mutableStateOf("") }
    var aktivitasPemrosesan by remember { mutableStateOf("") }
    var estimasiJumlahFitur by remember { mutableStateOf("") }
    var estimasiUkuranDataset by remember { mutableStateOf("") }
    var formatFile by remember { mutableStateOf("") }
    var tanggalMulai by remember { mutableStateOf("") }
    var tanggalSelesai by remember { mutableStateOf("") }

    val datasetRequests by datasetViewModel.allReqDatasets.collectAsState(initial = emptyList())

    // CORRECT: Observing both local and API projects from ProjectViewModel
    val localProjects by projectViewModel.allLocalProjects.observeAsState(emptyList())
    val apiProjects by projectViewModel.apiProjects.observeAsState(emptyList())

    // CORRECT: Combine and sort projects for display
    val projects = remember(localProjects, apiProjects) {
        (localProjects + apiProjects).sortedBy { it.projectName }
    }

    var selectedRequestForView by remember { mutableStateOf<DatasetRequest?>(null) }
    var showViewDialog by remember { mutableStateOf(false) }

    fun resetFormFields() {
        deskripsiMasalah = ""
        target = ""
        tipeData = ""
        aktivitasPemrosesan = ""
        estimasiJumlahFitur = ""
        estimasiUkuranDataset = ""
        formatFile = ""
        tanggalMulai = ""
        tanggalSelesai = ""
    }

    fun populateFormForEdit(request: DatasetRequest) {
        deskripsiMasalah = request.descriptionn
        target = request.target
        tipeData = request.dataType
        aktivitasPemrosesan = request.dataProcessing
        estimasiJumlahFitur = request.featureCount.toString()
        estimasiUkuranDataset = request.datasetSize
        formatFile = request.expectedFileFormat
        tanggalMulai = request.startDate
        tanggalSelesai = request.endDate
    }

    when (currentPage) {
        1 -> {
            // Halaman 1: Pilih Proyek
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Pilih Proyek untuk Membuat Permintaan Dataset",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (projects.isEmpty()) {
                    Text(
                        "Belum ada proyek yang tersedia. Silakan buat proyek terlebih dahulu.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    val horizontalScrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        // Header Tabel
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFEFEFEF))
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                        ) {
                            Text("#", Modifier.width(40.dp))
                            Text("Nama Proyek", Modifier.width(150.dp))
                            Text("Sumber", Modifier.width(80.dp)) // <--- Ditambahkan
                            Text("Deskripsi", Modifier.width(250.dp))
                            Text("Status", Modifier.width(100.dp))
                            Text("Aksi", Modifier.width(120.dp), textAlign = TextAlign.Center)
                        }

                        Divider(color = Color.Gray, thickness = 1.dp)

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            itemsIndexed(projects) { index, project ->
                                // Highlight row for API projects
                                val rowBackgroundColor = if (project.isFromApi) Color(0xFFE0F7FA) else Color.White // <--- Ditambahkan

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBackgroundColor) // <--- Digunakan
                                        .clickable {
                                            selectedProject = project
                                            resetFormFields()
                                            currentPage = 2
                                        }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${index + 1}", modifier = Modifier.width(40.dp))
                                    Text(project.projectName, modifier = Modifier.width(150.dp)) // <--- Dihapus ?: "N/A"
                                    Text(if (project.isFromApi) "API" else "Lokal", modifier = Modifier.width(80.dp)) // <--- Ditambahkan
                                    Text(project.description.orEmpty(), modifier = Modifier.width(250.dp)) // <--- Dihapus ?: "N/A"

                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .background(
                                                when (project.status) {
                                                    "Complete" -> Color(0xFF4CAF50) // <--- Diperbarui
                                                    "Ongoing" -> Color(0xFF03A9F4)  // <--- Diperbarui
                                                    "Pending" -> Color(0xFFFFC107)
                                                    else -> Color(0xFF9E9E9E)
                                                },
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = project.status.orEmpty(), // <--- Diperbarui,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            selectedProject = project
                                            resetFormFields()
                                            currentPage = 2
                                        },
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(36.dp)
                                            .padding(start = 8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Request", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Divider(color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }

        2 -> {
            // Halaman 2: Form Permintaan Dataset
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

            val startDatePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        val selectedCal = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth)
                        }
                        tanggalMulai = dateFormatter.format(selectedCal.time)
                    },
                    year, month, day
                )
            }

            val endDatePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        val selectedCal = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth)
                        }
                        tanggalSelesai = dateFormatter.format(selectedCal.time)
                    },
                    year, month, day
                )
            }

            if (selectedProject == null) {
                Text("Error: Proyek tidak terpilih. Kembali ke halaman sebelumnya.",
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
                )
                Button(onClick = { currentPage = 1 }) { Text("Kembali") }
            } else {
                val project = selectedProject!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // Header bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFE6F0))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Request Dataset",
                            color = Color(0xFFE91E63),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Form container
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .background(Color(0xFFF2F2F2))
                            .padding(16.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            "Form Permintaan Dataset: ${project.projectName}", // <--- Dihapus ?: "N/A"
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val formModifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)

                        LabeledTextField(
                            label = "Kebutuhan dataset",
                            value = deskripsiMasalah,
                            onValueChange = { deskripsiMasalah = it },
                            modifier = formModifier,
                        )

                        LabeledTextField(
                            label = "Target",
                            value = target,
                            onValueChange = { target = it },
                            modifier = formModifier
                        )

                        LabeledTextField(
                            label = "Tipe Data",
                            value = tipeData,
                            onValueChange = { tipeData = it },
                            modifier = formModifier
                        )

                        LabeledTextField(
                            label = "Aktivitas Pemrosesan Data",
                            value = aktivitasPemrosesan,
                            onValueChange = { aktivitasPemrosesan = it },
                            modifier = formModifier,
                        )

                        LabeledTextField(
                            label = "Estimasi Jumlah Fitur",
                            value = estimasiJumlahFitur,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                    estimasiJumlahFitur = newValue
                                }
                            },
                            modifier = formModifier,
                        )

                        LabeledTextField(
                            label = "Estimasi Ukuran Dataset (e.g., 1GB, 500MB)",
                            value = estimasiUkuranDataset,
                            onValueChange = { estimasiUkuranDataset = it },
                            modifier = formModifier
                        )

                        LabeledTextField(
                            label = "Format File (e.g., CSV, JSON, Parquet)",
                            value = formatFile,
                            onValueChange = { formatFile = it },
                            modifier = formModifier
                        )

                        LabeledTextField(
                            value = tanggalMulai,
                            onValueChange = {},
                            label = "Tanggal Mulai dibutuhkan",
                            modifier = formModifier.clickable {
                                startDatePickerDialog.show()
                            },
                            readOnly = true
                        )

                        LabeledTextField(
                            value = tanggalSelesai,
                            onValueChange = {},
                            label = "Tanggal Selesai dibutuhkan",
                            modifier = formModifier.clickable {
                                endDatePickerDialog.show()
                            },
                            readOnly = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { currentPage = 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2979FF)
                                ),
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Text("Return")
                            }
                            Button(
                                onClick = {
                                    if (deskripsiMasalah.isBlank() || target.isBlank() || tipeData.isBlank() || tanggalMulai.isBlank() || tanggalSelesai.isBlank()) {
                                        Toast.makeText(context, "Harap lengkapi semua bidang penting!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    datasetViewModel.addReqDataset(
                                        DatasetRequest(
                                            // projectId is removed as per request
                                            projectName = project.projectName, // <--- Dihapus ?: ""
                                            description = project.description.orEmpty(), // <--- Dihapus ?: ""
                                            featureCount = estimasiJumlahFitur.toIntOrNull() ?: 0,
                                            datasetSize = estimasiUkuranDataset,
                                            expectedFileFormat = formatFile,
                                            descriptionn = deskripsiMasalah,
                                            dataType = tipeData,
                                            dataProcessing = aktivitasPemrosesan,
                                            startDate = tanggalMulai,
                                            endDate = tanggalSelesai,
                                            target = target,
                                            requestedBy = "",
                                            status = "Pending"
                                        )
                                    )

                                    resetFormFields()
                                    currentPage = 3
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Changes")
                            }
                        }
                    }
                }
            }
        }

        3 -> {
            // Halaman 3: Daftar Permintaan Dataset
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Daftar Permintaan Dataset",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (datasetRequests.isEmpty()) {
                    Text(
                        "Belum ada permintaan dataset.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    val horizontalScrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(8.dp)
                    ) {
                        // Header tabel
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFE0E0E0))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#", modifier = Modifier.width(40.dp))
                            Text("Proyek", modifier = Modifier.width(120.dp))
                            Text("Tipe Data", modifier = Modifier.width(120.dp))
                            Text("Jenis File", modifier = Modifier.width(120.dp))
                            Text("Status", modifier = Modifier.width(100.dp)) // <--- Ditambahkan
                            Text("Aksi", modifier = Modifier.width(240.dp))
                        }

                        Divider(color = Color.Gray, thickness = 1.dp)

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            itemsIndexed(datasetRequests) { index, request ->
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${index + 1}", modifier = Modifier.width(40.dp))
                                    Text(request.projectName, modifier = Modifier.width(120.dp))
                                    Text(request.dataType, modifier = Modifier.width(120.dp))
                                    Text(request.expectedFileFormat, modifier = Modifier.width(120.dp))
                                    Box( // <--- Ditambahkan untuk status
                                        modifier = Modifier
                                            .width(100.dp)
                                            .background(
                                                when (request.status) {
                                                    "Approved" -> Color(0xFF4CAF50)
                                                    "Pending" -> Color(0xFFFFC107)
                                                    "Rejected" -> Color(0xFFF44336)
                                                    else -> Color(0xFF9E9E9E)
                                                },
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = request.status,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }


                                    Row(
                                        modifier = Modifier.width(240.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedRequestForView = request
                                                showViewDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                            ),
                                            contentPadding = PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 4.dp
                                            ),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("View", color = Color.White)
                                        }
                                        Button(
                                            onClick = {
                                                datasetViewModel.deleteReqDataset(request)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFF44336)
                                            ),
                                            contentPadding = PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 4.dp
                                            ),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Delete", color = Color.White)
                                        }
                                    }
                                }
                                Divider(color = Color.LightGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { currentPage = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Buat Permintaan Dataset Baru")
                }
            }

            // Popup/tampilan detail "View"
            if (showViewDialog && selectedRequestForView != null) {
                AlertDialog(
                    onDismissRequest = { showViewDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showViewDialog = false }) {
                            Text("Tutup")
                        }
                    },
                    title = {
                        Text("Detail Permintaan Dataset")
                    },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            val formModifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)

                            selectedRequestForView?.let { request ->
                                LabeledTextField(
                                    label = "Nama Proyek",
                                    value = request.projectName,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Deskripsi Proyek",
                                    value = request.description,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Kebutuhan dataset",
                                    value = request.descriptionn,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Target",
                                    value = request.target,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Tipe Data",
                                    value = request.dataType,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Aktivitas Pemrosesan Data",
                                    value = request.dataProcessing,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Estimasi Jumlah Fitur",
                                    value = request.featureCount.toString(),
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Estimasi Ukuran Dataset",
                                    value = request.datasetSize,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Format File",
                                    value = request.expectedFileFormat,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Tanggal Mulai dibutuhkan",
                                    value = request.startDate,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Tanggal Selesai dibutuhkan",
                                    value = request.endDate,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Diminta Oleh",
                                    value = request.requestedBy,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                                LabeledTextField(
                                    label = "Status Permintaan",
                                    value = request.status,
                                    onValueChange = { /* No-op, read-only */ },
                                    modifier = formModifier,
                                    readOnly = true
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black,
            modifier = Modifier.padding(start = 0.dp, bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black
            ),
            shape = RoundedCornerShape(4.dp)
        )
    }
}