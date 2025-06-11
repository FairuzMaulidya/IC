package com.example.test.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.test.data.DatasetRequest
import com.example.test.data.Project
import com.example.test.viewmodel.DatasetViewModel
import com.example.test.viewmodel.ProjectViewModel

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
    val projects by projectViewModel.allProjects.observeAsState(initial = emptyList())

    when (currentPage) {
        1 -> {
            // Halaman 1: Pilih Proyek
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Pilih Proyek untuk Membuat Permintaan Dataset",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))

                val scrollState = rememberScrollState()

                Column(modifier = Modifier.horizontalScroll(scrollState)) {
                    // Header Tabel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFEFEF))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#", modifier = Modifier.width(40.dp))
                        Text("Nama Proyek", modifier = Modifier.width(150.dp))
                        Text("Deskripsi", modifier = Modifier.width(200.dp))
                        Text("Status", modifier = Modifier.width(100.dp))
                        Text("Aksi", modifier = Modifier.width(100.dp))
                    }

                    Divider(color = Color.Gray, thickness = 1.dp)

                    LazyColumn {
                        itemsIndexed(projects) { index, project ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", modifier = Modifier.width(40.dp))
                                Text(project.projectName, modifier = Modifier.width(150.dp))
                                Text(project.description, modifier = Modifier.width(200.dp))

                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .background(
                                            if (project.status == "Completed") Color(0xFF4CAF50)
                                            else Color(0xFF03A9F4),
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = project.status,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedProject = project
                                        currentPage = 2
                                    },
                                    modifier = Modifier.width(100.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Request")
                                }
                            }
                            Divider(color = Color.LightGray)
                        }
                    }
                }
            }
        }

        2 -> {
            // Halaman 2: Form Permintaan Dataset
            selectedProject?.let { project ->
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Form Permintaan Dataset: ${project.projectName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deskripsiMasalah,
                        onValueChange = { deskripsiMasalah = it },
                        label = { Text("Deskripsi Masalah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Target / Tujuan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tipeData,
                        onValueChange = { tipeData = it },
                        label = { Text("Tipe Data") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = aktivitasPemrosesan,
                        onValueChange = { aktivitasPemrosesan = it },
                        label = { Text("Aktivitas Pemrosesan Data") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = estimasiJumlahFitur,
                        onValueChange = { estimasiJumlahFitur = it },
                        label = { Text("Estimasi Jumlah Fitur") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = estimasiUkuranDataset,
                        onValueChange = { estimasiUkuranDataset = it },
                        label = { Text("Estimasi Ukuran Dataset") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formatFile,
                        onValueChange = { formatFile = it },
                        label = { Text("Format File") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tanggalMulai,
                        onValueChange = { tanggalMulai = it },
                        label = { Text("Tanggal Mulai Dibutuhkan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tanggalSelesai,
                        onValueChange = { tanggalSelesai = it },
                        label = { Text("Tanggal Selesai Dibutuhkan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { currentPage = 1 }) {
                            Text("Kembali")
                        }
                        Button(onClick = {
                            datasetViewModel.addReqDataset(
                                DatasetRequest(
                                    projectName = project.projectName,
                                    description = project.description,
                                    featureCount = estimasiJumlahFitur.toIntOrNull() ?: 0,
                                    datasetSize = estimasiUkuranDataset,
                                    expectedFileFormat = formatFile,
                                    descriptionn = deskripsiMasalah,
                                    dataType = tipeData,
                                    dataProcessing = aktivitasPemrosesan,
                                    startDate = tanggalMulai,
                                    endDate = tanggalSelesai,
                                    target = target,
                                    requestedBy = "", // akan diisi dengan user login
                                    status = "Pending"
                                )
                            )

                            // Reset form
                            deskripsiMasalah = ""
                            target = ""
                            tipeData = ""
                            aktivitasPemrosesan = ""
                            estimasiJumlahFitur = ""
                            estimasiUkuranDataset = ""
                            formatFile = ""
                            tanggalMulai = ""
                            tanggalSelesai = ""

                            currentPage = 3
                        }) {
                            Text("Kirim Permintaan")
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

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .fillMaxWidth()
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#", modifier = Modifier.width(40.dp))
                        Text("PROYEK", modifier = Modifier.width(100.dp))
                        Text("DESKRIPSI MASALAH", modifier = Modifier.width(200.dp))
                        Text("TIPE DATA", modifier = Modifier.width(100.dp))
                        Text("FORMAT", modifier = Modifier.width(80.dp))
                        Text("TGL MULAI", modifier = Modifier.width(100.dp))
                        Text("TGL SELESAI", modifier = Modifier.width(100.dp))
                        Text("STATUS", modifier = Modifier.width(100.dp))
                        Text("DIMINTA OLEH", modifier = Modifier.width(100.dp))
                    }

                    Divider(color = Color.Gray)

                    LazyColumn {
                        itemsIndexed(datasetRequests) { index, request ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", modifier = Modifier.width(40.dp))
                                Text(request.projectName, modifier = Modifier.width(100.dp))
                                Text(request.descriptionn.take(40) + "...", modifier = Modifier.width(200.dp))
                                Text(request.dataType, modifier = Modifier.width(100.dp))
                                Text(request.expectedFileFormat, modifier = Modifier.width(80.dp))
                                Text(request.startDate, modifier = Modifier.width(100.dp))
                                Text(request.endDate ?: "-", modifier = Modifier.width(100.dp))

                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .background(
                                            if (request.status == "Pending") Color(0xFFFFC107) else Color(0xFF4CAF50),
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = request.status,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(request.requestedBy, modifier = Modifier.width(100.dp))
                            }
                            Divider(color = Color.LightGray)
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
        }
    }
}
