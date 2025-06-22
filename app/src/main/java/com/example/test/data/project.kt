package com.example.test.data

import androidx.room.Entity
import androidx.room.Ignore // Penting untuk mengabaikan meaningful_objectives
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // HARUS var dan punya nilai default
    @SerializedName("name")
    var projectName: String = "", // HARUS var dan punya nilai default
    @SerializedName("description")
    var description: String? = null, // HARUS var dan punya nilai default null
    @SerializedName("status")
    var status: String? = "Pending", // HARUS var dan punya nilai default
    @SerializedName("created_by")
    var createdBy: String? = null, // HARUS var dan punya nilai default null
    @SerializedName("start_date")
    var startDate: String? = null, // HARUS var dan punya nilai default null
    @SerializedName("end_date")
    var endDate: String? = null, // HARUS var dan punya nilai default null
    @SerializedName("supervisor")
    var clientName: String? = null, // HARUS var dan punya nilai default null
    @SerializedName("location")
    var location: String? = null, // HARUS var dan punya nilai default null
    var isFromApi: Boolean = false, // HARUS var dan punya nilai default

    @Ignore // Ini sangat penting. Karena 'meaningful_objectives' adalah objek bersarang dari API,
    // Room tidak akan mencoba menyimpannya di tabel 'projects'.
    // Kita akan menyimpannya secara terpisah di tabel 'meaningful_objectives'.
    @SerializedName("meaningful_objectives")
    var meaningfulObjectives: MeaningfulObjectives? = null // HARUS var dan punya nilai default null
)