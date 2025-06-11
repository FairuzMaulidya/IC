package com.example.test.screen

import android.app.Application
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.test.data.Project
import com.example.test.viewmodel.ProjectViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    navController: NavHostController? = null,
    projectViewModel: ProjectViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val projects by projectViewModel.allProjects.observeAsState(emptyList())

    var showForm by remember { mutableStateOf(false) }
    var projectId by remember { mutableStateOf<Int?>(null) }
    var projectName by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ongoing") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun resetForm() {
        projectId = null
        projectName = ""
        clientName = ""
        location = ""
        description = ""
        startDate = ""
        endDate = ""
        status = "Ongoing"
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (showForm) {
            Text(if (projectId == null) "Buat Proyek Baru" else "Edit Proyek", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = projectName, onValueChange = { projectName = it }, label = { Text("Project Name*") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Start Date") },
                modifier = Modifier.fillMaxWidth().clickable { showStartDatePicker = true },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date") }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("End Date") },
                modifier = Modifier.fillMaxWidth().clickable { showEndDatePicker = true },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date") }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Spacer(Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = status,
                    onValueChange = {},
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Ongoing", "Complete").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            status = it
                            expanded = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    resetForm()
                    showForm = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Batal")
                }
                Button(onClick = {
                    if (projectName.isNotBlank()) {
                        val project = Project(
                            id = projectId ?: 0,
                            projectName = projectName,
                            clientName = clientName,
                            location = location,
                            startDate = startDate,
                            endDate = endDate,
                            description = description,
                            status = status,
                            createdBy = "Admin"
                        )
                        if (projectId == null) projectViewModel.addProject(project)
                        else projectViewModel.updateProject(project)

                        resetForm()
                        showForm = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
                    Text(if (projectId == null) "Simpan" else "Perbarui", color = Color.White)
                }
            }

            if (showStartDatePicker) {
                DatePickerDialog(onDismissRequest = { showStartDatePicker = false }) {
                    startDate = it
                    showStartDatePicker = false
                }
            }
            if (showEndDatePicker) {
                DatePickerDialog(onDismissRequest = { showEndDatePicker = false }) {
                    endDate = it
                    showEndDatePicker = false
                }
            }
        } else {
            Text("Daftar Proyek", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            ProjectTable(projects = projects, onEdit = {
                projectId = it.id
                projectName = it.projectName
                clientName = it.clientName
                location = it.location ?: ""
                startDate = it.startDate
                endDate = it.endDate
                description = it.description
                status = it.status
                showForm = true
            }, onDelete = {
                projectViewModel.deleteProject(it)
                if (projectId == it.id) resetForm()
            })

            Spacer(Modifier.height(16.dp))
            Button(onClick = { showForm = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
                Spacer(Modifier.width(8.dp))
                Text("Buat Proyek Baru", color = Color.White)
            }
        }
    }
}

@Composable
fun ProjectTable(projects: List<Project>, onEdit: (Project) -> Unit, onDelete: (Project) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            Row(Modifier.background(Color.LightGray).padding(vertical = 8.dp)) {
                TableCell("No.", 40.dp)
                TableCell("Nama Proyek", 160.dp)
                TableCell("Klien", 120.dp)
                TableCell("Lokasi", 120.dp)
                TableCell("Mulai", 100.dp)
                TableCell("Selesai", 100.dp)
                TableCell("Status", 100.dp)
                TableCell("Deskripsi", 200.dp)
                TableCell("Aksi", 140.dp)
            }
            projects.forEachIndexed { index, project ->
                Row(
                    Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell("${index + 1}", 40.dp)
                    TableCell(project.projectName, 160.dp)
                    TableCell(project.clientName.ifBlank { "-" }, 120.dp)
                    TableCell(project.location?.ifBlank { "-" } ?: "-", 120.dp)
                    TableCell(project.startDate.ifBlank { "-" }, 100.dp)
                    TableCell(project.endDate.ifBlank { "-" }, 100.dp)
                    Box(modifier = Modifier.width(100.dp).background(
                        if (project.status == "Ongoing") Color(0xFFBBDEFB)
                        else if (project.status == "Complete") Color(0xFFC8E6C9)
                        else Color.White
                    ).padding(4.dp)) {
                        Text(project.status)
                    }
                    TableCell(project.description.ifBlank { "-" }, 200.dp)
                    Row(Modifier.width(140.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onEdit(project) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))) {
                            Text("Edit", color = Color.White)
                        }
                        Button(onClick = { onDelete(project) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Hapus", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(text: String, width: Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(horizontal = 8.dp),
        maxLines = 1,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun DatePickerDialog(onDismissRequest: () -> Unit, onDateSelected: (String) -> Unit) {
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember {
        mutableStateOf(
            String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        )
    }
    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp)) {
            Column {
                AndroidView(factory = { ctx ->
                    DatePicker(ctx).apply {
                        init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, y, m, d ->
                            selectedDate = String.format("%02d/%02d/%04d", d, m + 1, y)
                        }
                    }
                }, modifier = Modifier.wrapContentSize())
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text("Batal") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onDateSelected(selectedDate) }) { Text("OK") }
                }
            }
        }
    }
}
