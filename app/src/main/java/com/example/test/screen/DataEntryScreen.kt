package com.example.test.screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange // Untuk ikon DatePicker
import androidx.compose.material.icons.filled.Search // Untuk ikon Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.test.data.DataEntry
import com.example.test.data.Project
import com.example.test.viewmodel.DataEntryViewModel
import com.example.test.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataEntryScreen(
    navController: NavHostController,
    viewModel: DataEntryViewModel = viewModel(),
    projectViewModel: ProjectViewModel = viewModel()
) {
    val allEntries by viewModel.allEntries.collectAsState(initial = emptyList())
    val localProjects by projectViewModel.allLocalProjects.observeAsState(emptyList())
    val apiProjects by projectViewModel.apiProjects.observeAsState(emptyList())
    val projectList = remember(localProjects, apiProjects) {
        (localProjects + apiProjects).sortedBy { it.projectName }
    }

    val projectNames = remember(projectList) {
        projectList.map { it.projectName }
    }

    var selectedProject by remember { mutableStateOf<Project?>(null) }
    val entryState = remember { mutableStateOf(DataEntry()) }
    val showForm = remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val showDetail = remember { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val selectedEntry = remember { mutableStateOf<DataEntry?>(null) }

    val verticalScrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(verticalScrollState) // Main screen column is scrollable
    ) {
        if (selectedProject == null) {
            // Display Project Table when no project is selected
            Text("Pilih Proyek untuk Problem Framing", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ProjectTable(projects = projectList, onStart = {
                selectedProject = it
                entryState.value = DataEntry(
                    projectName = it.projectName,
                    dateCreated = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                )
                showForm.value = true // Automatically show form for new entry after project selection
            })
        } else {
            // Content after a project is selected
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Proyek: ${selectedProject!!.projectName}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    selectedProject = null
                    showForm.value = false
                    entryState.value = DataEntry() // Reset entry state
                }) {
                    Text("Kembali Pilih Proyek")
                }
            }

            Spacer(Modifier.height(8.dp))

            if (showForm.value) {
                // Display Data Entry Form when showForm is true
                DataEntryForm(
                    entry = entryState.value,
                    onEntryChange = { field, value ->
                        entryState.value = entryState.value.copyField(field, value)
                    },
                    onSave = {
                        if (entryState.value.id == 0) viewModel.insertEntry(entryState.value)
                        else viewModel.updateEntry(entryState.value)
                        showForm.value = false
                        if (entryState.value.id == 0) { // Reset for new entry after save
                            entryState.value = DataEntry(
                                projectName = selectedProject!!.projectName,
                                dateCreated = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            )
                        }
                    },
                    onClose = { showForm.value = false },
                    // Hapus onDelete dari DataEntryForm karena tombol delete dipindahkan ke tabel
                    projectList = projectNames
                )
            } else {
                // Display "Tambah Data Entry" button and the table when form is not shown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            entryState.value = DataEntry(
                                projectName = selectedProject!!.projectName,
                                dateCreated = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            )
                            showForm.value = true
                        },
                        shape = CircleShape, // Bentuk lingkaran
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Warna hijau
                        modifier = Modifier.size(48.dp), // Ukuran tombol
                        contentPadding = PaddingValues(0.dp) // Pastikan tidak ada padding default yang mengganggu
                    ) {
                        // Menggunakan Text untuk menampilkan '+'
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 24.sp, // Ukuran font disesuaikan agar pas di dalam 48.dp
                            fontWeight = FontWeight.Bold // Ketebalan font untuk visibilitas
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Search field (disesuaikan agar mirip gambar)
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Searching...", color = Color.Gray) }, // Placeholder
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        },
                        modifier = Modifier.weight(1f), // Mengisi sisa ruang
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp) // Bentuk rounded
                    )
                }


                Spacer(Modifier.height(8.dp))

                val filteredEntries = allEntries.filter {
                    it.projectName == selectedProject!!.projectName &&
                            (query.isBlank() || it.problemDescription.contains(query, true) ||
                                    it.target.contains(query, true) || it.dataNeeded.contains(query, true))
                }
                HorizontalScrollableTable(
                    data = filteredEntries,
                    onEdit = {
                        entryState.value = it
                        showForm.value = true
                    },
                    onDelete = {
                        selectedEntry.value = it
                        showDeleteConfirmation.value = true
                    },
                    onView = {
                        selectedEntry.value = it
                        showDetail.value = true
                    }
                )

                if (showDetail.value && selectedEntry.value != null) {
                    EntryDetailView(entry = selectedEntry.value!!, onClose = { showDetail.value = false })
                }
                if (showDeleteConfirmation.value && selectedEntry.value != null) {
                    DeleteConfirmationDialog(
                        entry = selectedEntry.value!!,
                        onDeleteConfirm = {
                            viewModel.deleteEntry(selectedEntry.value!!)
                            showDeleteConfirmation.value = false
                        },
                        onDismiss = { showDeleteConfirmation.value = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryForm(
    entry: DataEntry,
    onEntryChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    // onDelete: () -> Unit, // Hapus parameter onDelete
    projectList: List<String> // List of project names for dropdown
) {
    val context = LocalContext.current
    var selectedProjectName by remember(entry.projectName) { mutableStateOf(entry.projectName) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Problem Framing",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB0006D),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(color = Color(0xFFB0006D), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            ProjectDropdownField(
                selectedProject = selectedProjectName,
                onProjectSelected = { newName ->
                    selectedProjectName = newName
                    onEntryChange("projectName", newName)
                },
                projectList = projectList
            )
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Deskripsi Masalah", entry.problemDescription) {
                onEntryChange("problemDescription", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Target/Tujuan", entry.target) {
                onEntryChange("target", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Stock", entry.stock) {
                onEntryChange("stock", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Inflow", entry.inflow) {
                onEntryChange("inflow", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Outflow", entry.outflow) {
                onEntryChange("outflow", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Data Diperlukan", entry.dataNeeded) {
                onEntryChange("dataNeeded", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Diframe Oleh", entry.framedBy) {
                onEntryChange("framedBy", it)
            }
            Spacer(modifier = Modifier.height(12.dp))

            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                        onEntryChange("dateCreated", formattedDate)
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                )
            }

            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text("Tanggal Dibuat", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = entry.dateCreated,
                    onValueChange = { onEntryChange("dateCreated", it) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 100.dp)
                ) {
                    Text("Return", color = Color.White)
                }

                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 140.dp)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProjectTable(
    projects: List<Project>,
    onStart: (Project) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        Row(
            Modifier
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            TableCell("No", fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
            TableCell("Nama Proyek", fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
            TableCell("Deskripsi", fontWeight = FontWeight.Bold, modifier = Modifier.width(240.dp))
            TableCell("Lokasi", fontWeight = FontWeight.Bold, modifier = Modifier.width(160.dp))
            TableCell("Status", fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
            TableCell("Aksi", fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
        }

        projects.forEachIndexed { index, project ->
            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell("${index + 1}", modifier = Modifier.width(60.dp))
                TableCell(project.projectName, modifier = Modifier.width(180.dp))
                TableCell(project.description.orEmpty(), modifier = Modifier.width(240.dp))
                TableCell(project.location.orEmpty(), modifier = Modifier.width(160.dp))
                TableCell(
                    project.status.orEmpty(),
                    modifier = Modifier.width(120.dp),
                    fontWeight = FontWeight.Medium
                )
                TableCell(modifier = Modifier.width(180.dp)) {
                    Button(
                        onClick = { onStart(project) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Blue color
                    ) {
                        Text("Mulai Problem Framing")
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalScrollableTable(
    data: List<DataEntry>,
    onEdit: (DataEntry) -> Unit,
    onDelete: (DataEntry) -> Unit,
    onView: (DataEntry) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .horizontalScroll(scrollState)
            .padding(8.dp)
    ) {
        HeaderRow()
        data.forEachIndexed { index, entry ->
            DataRow(entry, index, onEdit, onDelete, onView)
        }
    }
}

@Composable
fun HeaderRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
    ) {
        // Sesuaikan header agar cocok dengan gambar
        val headers = listOf(
            "#", // Sesuai gambar
            "Nama Projek",
            "Deskripsi Masalah",
            "Target", // Diubah dari "Target/Tujuan"
            "Stock",
            "Inflow",
            "Diframe Oleh",
            // "Outflow", "Data Diperlukan", "Tanggal Dibuat" dihapus
        )
        headers.forEachIndexed { index, header ->
            // Sesuaikan lebar kolom agar lebih fleksibel atau sesuai kebutuhan
            val modifier = when (header) {
                "#" -> Modifier.width(60.dp)
                "Nama Projek" -> Modifier.width(150.dp)
                "Deskripsi Masalah" -> Modifier.width(200.dp)
                "Target" -> Modifier.width(150.dp)
                "Stock" -> Modifier.width(100.dp)
                "Inflow" -> Modifier.width(100.dp)
                "Diframe Oleh" -> Modifier.width(150.dp)
                else -> Modifier.width(140.dp) // Default
            }
            TableCell(header, fontWeight = FontWeight.Bold, modifier = modifier)
        }
        TableCell("Action", fontWeight = FontWeight.Bold, modifier = Modifier.width(220.dp))
    }
}

@Composable
fun DataRow(
    entry: DataEntry,
    index: Int,
    onEdit: (DataEntry) -> Unit,
    onDelete: (DataEntry) -> Unit,
    onView: (DataEntry) -> Unit
) {
    val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF9F9F9)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp)
    ) {
        // Sesuaikan data yang ditampilkan agar cocok dengan header baru
        listOf(
            "${index + 1}", // Untuk kolom #
            entry.projectName,
            entry.problemDescription,
            entry.target,
            entry.stock,
            entry.inflow,
            entry.framedBy,
            // entry.outflow, entry.dataNeeded, entry.dateCreated dihapus
        ).forEachIndexed { i, text ->
            // Sesuaikan lebar kolom agar lebih fleksibel atau sesuai kebutuhan
            val modifier = when (i) {
                0 -> Modifier.width(60.dp) // #
                1 -> Modifier.width(150.dp) // Nama Projek
                2 -> Modifier.width(200.dp) // Deskripsi Masalah
                3 -> Modifier.width(150.dp) // Target
                4 -> Modifier.width(100.dp) // Stock
                5 -> Modifier.width(100.dp) // Inflow
                6 -> Modifier.width(150.dp) // Diframe Oleh
                else -> Modifier.width(140.dp) // Default
            }
            TableCell(text, modifier = modifier)
        }

        TableCell(modifier = Modifier.width(220.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton("View", Color(0xFF88C28C)) { onView(entry) }
                ActionButton("Edit", Color(0xFF88C28C)) { onEdit(entry) }
                ActionButton("Delete", Color(0xFFE38787)) { onDelete(entry) }
            }
        }
    }
}

@Composable
fun EntryDetailView(entry: DataEntry, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {},
        dismissButton = {},
        title = {
            Text(
                text = "Detail Problem Framing",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB0006D),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                LabelValueWithGrayBackground("Nama Proyek", entry.projectName)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Deskripsi Masalah", entry.problemDescription)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Target/Tujuan", entry.target)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Stock", entry.stock)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Inflow", entry.inflow)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Outflow", entry.outflow)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Data Diperlukan", entry.dataNeeded)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Diframe Oleh", entry.framedBy)
                Spacer(modifier = Modifier.height(8.dp))

                LabelValueWithGrayBackground("Tanggal Dibuat", entry.dateCreated)
                // Spacer(modifier = Modifier.height(8.dp)) // Opsional, tergantung jarak yang diinginkan
                // Fitur Kunci TELAH DIHAPUS
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun LabelValueWithGrayBackground(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = value.ifEmpty { "-" },
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}


@Composable
fun LabelAndField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { if (value.isEmpty()) Text("") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDropdownField(
    selectedProject: String,
    onProjectSelected: (String) -> Unit,
    projectList: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedProject,
            onValueChange = {},
            label = { Text("Nama Projek") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            projectList.forEach { project ->
                DropdownMenuItem(
                    text = { Text(project) },
                    onClick = {
                        onProjectSelected(project)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier.width(140.dp)
) {
    Box(
        modifier = modifier.padding(8.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = fontWeight)
    }
}

@Composable
fun TableCell(
    modifier: Modifier = Modifier.width(140.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.padding(8.dp)
    ) {
        content()
    }
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(end = 4.dp)
            .height(32.dp)
    ) {
        Text(text, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun DeleteConfirmationDialog(
    entry: DataEntry,
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
                    color = Color(0xFFD87AB2), // pink lembut
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.projectName,
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
        confirmButton = {}, // tidak dipakai karena sudah ditangani di 'text'
        dismissButton = {}
    )
}