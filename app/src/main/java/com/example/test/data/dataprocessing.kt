package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "data_processing")
data class DataProcessing(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectName: String, // Nama proyek yang terkait dengan pemrosesan data
    val sourceData: String, // Sumber data asli yang digunakan (misal: "API", "CSV", "Database")
    val transformationSteps: String, // Langkah-langkah detail tentang bagaimana data diubah/transformasi
    val featureEngineering: String, // Detail tentang proses rekayasa fitur (pembuatan fitur baru dari data yang ada)
    val processedFileLocation: String, // Lokasi tempat file data yang sudah diproses disimpan (misal: path folder lokal, URL cloud storage)
    val processedFileName: String, // Nama file dari data yang telah diproses (untuk representasi "upload file")
    val processingStatus: String, // Status pemrosesan data (misal: "Selesai", "Dalam Proses", "Gagal", "Menunggu")
    val createdAt: String = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date()) // Waktu entri ini dibuat
)