package com.example.test.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Int,
    @SerialName("data_created") val dataCreated: String,
    val artikel: String,
    val author: Int
)
