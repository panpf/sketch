package com.github.panpf.sketch.request

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.height
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.width

fun Drawable.asSketchImage(): Image {
    return if (this is BitmapDrawable) {
        bitmap.asSketchImage()
    } else {
        DrawableImage(this, false)
    }
}

fun Drawable.asSketchImage(shareable: Boolean): Image {
    return if (this is BitmapDrawable) {
        bitmap.asSketchImage(shareable)
    } else {
        DrawableImage(this, shareable)
    }
}

fun Bitmap.asSketchImage(
    shareable: Boolean = isImmutable,
): Image = BitmapImage(this, shareable)

fun Image.getBitmap(): Bitmap? = when (this) {
    is BitmapImage -> bitmap
    is DrawableImage -> drawable.asOrNull<BitmapDrawable>()?.bitmap
    else -> throw IllegalArgumentException("Not supported get bitmap from Image '$this'")
}

fun Image.asDrawable(resources: Resources): Drawable = when (this) {
    is BitmapImage -> BitmapDrawable(resources, bitmap)
    is DrawableImage -> drawable
    else -> throw IllegalArgumentException("Not supported conversion to Drawable from Image '$this'")
}

interface Image {

    /** The width of the image in pixels. */
    val width: Int

    /** The height of the image in pixels. */
    val height: Int

    /** Returns the minimum number of bytes that can be used to store this bitmap's pixels. */
    val byteCount: Int

    /** Returns the size of the allocated memory used to store this bitmap's pixels.. */
    val allocationByteCount: Int

    /**
     * True if the image can be shared between multiple [Target]s at the same time.
     *
     * For example, a bitmap can be shared between multiple targets if it's immutable.
     * Conversely, an animated image cannot be shared as its internal state is being mutated while
     * its animation is running.
     */
    val shareable: Boolean
}

fun Image.findLeafImage(): Image {
    return if (this is ImageWrapper) {
        image.findLeafImage()
    } else {
        this
    }
}

open class ImageWrapper(val image: Image) : Image by image {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageWrapper

        return image == other.image
    }

    override fun hashCode(): Int {
        return image.hashCode()
    }

    override fun toString(): String {
        return "ImageWrapper(image=$image)"
    }
}

data class BitmapImage internal constructor(
    val bitmap: Bitmap,
    override val shareable: Boolean = !bitmap.isMutable
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Int = bitmap.byteCount

    override val allocationByteCount: Int = bitmap.allocationByteCountCompat
}

data class DrawableImage internal constructor(
    val drawable: Drawable,
    override val shareable: Boolean = drawable !is Animatable
) : Image {

    override val width: Int = drawable.intrinsicWidth

    override val height: Int = drawable.intrinsicHeight

    override val byteCount: Int = when (drawable) {
        is ByteCountProvider -> drawable.byteCount
        is BitmapDrawable -> drawable.bitmap.byteCount
        else -> 4 * drawable.width * drawable.height    // Estimate 4 bytes per pixel.
    }

    override val allocationByteCount: Int = when (drawable) {
        is ByteCountProvider -> drawable.allocationByteCount
        is BitmapDrawable -> drawable.bitmap.allocationByteCountCompat
        else -> 4 * drawable.width * drawable.height    // Estimate 4 bytes per pixel.
    }
}

interface ByteCountProvider {
    val byteCount: Int
    val allocationByteCount: Int
}