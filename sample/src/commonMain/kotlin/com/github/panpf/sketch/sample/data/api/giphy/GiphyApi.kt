package com.github.panpf.sketch.sample.data.api.giphy

import com.github.panpf.sketch.sample.data.api.BaseApi
import com.github.panpf.sketch.sample.data.api.Response
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter

class GiphyApi(client: HttpClient) : BaseApi(client, "https://api.giphy.com") {

    override fun commonBuilder(builder: HttpRequestBuilder) {
        super.commonBuilder(builder)
        builder.apply {
            parameter("api_key", "6e652f5378ce4df1af774034cd13a9c2")
        }
    }

    suspend fun search(
        queryWord: String,
        pageStart: Int,
        pageSize: Int
    ): Response<GiphySearchResponse> {
        return execute(buildGetRequestWithPath("/v1/gifs/search") {
            parameter("q", queryWord)
            parameter("offset", pageStart)
            parameter("limit", pageSize)
        })
    }

    suspend fun trending(pageStart: Int, pageSize: Int): Response<GiphySearchResponse> {
        return execute(buildGetRequestWithPath("/v1/gifs/trending") {
            parameter("offset", pageStart)
            parameter("limit", pageSize)
        })
    }
}