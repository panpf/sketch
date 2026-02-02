package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun builtinImages(): List<ImageFile> {
    return ComposeResImageFiles.statics
        .asSequence()
        .plus(ComposeResImageFiles.anims)
        .plus(ComposeResImageFiles.numbersGif)
        .plus(ComposeResImageFiles.longQMSHT)
        .plus(ComposeResImageFiles.longCOMIC)
        .plus(ComposeResImageFiles.clockExifs)
        .plus(ComposeResImageFiles.mp4)
        .toList()
}

expect suspend fun localImages(context: PlatformContext): List<String>

suspend fun readImageInfoOrNull(
    sketch: Sketch,
    uri: String,
): ImageInfo? = withContext(ioCoroutineDispatcher()) {
    runCatching {
        val request = ImageRequest(sketch.context, uri)
        val requestContext = RequestContext(sketch, request, Size.Empty)
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