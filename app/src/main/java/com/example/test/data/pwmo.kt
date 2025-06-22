package com.example.test.data

import com.google.gson.annotations.SerializedName


data class ProjectWithMeaningfulObjectives(
    val id: Int,
    @SerializedName("nama_proyek") val projectName: String?, // <-- PERUBAHAN KRUSIAL DI SINI!
    @SerializedName("meaningful_objectives") val meaningfulObjectives: MeaningfulObjectives?
)