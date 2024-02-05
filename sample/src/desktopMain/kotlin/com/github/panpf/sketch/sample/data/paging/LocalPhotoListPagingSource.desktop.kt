package com.github.panpf.sketch.sample.data.paging

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.readImageInfoWithImageReaderOrThrow
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun readPhotosFromPhotoAlbum(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String> = emptyList()

actual fun isIgnoreExifOrientation(context: PlatformContext): Boolean {
//    return context.appSettingsService.ignoreExifOrientation.value
    return false    // TODO
}

actual suspend fun readImageInfoOrNull(
    context: PlatformContext,
    sketch: Sketch,
    uri: String,
    ignoreExifOrientation: Boolean
): ImageInfo? = withContext(Dispatchers.IO) {
    runCatching {
        val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
        val dataSource = fetcher.fetch().getOrThrow().dataSource
        dataSource.readImageInfoWithImageReaderOrThrow(ignoreExifOrientation)
    }.apply {
        if (isFailure) {
            exceptionOrNull()?.printStackTrace()
        }
    }.getOrNull()
}