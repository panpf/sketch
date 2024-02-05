package com.github.panpf.sketch.sample.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

abstract class BaseApi(val client: HttpClient, val baseUrl: String) {

    protected open fun commonBuilder(builder: HttpRequestBuilder) {

    }

    protected fun buildGetRequestWithPath(
        @Suppress("SameParameterValue") path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpRequestBuilder = buildRequestWithPath(path) {
        method = HttpMethod.Get
        block()
    }

    private fun buildRequestWithPath(
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpRequestBuilder = HttpRequestBuilder().apply {
        url(baseUrl + path)
        commonBuilder(this)
        block()
    }

    protected suspend inline fun <reified T> execute(request: HttpRequestBuilder): Response<T> {
        val result = runCatching {
            client.request(request)
        }
        val rawResponse = result.getOrNull()
        return when {
            rawResponse == null -> Response.Error(null, result.exceptionOrNull())
            rawResponse.status.value == 200 -> Response.Success(rawResponse, rawResponse.body<T>())
            else -> Response.Error(rawResponse, null)
        }
    }
}