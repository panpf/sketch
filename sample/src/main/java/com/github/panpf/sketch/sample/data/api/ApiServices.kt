package com.github.panpf.sketch.sample.data.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class ApiServices(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val jsonConverterFactory = json.asConverterFactory(MediaType.get("application/json"))

    val giphy = Retrofit.Builder()
            .baseUrl("https://api.giphy.com")
            .addConverterFactory(jsonConverterFactory)
            .build()
            .create(GiphyService::class.java)

    val pexels = Retrofit.Builder()
        .baseUrl("https://api.pexels.com")
        .addConverterFactory(jsonConverterFactory)
        .client(OkHttpClient.Builder().addInterceptor {
            val newRequest = it.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "563492ad6f917000010000011ee9732fe9a644d0a798296f68b93c4e"
                ).build()
            it.proceed(newRequest)
        }.build())
        .build()
        .create(PexelsService::class.java)
}