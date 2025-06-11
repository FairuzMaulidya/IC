package com.example.test.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val username: String,
    val name: String = "",
    val dateOfBirth: String = "",
    val country: String = "",
    val region: String = "",
    val mobile: String = "",
    val photoUri: String? = null
)

