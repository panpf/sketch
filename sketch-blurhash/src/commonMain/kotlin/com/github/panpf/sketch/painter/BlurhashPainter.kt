package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.BASE_COLOR_TYPE
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.fetch.BlurhashUtil
import com.github.panpf.sketch.util.asComposeImageBitmap
import com.github.panpf.sketch.util.installPixels
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlin.math.ceil

/**
 * Blurhash painter that decodes and draws blurhash strings
 */
@Stable
class BlurhashPainter(
    val blurhash: String,
    val size: Size = Size.Unspecified,
) : Painter(), SketchPainter {

    val imageSizeAndBitmapLock = SynchronizedObject()
    private var imageSizeAndBitmap: Pair<IntSize, ImageBitmap>? = null

    override fun DrawScope.onDraw() {
        val dstSize = IntSize(
            width = ceil(size.width).toInt(),
            height = ceil(size.height).toInt()
        )
        val imageSizeAndBitmapLocal = imageSizeAndBitmap
        if (imageSizeAndBitmapLocal == null || imageSizeAndBitmapLocal.first != dstSize) {
            val pixelData = BlurhashUtil.decodeByte(blurhash, dstSize.width, dstSize.height)
            val bitmap = createBitmap(dstSize.width, dstSize.height, BASE_COLOR_TYPE)
            bitmap.installPixels(pixelData)
            imageSizeAndBitmap = dstSize to bitmap.asComposeImageBitmap()
        }
        drawImage(imageSizeAndBitmap!!.second, dstSize = dstSize)
    }

    override val intrinsicSize: Size = size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurhashPainter
        if (blurhash != other.blurhash) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = blurhash.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlurhashPainter(blurhash=$blurhash, size=$size)"
    }
}