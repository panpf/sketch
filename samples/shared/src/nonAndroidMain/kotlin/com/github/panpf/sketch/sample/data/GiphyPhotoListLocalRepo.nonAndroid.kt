package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.giphy.GiphySearchResponse

actual class GiphyPhotoListLocalRepo actual constructor(val context: PlatformContext) {
    actual suspend fun loadFromLocalGiphyPhotoList(
        pageStart: Int,
        pageSize: Int
    ): GiphySearchResponse {
        return GiphySearchResponse(emptyList())
    }
}