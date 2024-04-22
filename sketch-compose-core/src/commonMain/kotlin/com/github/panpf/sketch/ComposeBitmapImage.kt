package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.cache.ComposeBitmapValue
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.request.internal.RequestContext


fun ComposeBitmap.asSketchImage(shareable: Boolean = true): Image {
    return ComposeBitmapImage(this, shareable)
}

@Stable
data class ComposeBitmapImage(
    val bitmap: ComposeBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = 4L * width * height

    override val allocationByteCount: Long = 4L * width * height

    override fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value = ComposeBitmapValue(this, extras)

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun getPixels(): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(pixels)
        return pixels
    }

    override fun toString(): String =
        "ComposeBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}