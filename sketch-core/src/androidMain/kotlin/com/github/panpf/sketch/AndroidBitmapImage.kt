/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toBitmap
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toLogString

/**
 * Convert [AndroidBitmap] to [Image]
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testAsSketchImage
 */
fun AndroidBitmap.asSketchImage(
    resources: Resources? = null,
    shareable: Boolean = isImmutable
): AndroidBitmapImage = AndroidBitmapImage(this, shareable, resources)

/**
 * Convert [Image] to [Bitmap], if the conversion fails, return null
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testGetBitmapOrNull
 */
fun Image.getBitmapOrNull(): AndroidBitmap? = when (this) {
    is AndroidBitmapImage -> bitmap
    is AndroidDrawableImage -> drawable.asOrNull<BitmapDrawable>()?.bitmap
    else -> null
}

/**
 * Convert [Image] to [Bitmap], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testGetBitmapOrThrow
 */
fun Image.getBitmapOrThrow(): AndroidBitmap = getBitmapOrNull()
    ?: throw IllegalArgumentException("Unable to get Bitmap from Image '$this'")

/**
 * Create a new [Bitmap] from [Image], if the conversion fails, return null
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testToBitmapOrNull
 */
fun Image.toBitmapOrNull(): AndroidBitmap? = when (this) {
    is AndroidBitmapImage -> bitmap.copy(bitmap.config, false)
    is AndroidDrawableImage -> drawable.toBitmap()
    else -> null
}

/**
 * Create a new [Bitmap] from [Image], if the conversion fails, throw an exception
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testToBitmapOrThrow
 */
fun Image.toBitmapOrThrow(): AndroidBitmap = toBitmapOrNull()
    ?: throw IllegalArgumentException("'$this' can't be converted to Bitmap")

/**
 * Android Bitmap Image
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest
 */
data class AndroidBitmapImage internal constructor(
    val bitmap: AndroidBitmap,
    override val shareable: Boolean = !bitmap.isMutable,
    val resources: Resources? = null
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = bitmap.byteCount.toLong()

    override val allocationByteCount: Long = bitmap.allocationByteCountCompat.toLong()

    override fun cacheValue(extras: Map<String, Any?>?): Value =
        AndroidBitmapImageValue(this, extras)

    override fun checkValid(): Boolean = !bitmap.isRecycled

    override fun transformer(): ImageTransformer = AndroidBitmapImageTransformer

    override fun toString(): String =
        "AndroidBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

/**
 * Android Bitmap Image Transformer
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapImageTest.testAndroidBitmapImageTransformer
 */
internal object AndroidBitmapImageTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<AndroidBitmapImage>().bitmap
        val outBitmap = inputBitmap.scaled(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<AndroidBitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}