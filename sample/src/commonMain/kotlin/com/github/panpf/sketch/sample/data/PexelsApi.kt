package com.github.panpf.sketch.sample.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class PexelsApi(val client: HttpClient) {

    suspend fun curated() {
        val response = client.get("https://api.pexels.com/v1/curated") {
            header("Authorization", "563492ad6f917000010000011ee9732fe9a644d0a798296f68b93c4e")
        }
//        response.
    }
}