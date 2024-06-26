package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.ComposeBitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.painter.toLogString


fun ComposeBitmap.asSketchImage(shareable: Boolean = true): ComposeBitmapImage {
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
        extras: Map<String, Any?>?
    ): Value = ComposeBitmapImageValue(this, extras)

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