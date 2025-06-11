package com.example.test.screen

import DataEntryViewModel
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // Import for vertical scrolling
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.test.data.DataEntry
import com.example.test.data.Project
import com.example.test.viewmodel.ProjectViewModel
import com.google.accompanist.flowlayout.FlowRow
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
    val projectList by projectViewModel.allProjects.observeAsState(emptyList())

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
            .verticalScroll(verticalScrollState)
    ) {
        if (selectedProject == null) {
            Text("Pilih Proyek untuk Problem Framing", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ProjectTable(projects = projectList, onStart = {
                selectedProject = it
                entryState.value = DataEntry(projectName = it.projectName)
                showForm.value = true
            })
        } else {
            val filteredEntries = allEntries.filter {
                it.projectName == selectedProject!!.projectName &&
                        (query.isBlank() || it.problemDescription.contains(query, true) ||
                                it.target.contains(query, true) || it.keyFeatures.contains(query, true))
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Proyek: ${selectedProject!!.projectName}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    selectedProject = null
                    showForm.value = false
                }) {
                    Text("Kembali Pilih Proyek")
                }
            }

            Spacer(Modifier.height(8.dp))

            if (showForm.value) {
                DataEntryForm(
                    entry = entryState.value,
                    onEntryChange = { field, value ->
                        entryState.value = entryState.value.copyField(field, value)
                    },
                    onSave = {
                        if (entryState.value.id == 0) viewModel.insertEntry(entryState.value)
                        else viewModel.updateEntry(entryState.value)
                        showForm.value = false
                    },
                    onClose = { showForm.value = false },
                    onDelete = {
                        viewModel.deleteEntry(entryState.value)
                        showForm.value = false
                    },
                    navController = navController
                )
            } else {
                Button(onClick = {
                    entryState.value = DataEntry(projectName = selectedProject!!.projectName)
                    showForm.value = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(" Tambah Data Entry")
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Cari...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
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

@Composable
fun DataEntryForm(
    entry: DataEntry,
    onEntryChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Data Entry",
                fontSize = 20.sp,
                color = Color(0xFFB0006D)
            )
            Divider(color = Color(0xFFB0006D), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Nama Proyek: ${entry.projectName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(12.dp))

            LabelAndField("Deskripsi Masalah", entry.problemDescription) {
                onEntryChange("problemDescription", it)
            }
            LabelAndField("Target / Tujuan", entry.target) {
                onEntryChange("target", it)
            }
            LabelAndField("Stock", entry.stock) {
                onEntryChange("stock", it)
            }
            LabelAndField("Inflow", entry.inflow) {
                onEntryChange("inflow", it)
            }
            LabelAndField("Outflow", entry.outflow) {
                onEntryChange("outflow", it)
            }
            LabelAndField("Fitur Kunci", entry.keyFeatures) {
                onEntryChange("keyFeatures", it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onSave) {
                    Text("Simpan")
                }
                if (entry.id != 0) {
                    Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("Hapus")
                    }
                }
                OutlinedButton(onClick = onClose) {
                    Text("Batal")
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
                TableCell(project.description, modifier = Modifier.width(240.dp))
                TableCell(project.location, modifier = Modifier.width(160.dp))
                TableCell(
                    project.status,
                    modifier = Modifier.width(120.dp),
                    fontWeight = FontWeight.Medium
                )
                TableCell(modifier = Modifier.width(180.dp)) {
                    Button(onClick = { onStart(project) }) {
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
        val headers = listOf(
            "ID",
            "Nama Projek",
            "Deskripsi Masalah",
            "Target/Tujuan",
            "Stock",
            "Inflow",
            "Outflow",
            "Fitur Kunci"
        )
        headers.forEach { TableCell(it, fontWeight = FontWeight.Bold) }
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
        listOf(
            entry.id.toString(),
            entry.projectName,
            entry.problemDescription,
            entry.target,
            entry.stock,
            entry.inflow,
            entry.outflow,
            entry.keyFeatures
        ).forEach { TableCell(it) }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryForm(
    entry: DataEntry,
    onEntryChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    navController: NavHostController,
    projectList: List<String>
) {
    var selectedProject by remember { mutableStateOf(entry.projectName) }
    // Add a vertical scroll state for the DataEntryForm
    // Form ini juga sudah di dalam Column utama yang scrollable,
    // jadi Modifier.verticalScroll di sini juga perlu dipertimbangkan.
    // Jika form ini hanya muncul sebagai pop-up/dialog, maka tidak perlu scrollable,
    // tapi karena di sini ditempatkan langsung di Column utama,
    // kita bisa biarkan Column utama yang menangani scroll keseluruhan.
    // Namun, untuk form yang mungkin sangat panjang, memiliki scroll sendiri juga bisa diperlukan
    // tergantung desain UI akhir. Untuk menghindari nesting scrollable yang tidak perlu
    // dan sesuai dengan error yang muncul, kita hapus Modifier.verticalScroll() dari DataEntryForm
    // asalkan Column induk DataEntryScreen sudah menanganinya.
    val formScrollState = rememberScrollState() // Masih bisa dideklarasikan, tapi tidak dipakai di Modifier.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                .padding(16.dp)
            // HAPUS Modifier.verticalScroll() dari sini juga jika Column di DataEntryScreen sudah scrollable
            // .verticalScroll(formScrollState) // Hapus baris ini
        ) {
            Text(
                text = "Data Entry",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB0006D),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(color = Color(0xFFB0006D), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Gunakan komponen dropdown yang sudah optimal
            ProjectDropdownField(
                selectedProject = selectedProject,
                onProjectSelected = {
                    selectedProject = it
                    onEntryChange("projectName", it)
                },
                projectList = projectList
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field lainnya
            LabelAndField("Deskripsi Masalah", entry.problemDescription) {
                onEntryChange("problemDescription", it)
            }
            LabelAndField("Target / Tujuan", entry.target) {
                onEntryChange("target", it)
            }
            LabelAndField("Stock", entry.stock) {
                onEntryChange("stock", it)
            }
            LabelAndField("Inflow", entry.inflow) {
                onEntryChange("inflow", it)
            }
            LabelAndField("Outflow", entry.outflow) {
                onEntryChange("outflow", it)
            }
            LabelAndField("Fitur Kunci", entry.keyFeatures) {
                onEntryChange("keyFeatures", it)
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
fun EntryDetailView(entry: DataEntry, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                LabelValue(label = "Project Name", value = entry.projectName)
                LabelValue(label = "Problem Description", value = entry.problemDescription)
                LabelValue(label = "Target", value = entry.target)
                LabelValue(label = "Stock", value = entry.stock)
                LabelValue(label = "Inflow", value = entry.inflow)
                LabelValue(label = "Outflow", value = entry.outflow)
                LabelValue(label = "Key Features", value = entry.keyFeatures)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    )
}


@Composable
fun LabelValue(label: String, value: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    Column(modifier.padding(bottom = 8.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
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



@Composable
fun LabelAndField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
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