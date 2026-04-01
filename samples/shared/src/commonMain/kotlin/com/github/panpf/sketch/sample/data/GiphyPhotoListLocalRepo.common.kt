package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.giphy.GiphySearchResponse

expect class GiphyPhotoListLocalRepo(context: PlatformContext) {
    suspend fun loadFromLocalGiphyPhotoList(pageStart: Int, pageSize: Int): GiphySearchResponse
}