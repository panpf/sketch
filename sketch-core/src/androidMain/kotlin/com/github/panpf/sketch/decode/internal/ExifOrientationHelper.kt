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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorSpace
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.flip
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.rotateInSpace
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.safeToSoftware
import okio.buffer
import java.io.IOException
import kotlin.math.abs

/**
 * Read the Exif orientation attribute of the image
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.ExifOrientationHelperTest.testReadExifOrientation
 */
@Throws(IOException::class)
fun DataSource.readExifOrientation(): Int =
    openSource().buffer().inputStream().use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }

/**
 * Read the Exif orientation attribute of the image, if the mimeType is not supported, return [ExifInterface.ORIENTATION_UNDEFINED]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.ExifOrientationHelperTest.testReadExifOrientationWithMimeType
 */
@Throws(IOException::class)
fun DataSource.readExifOrientationWithMimeType(mimeType: String): Int =
    if (ExifInterface.isSupportedMimeType(mimeType)) {
        readExifOrientation()
    } else {
        ExifInterface.ORIENTATION_UNDEFINED
    }

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.ExifOrientationHelperTest
 */
class ExifOrientationHelper constructor(val exifOrientation: Int) {

    init {
        require(values.any { it == exifOrientation }) { "Invalid exifOrientation: $exifOrientation" }
    }

    /**
     * Returns if the current image orientation is flipped.
     */
    val isFlipHorizontally: Boolean = exifOrientation == FLIP_HORIZONTAL
            || exifOrientation == FLIP_VERTICAL
            || exifOrientation == TRANSVERSE
            || exifOrientation == TRANSPOSE

    /**
     * Returns the rotation degrees for the current image orientation. If the image is flipped,
     * i.e., [.isFlipped] returns `true`, the rotation degrees will be base on
     * the assumption that the image is first flipped horizontally (along Y-axis), and then do
     * the rotation. For example, [.TRANSPOSE] will be interpreted as flipped
     * horizontally first, and then rotate 270 degrees clockwise.
     *
     * @return The rotation degrees of the image after the horizontal flipping is applied, if any.
     *
     * @see isFlipHorizontally
     */
    val rotationDegrees: Int = when (exifOrientation) {
        ROTATE_90,
        TRANSVERSE -> 90

        ROTATE_180,
        FLIP_VERTICAL -> 180

        ROTATE_270,
        TRANSPOSE -> 270

        UNDEFINED,
        NORMAL,
        FLIP_HORIZONTAL -> 0

        else -> 0
    }

    fun applyToSize(size: Size, reverse: Boolean = false): Size {
        return size.rotate(if (!reverse) rotationDegrees else -rotationDegrees)
    }

    fun applyToRect(srcRect: Rect, spaceSize: Size, reverse: Boolean = false): Rect {
        val isRotated = abs(rotationDegrees % 360) != 0
        return if (!reverse) {
            srcRect
                .let { if (this.isFlipHorizontally) it.flip(spaceSize, vertical = false) else it }
                .let { if (isRotated) it.rotateInSpace(spaceSize, rotationDegrees) else it }
        } else {
            val rotatedImageSize = spaceSize.rotate(-rotationDegrees)
            srcRect
                .let { if (isRotated) it.rotateInSpace(spaceSize, -rotationDegrees) else it }
                .let {
                    if (this.isFlipHorizontally) it.flip(
                        rotatedImageSize,
                        vertical = false
                    ) else it
                }
        }
    }

