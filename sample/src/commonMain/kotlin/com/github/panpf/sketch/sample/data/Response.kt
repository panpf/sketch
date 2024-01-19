package com.github.panpf.sketch.sample.data

import io.ktor.client.statement.HttpResponse

interface Response<T> {

    class Success<T>(val rawResponse: HttpResponse, val body: T): Response<T>

    class Error<T>(val rawResponse: HttpResponse?, val throwable: Throwable?): Response<T>
}