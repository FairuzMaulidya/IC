package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_entry")
data class DataEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String = "",
    val problemDescription: String = "", // Deskripsi Masalah
    val target: String = "",             // Target/Tujuan
    val stock: String = "",
    val inflow: String = "",
    val outflow: String = "",
    val dataNeeded: String = "",         // Data Diperlukan (NEW)
    val framedBy: String = "",           // Diframe Oleh (NEW)
    val dateCreated: String = ""        // Tanggal Dibuat (NEW)
) {
    /** Helper used by the UI to copy the correct field with a new value */
    fun copyField(field: String, value: String): DataEntry = when (field) {
        "projectName" -> copy(projectName = value)
        "problemDescription" -> copy(problemDescription = value)
        "target" -> copy(target = value)
        "stock" -> copy(stock = value)
        "inflow" -> copy(inflow = value)
        "outflow" -> copy(outflow = value)
        "dataNeeded" -> copy(dataNeeded = value) // NEW
        "framedBy" -> copy(framedBy = value)     // NEW
        "dateCreated" -> copy(dateCreated = value) // NEW
        else -> this
    }
}