package com.github.panpf.sketch.sample.ui.test

import android.os.Build
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.images.ResourceImages
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

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            list.add("ImageBitmapPainter\nImageBitmap" to this)
        }

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val resizePainter = ResizePainter(this, Size(300f, 300f), contentScale, alignment)
            list.add("ResizePainter\nImageBitmapPainter\nImageBitmap" to resizePainter)
        }

    ImageRequest(context, ResourceImages.clockHor.uri).execute()
        .let { it as ImageResult.Success }
        .image.asPainter().apply {
            val crossfadePainter =
                CrossfadePainter(null, this, contentScale = contentScale, alignment = alignment)
            list.add("CrossfadePainter\nImageBitmapPainter\nImageBitmap" to crossfadePainter)
        }

    ImageRequest(context, ResourceImages.animGif.uri)
        .decode(MovieGifDecoder.Factory())?.image?.asPainter()?.apply {
            list.add("DrawableAnimatablePainter\nAnimatableDrawable\nMovieDrawable" to this)
        }

    ImageRequest(context, ResourceImages.animGif.uri)
        .decode(KoralGifDecoder.Factory())?.image?.asPainter()?.apply {
            list.add("DrawableAnimatablePainter\nAnimatableDrawable\nGifDrawableWrapperDrawable" to this)
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageRequest(context, ResourceImages.animGif.uri)
            .decode(ImageDecoderGifDecoder.Factory())?.image?.asPainter()?.apply {
                list.add("DrawableAnimatablePainter\nScaledAnimatableDrawable\nAnimatedImageDrawable" to this)
            }
    }

    return list
}