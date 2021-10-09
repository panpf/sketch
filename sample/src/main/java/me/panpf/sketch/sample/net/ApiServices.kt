package me.panpf.sketch.sample.net

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServices(private val context: Context) {

    val unsplash = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UnsplashService::class.java)

    val giphy = Retrofit.Builder()
        .baseUrl("https://api.giphy.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GiphyService::class.java)
}