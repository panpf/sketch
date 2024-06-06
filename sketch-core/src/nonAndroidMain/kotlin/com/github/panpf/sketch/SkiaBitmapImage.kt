package com.github.panpf.sketch

import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.readIntPixels
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toLogString

fun SkiaBitmap.asSketchImage(): SkiaBitmapImage = SkiaBitmapImage(this)

data class SkiaBitmapImage(
    val bitmap: SkiaBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = (bitmap.rowBytes * bitmap.height).toLong()

    override val allocationByteCount: Long = byteCount

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer = SkiaBitmapImageTransformer()

    override fun getPixels(): IntArray? = bitmap.readIntPixels()

    override fun toString(): String =
        "SkiaBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

class SkiaBitmapImageTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<SkiaBitmapImage>().bitmap
        val outBitmap = inputBitmap.scaled(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<SkiaBitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}