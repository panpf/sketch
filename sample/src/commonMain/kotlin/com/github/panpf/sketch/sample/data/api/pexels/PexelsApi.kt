package com.github.panpf.sketch.sample.data.api.pexels

import com.github.panpf.sketch.sample.data.api.BaseApi
import com.github.panpf.sketch.sample.data.api.Response
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class PexelsApi(client: HttpClient) : BaseApi(client, "https://api.pexels.com") {

    override fun commonBuilder(builder: HttpRequestBuilder) {
        super.commonBuilder(builder)
        builder.apply {
            header("Authorization", "563492ad6f917000010000011ee9732fe9a644d0a798296f68b93c4e")
        }
    }

    suspend fun curated(page: Int, size: Int): Response<PexelsCurated> {
        return execute(buildGetRequestWithPath("/v1/curated") {
            parameter("page", page)
            parameter("per_page", size)
        })
    }
}