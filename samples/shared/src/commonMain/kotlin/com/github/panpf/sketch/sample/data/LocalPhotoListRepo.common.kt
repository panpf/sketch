package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext

interface LocalPhotoListRepo {
    suspend fun loadLocalPhotoList(pageStart: Int, pageSize: Int): List<Photo>
}

expect class GalleryLocalPhotoListRepo(sketch: Sketch) : LocalPhotoListRepo {
    override suspend fun loadLocalPhotoList(pageStart: Int, pageSize: Int): List<Photo>
}

class BuiltinLocalPhotoListRepo(val sketch: Sketch) : LocalPhotoListRepo {

    private val list: List<ComposeResImageFile> by lazy {
        ComposeResImageFiles.statics
            .asSequence()
            .plus(ComposeResImageFiles.anims)
            .plus(ComposeResImageFiles.numbersGif)
            .plus(ComposeResImageFiles.longQMSHT)
            .plus(ComposeResImageFiles.longCOMIC)
            .plus(ComposeResImageFiles.clockExifs)
            .plus(ComposeResImageFiles.mp4)
            .toList()
    }

    val size: Int
        get() = list.size

    override suspend fun loadLocalPhotoList(pageStart: Int, pageSize: Int): List<Photo> {
        return if (pageStart < size) {
            list.subList(
                fromIndex = pageStart,
                toIndex = minOf(pageStart + pageSize, size)
            )
        } else {
            emptyList()
        }.map {
            photoUri2PhotoInfo(sketch, it.uri)
        }
    }
}

suspend fun photoUri2PhotoInfo(sketch: Sketch, uri: String): Photo {
    val imageInfo = withContext(ioCoroutineDispatcher()) {
        runCatching {
            val request = ImageRequest(sketch.context, uri = uri)
            val requestContext =
                RequestContext(sketch = sketch, initialRequest = request, size = Size.Empty)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            val fetchResult = fetcher.fetch().getOrThrow()
            val decoder = sketch.components.newDecoderOrThrow(requestContext, fetchResult)
            decoder.imageInfo
        }.apply {
            if (isFailure) {
                Exception("uri='$uri'", exceptionOrNull()).printStackTrace()
            }
        }.getOrNull()
    }
    return Photo(
        originalUrl = uri,
        mediumUrl = null,
        thumbnailUrl = null,
        width = imageInfo?.width,
        height = imageInfo?.height,
    )
}