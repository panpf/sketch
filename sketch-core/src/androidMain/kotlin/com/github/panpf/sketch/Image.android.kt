/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.BitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.decode.internal.toLogString
import com.github.panpf.sketch.drawable.internal.toLogString
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.height
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.util.width

fun Bitmap.asSketchImage(
    resources: Resources? = null,
    shareable: Boolean = isImmutable
): BitmapImage {
    return BitmapImage(this, shareable, resources)
}

fun Drawable.asSketchImage(shareable: Boolean = this !is Animatable): DrawableImage {
    return DrawableImage(this, shareable)
}

fun Image.getBitmapOrNull(): Bitmap? = when (this) {
    is BitmapImage -> bitmap
    is DrawableImage -> drawable.asOrNull<BitmapDrawable>()?.bitmap
    else -> null
}

fun Image.getBitmapOrThrow(): Bitmap = getBitmapOrNull()
    ?: throw IllegalArgumentException("Unable to get Bitmap from Image '$this'")

fun Image.getDrawableOrNull(): Drawable? = when (this) {
    is BitmapImage -> null
    is DrawableImage -> drawable
    else -> null
}

fun Image.getDrawableOrThrow(): Drawable = getDrawableOrNull()
    ?: throw IllegalArgumentException("Unable to get Drawable from Image '$this'")

fun Image.asDrawableOrThrow(): Drawable = when (this) {
    is BitmapImage -> BitmapDrawable(resources, bitmap)
    is DrawableImage -> drawable
    else -> throw IllegalArgumentException("'$this' can't be converted to Drawable")
}

@Stable
actual interface Image {

    /** The width of the image in pixels. */
    actual val width: Int

    /** The height of the image in pixels. */
    actual val height: Int

    /** Returns the minimum number of bytes that can be used to store this bitmap's pixels. */
    actual val byteCount: Int

    /** Returns the size of the allocated memory used to store this bitmap's pixels.. */
    actual val allocationByteCount: Int

    /**
     * True if the image can be shared between multiple [Target]s at the same time.
     *
     * For example, a bitmap can be shared between multiple targets if it's immutable.
     * Conversely, an animated image cannot be shared as its internal state is being mutated while
     * its animation is running.
     */
    actual val shareable: Boolean

    actual fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value?

    actual fun checkValid(): Boolean

    actual fun transformer(): ImageTransformer?
}

@Stable
data class BitmapImage internal constructor(
    val bitmap: Bitmap,
    override val shareable: Boolean = !bitmap.isMutable,
    val resources: Resources? = null
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Int = bitmap.byteCount

    override val allocationByteCount: Int = bitmap.allocationByteCountCompat

    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value =
        BitmapImageValue(this, extras)

    override fun checkValid(): Boolean = !bitmap.isRecycled

    override fun transformer(): ImageTransformer = BitmapImageTransformer()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BitmapImage) return false
        if (bitmap != other.bitmap) return false
        if (shareable != other.shareable) return false
        return resources == other.resources
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + shareable.hashCode()
        result = 31 * result + (resources?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "BitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

class BitmapImageTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<BitmapImage>().bitmap
        val outBitmap = inputBitmap.scale(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<BitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}

@Stable
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

    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value? =
        null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun toString(): String {
        return "DrawableImage(drawable=${drawable.toLogString()}, shareable=$shareable)"
    }
}