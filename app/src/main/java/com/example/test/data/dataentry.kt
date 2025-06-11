package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_entry")
data class DataEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String = "",
    val problemDescription: String = "",
    val target: String = "",
    val stock: String = "",
    val inflow: String = "",
    val outflow: String = "",
    val keyFeatures: String = ""
) {
    /** Helper used by the UI to copy the correct field with a new value */
    fun copyField(field: String, value: String): DataEntry = when (field) {
        "projectName" -> copy(projectName = value)
        "problemDescription" -> copy(problemDescription = value)
        "target" -> copy(target = value)
        "stock" -> copy(stock = value)
        "inflow" -> copy(inflow = value)
        "outflow" -> copy(outflow = value)
        "keyFeatures" -> copy(keyFeatures = value)
        else -> this
    }
}