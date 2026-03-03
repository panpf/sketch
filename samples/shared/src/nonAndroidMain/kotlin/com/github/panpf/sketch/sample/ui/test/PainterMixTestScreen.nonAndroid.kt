package com.github.panpf.sketch.sample.ui.test

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.sample.image.decode

actual suspend fun buildPainterContentScaleTestPainters(
    context: PlatformContext,
    contentScale: ContentScale,
    alignment: Alignment,
): List<Pair<String, Painter>> {
    val list = mutableListOf<Pair<String, Painter>>()

    ImageRequest(context, ComposeResImageFiles.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            list.add("ImageBitmapPainter\nImageBitmap" to this)
        }

    ImageRequest(context, ComposeResImageFiles.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val resizePainter = ResizePainter(this, Size(300f, 300f), contentScale, alignment)
            list.add("ResizePainter\nImageBitmapPainter\nImageBitmap" to resizePainter)
        }

    ImageRequest(context, ComposeResImageFiles.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val crossfadePainter =
                CrossfadePainter(null, this, contentScale = contentScale, alignment = alignment)
            list.add("CrossfadePainter\nImageBitmapPainter\nImageBitmap" to crossfadePainter)
        }

    ImageRequest(context, ComposeResImageFiles.animGif.uri)
        .decode(SkiaGifDecoder.Factory())?.image?.asPainter()?.apply {
            list.add("AnimatedImagePainter\nAnimatedImage" to this)
        }

    return list
}