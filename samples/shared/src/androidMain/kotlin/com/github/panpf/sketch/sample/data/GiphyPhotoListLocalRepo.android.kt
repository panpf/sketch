package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.giphy.GiphySearchResponse
import com.github.panpf.sketch.sample.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GiphyPhotoListLocalRepo actual constructor(val context: PlatformContext) {
    actual suspend fun loadFromLocalGiphyPhotoList(
        pageStart: Int,
        pageSize: Int
    ): GiphySearchResponse {
        val fileName = when (pageStart) {
            0 -> "trending_offset0.json"
            120 -> "trending_offset1.json"
            else -> null
        } ?: return GiphySearchResponse(emptyList())
        val response = withContext(Dispatchers.IO) {
            val reader = context.assets.open(fileName).bufferedReader()
            val jsonString = reader.use { it.readText() }
            json.decodeFromString<GiphySearchResponse>(jsonString)
        }
        return response
    }
}