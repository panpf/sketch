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
            parameter("api_key", "Gc7131jiJuvI7IdN0HZ1D7nh0ow5BU6g")
            parameter("pingback_id", "17c5f87f46b18d99")
        }
    }

    suspend fun search(
        queryWord: String,
        pageStart: Int,
        pageSize: Int
    ): Response<GiphySearchResponse> {
        return execute(buildGetRequestWithPath("/v1/gifs/search?type=gifs&sort=") {
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