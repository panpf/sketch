package com.github.panpf.sketch.compose.request

import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ByteCountProvider
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.internal.RequestContext
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlin.math.roundToInt

fun Painter.asSketchImage(shareable: Boolean = false): Image {
    return PainterImage(this, shareable)
}

fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is BitmapImage -> BitmapPainter(bitmap.asImageBitmap())
    is DrawableImage -> DrawablePainter(drawable.mutate())
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}

data class PainterImage(val painter: Painter, override val shareable: Boolean = false) : Image {

    override val width: Int
        get() = painter.intrinsicSize.width.roundToInt()

    override val height: Int
        get() = painter.intrinsicSize.height.roundToInt()

    override val byteCount: Int
        get() = when (painter) {
            is DrawablePainter -> {
                when (val drawable = painter.drawable) {
                    is ByteCountProvider -> drawable.byteCount
                    is BitmapDrawable -> drawable.bitmap.byteCount
                    else -> 4 * width * height    // Estimate 4 bytes per pixel.
                }
            }

            else -> 4 * width * height
        }

    override val allocationByteCount: Int
        get() = when (painter) {
            is DrawablePainter -> {
                when (val drawable = painter.drawable) {
                    is ByteCountProvider -> drawable.allocationByteCount
                    is BitmapDrawable -> drawable.bitmap.allocationByteCount
                    else -> 4 * width * height    // Estimate 4 bytes per pixel.
                }
            }

            else -> 4 * width * height
        }

    override fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value? = null
}