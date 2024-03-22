package com.github.panpf.sketch.sample.data.paging

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.readImageInfoWithImageReaderOrThrow
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual suspend fun readPhotosFromPhotoAlbum(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String> {
    val userHomeDir = File(System.getProperty("user.home"))
    val userPicturesDir = File(userHomeDir, "Pictures")
    val photoList = mutableListOf<String>()
    var index = -1
    userPicturesDir.walkTopDown().forEach { file ->
        if (file.isFile) {
            val extension = file.extension
            if (extension == "jpeg" || extension == "jpg" || extension == "png" || extension == "gif") {
                index++
                if (index >= startPosition && index < startPosition + pageSize) {
                    photoList.add(file.path)
                }
                if (photoList.size >= pageSize) {
                    return@forEach
                }
            }
        }
    }
    return photoList
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