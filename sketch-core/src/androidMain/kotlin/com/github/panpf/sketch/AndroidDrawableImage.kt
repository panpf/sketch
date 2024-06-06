package com.github.panpf.sketch

import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.drawable.internal.toLogString
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.heightWithBitmapFirst
import com.github.panpf.sketch.util.widthWithBitmapFirst

fun Drawable.asSketchImage(shareable: Boolean = this !is Animatable): AndroidDrawableImage {
    return AndroidDrawableImage(this, shareable)
}

fun Image.getDrawableOrNull(): Drawable? = when (this) {
    is AndroidBitmapImage -> null
    is AndroidDrawableImage -> drawable
    else -> null
}

fun Image.getDrawableOrThrow(): Drawable = getDrawableOrNull()
    ?: throw IllegalArgumentException("Unable to get Drawable from Image '$this'")

fun Image.asDrawableOrNull(): Drawable? = when (this) {
    is AndroidBitmapImage -> BitmapDrawable(resources, bitmap)
    is AndroidDrawableImage -> drawable
    else -> null
}

fun Image.asDrawableOrThrow(): Drawable = asDrawableOrNull()
    ?: throw IllegalArgumentException("'$this' can't be converted to Drawable")

data class AndroidDrawableImage internal constructor(
    val drawable: Drawable,
    override val shareable: Boolean = drawable !is Animatable
) : Image {

    override val width: Int = drawable.intrinsicWidth

    override val height: Int = drawable.intrinsicHeight

    override val byteCount: Long = when (drawable) {
        is ByteCountProvider -> drawable.byteCount
        is BitmapDrawable -> drawable.bitmap.byteCount.toLong()
        else -> 4L * drawable.widthWithBitmapFirst * drawable.heightWithBitmapFirst    // Estimate 4 bytes per pixel.
    }

    override val allocationByteCount: Long = when (drawable) {
        is ByteCountProvider -> drawable.allocationByteCount
        is BitmapDrawable -> drawable.bitmap.allocationByteCountCompat.toLong()
        else -> 4L * drawable.widthWithBitmapFirst * drawable.heightWithBitmapFirst    // Estimate 4 bytes per pixel.
    }

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun toString(): String =
        "AndroidDrawableImage(drawable=${drawable.toLogString()}, shareable=$shareable)"

    override fun getPixels(): IntArray? {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            return pixels
        }
        return null
    }
}