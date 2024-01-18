package com.github.panpf.sketch.sample.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging

object Client {
    val client = HttpClient {
        install(Logging)
        install(ContentNegotiation)
    }
}