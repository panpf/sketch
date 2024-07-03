package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.readImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext


actual suspend fun readImageInfoOrNull(
    context: PlatformContext,
    sketch: Sketch,
    uri: String,
): ImageInfo? = withContext(ioCoroutineDispatcher()) {
    runCatching {
        val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
        val dataSource = fetcher.fetch().getOrThrow().dataSource
        dataSource.readImageInfo()
    }.apply {
        if (isFailure) {
            exceptionOrNull()?.printStackTrace()
        }
    }.getOrNull()
}