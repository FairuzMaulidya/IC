package com.example.test.network

import com.example.test.model.Content
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private val json = Json {
    ignoreUnknownKeys = true
}

private val contentType = "application/json".toMediaType()

private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.24.80.135:8000/api-content/") // hanya satu deklarasi!
    .addConverterFactory(json.asConverterFactory(contentType))
    .build()

interface ContentApiService {
    @GET("content/")
    suspend fun getContents(): List<Content>
}

object ContentApi {
    val retrofitService: ContentApiService by lazy {
        retrofit.create(ContentApiService::class.java)
    }
}