    fun applyToBitmap(bitmap: Bitmap, reverse: Boolean = false): Bitmap? {
        if (!isFlipHorizontally && abs(rotationDegrees % 360) == 0) {
            return null
        }

        val matrix = Matrix().apply {
            if (!reverse) {
                if (isFlipHorizontally) {
                    this.postScale(-1f, 1f)
                }
                this.postRotate(rotationDegrees.toFloat())
            } else {
                this.postRotate(-rotationDegrees.toFloat())
                if (isFlipHorizontally) {
                    this.postScale(-1f, 1f)
                }
            }
        }

        val newBitmapSize = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
            .apply {
                matrix.mapRect(this)
                matrix.postTranslate(-this.left, -this.top)
            }.let {
                Size(it.width().toInt(), it.height().toInt())
            }
        val newBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Bitmap.createBitmap(
                /* width = */ newBitmapSize.width,
                /* height = */ newBitmapSize.height,
                /* config = */ bitmap.safeConfig.safeToSoftware(),
                /* hasAlpha = */ bitmap.hasAlpha(),
                /* colorSpace = */ bitmap.colorSpace ?: ColorSpace.get(ColorSpace.Named.SRGB)
            )
        } else {
            Bitmap.createBitmap(
                /* width = */ newBitmapSize.width,
                /* height = */ newBitmapSize.height,
                /* config = */ bitmap.safeConfig.safeToSoftware(),
            )
        }

        val canvas = Canvas(newBitmap)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bitmap, matrix, paint)
        return newBitmap
    }

    companion object {
        /**
         * No orientation defined
         *
         * * flip horizontally: false
         * * rotate: 0
         */
        const val UNDEFINED = 0

        /**
         * Orientation is normal
         *
         * * flip horizontally: false
         * * rotate: 0
         */
        const val NORMAL = 1

        /**
         * Indicates the image is left right reversed mirror.
         *
         * * flip horizontally: true
         * * rotate: 0
         */
        const val FLIP_HORIZONTAL = 2

        /**
         * Indicates the image is rotated by 180 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 180
         */
        const val ROTATE_180 = 3

        /**
         * Indicates the image is upside down mirror, it can also be represented by flip horizontally firstly and rotate 180 degree clockwise.
         *
         * * flip horizontally: true
         * * rotate: 180
         */
        const val FLIP_VERTICAL = 4

        /**
         * Indicates the image is flipped about top-left <--> bottom-right axis, it can also be
         * represented by flip horizontally firstly and rotate 270 degree clockwise.
         *
         * * flip horizontally: true
         * * rotate: 270
         */
        const val TRANSPOSE = 5

        /**
         * Indicates the image is rotated by 90 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 90
         */
        const val ROTATE_90 = 6

        /**
         * Indicates the image is flipped about top-right <--> bottom-left axis, it can also be
         * represented by flip horizontally firstly and rotate 90 degree clockwise.
         * * flip horizontally: true
         * * rotate: 90
         */
        const val TRANSVERSE = 7

        /**
         * Indicates the image is rotated by 270 degree clockwise.
         *
         * * flip horizontally: false
         * * rotate: 270
         */
        const val ROTATE_270 = 8

        fun name(exifOrientation: Int): String =
            when (exifOrientation) {
                UNDEFINED -> "UNDEFINED"
                NORMAL -> "NORMAL"
                FLIP_HORIZONTAL -> "FLIP_HORIZONTAL"
                ROTATE_180 -> "ROTATE_180"
                FLIP_VERTICAL -> "FLIP_VERTICAL"
                TRANSPOSE -> "TRANSPOSE"
                ROTATE_90 -> "ROTATE_90"
                TRANSVERSE -> "TRANSVERSE"
                ROTATE_270 -> "ROTATE_270"
                else -> throw IllegalArgumentException("Invalid exifOrientation: $exifOrientation")
            }

        fun valueOf(name: String): Int =
            when (name) {
                "UNDEFINED" -> UNDEFINED
                "NORMAL" -> NORMAL
                "FLIP_HORIZONTAL" -> FLIP_HORIZONTAL
                "ROTATE_180" -> ROTATE_180
                "FLIP_VERTICAL" -> FLIP_VERTICAL
                "TRANSPOSE" -> TRANSPOSE
                "ROTATE_90" -> ROTATE_90
                "TRANSVERSE" -> TRANSVERSE
                "ROTATE_270" -> ROTATE_270
                else -> throw IllegalArgumentException("Unknown ExifOrientation name: $name")
            }

        val values = intArrayOf(
            UNDEFINED,
            NORMAL,
            FLIP_HORIZONTAL,
            ROTATE_180,
            FLIP_VERTICAL,
            TRANSPOSE,
            ROTATE_90,
            TRANSVERSE,
            ROTATE_270,
        )
    }
}