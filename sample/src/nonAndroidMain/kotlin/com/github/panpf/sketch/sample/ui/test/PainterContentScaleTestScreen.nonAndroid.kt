package com.github.panpf.sketch.sample.ui.test

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.sample.image.decode
import com.github.panpf.sketch.util.toScale

actual suspend fun buildPainterContentScaleTestPainters(
    context: PlatformContext,
    contentScale: ContentScale
): List<Pair<String, Painter>> {
    val list = mutableListOf<Pair<String, Painter>>()

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            list.add("ImageBitmapPainter\nImageBitmap" to this)
        }

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val resizePainter = ResizePainter(this, Size(300f, 300f), contentScale.toScale())
            list.add("ResizePainter\nImageBitmapPainter\nImageBitmap" to resizePainter)
        }

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val crossfadePainter = CrossfadePainter(null, this, contentScale = contentScale)
            list.add("CrossfadePainter\nImageBitmapPainter\nImageBitmap" to crossfadePainter)
        }

    ImageRequest(context, ResourceImages.animGif.uri)
        .decode(SkiaGifDecoder.Factory())?.image?.asPainter()?.apply {
            list.add("AnimatedImagePainter\nAnimatedImage" to this)
        }

    return list
}