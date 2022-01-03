package com.github.panpf.sketch.sample.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

class ApiServices(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val jsonConverterFactory = json.asConverterFactory(MediaType.get("application/json"))

    val unsplash = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create(UnsplashService::class.java)

    val giphy = Retrofit.Builder()
        .baseUrl("https://api.giphy.com")
        .addConverterFactory(jsonConverterFactory)
        .build()
        .create(GiphyService::class.java)
}