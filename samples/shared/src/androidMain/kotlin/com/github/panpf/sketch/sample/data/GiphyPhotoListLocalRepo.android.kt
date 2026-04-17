package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.data.api.giphy.GiphyGif
import com.github.panpf.sketch.sample.data.api.giphy.GiphySearchResponse
import com.github.panpf.sketch.sample.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

actual class GiphyPhotoListLocalRepo actual constructor(val context: PlatformContext) {

    private val mutex = Mutex()
    private var cachedList: List<GiphyGif>? = null

    actual suspend fun loadFromLocalGiphyPhotoList(
        pageStart: Int,
        pageSize: Int
    ): GiphySearchResponse = withContext(Dispatchers.IO) {
        val finalCachedList = mutex.withLock {
            cachedList ?: arrayOf(
                "trending_offset0.json",
                "trending_offset1.json"
            ).flatMap { fileName ->
                val reader = context.assets.open(fileName).bufferedReader()
                val jsonString = reader.use { it.readText() }
                json.decodeFromString<GiphySearchResponse>(jsonString).dataList ?: emptyList()
            }.apply {
                this@GiphyPhotoListLocalRepo.cachedList = this
            }
        }
        val dataList = if (pageStart < finalCachedList.size) {
            finalCachedList.subList(
                fromIndex = pageStart,
                toIndex = (pageStart + pageSize).coerceAtMost(finalCachedList.size)
            )
        } else {
            emptyList()
        }
        GiphySearchResponse(dataList)
    }
}