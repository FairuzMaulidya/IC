package com.example.test.data

import com.google.gson.annotations.SerializedName

// Model untuk mengirim kredensial login
data class LoginRequest(
    val username: String,
    val password: String
)

// Model untuk menerima token dari API login
data class LoginResponse(
    val token: String
)
