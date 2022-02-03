/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.util.Size
import kotlin.math.abs

fun DataSource.readExifOrientation(): Int =
    newInputStream().use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }

fun DataSource.readExifOrientationWithMimeType(mimeType: String): Int =
    if (ExifInterface.isSupportedMimeType(mimeType)) {
        readExifOrientation()
    } else {
        ExifInterface.ORIENTATION_UNDEFINED
    }

fun exifOrientationName(exifOrientation: Int): String =
    when (exifOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> "ROTATE_90"
        ExifInterface.ORIENTATION_TRANSPOSE -> "TRANSPOSE"
        ExifInterface.ORIENTATION_ROTATE_180 -> "ROTATE_180"
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> "FLIP_VERTICAL"
        ExifInterface.ORIENTATION_ROTATE_270 -> "ROTATE_270"
        ExifInterface.ORIENTATION_TRANSVERSE -> "TRANSVERSE"
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> "FLIP_HORIZONTAL"
        ExifInterface.ORIENTATION_UNDEFINED -> "UNDEFINED"
        ExifInterface.ORIENTATION_NORMAL -> "NORMAL"
        else -> exifOrientation.toString()
    }

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
class ExifOrientationHelper constructor(val exifOrientation: Int) {

    /**
     * Returns if the current image orientation is flipped.
     *
     * @see rotationDegrees
     */
    val isFlipped: Boolean =
        when (exifOrientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
            ExifInterface.ORIENTATION_TRANSVERSE,
            ExifInterface.ORIENTATION_FLIP_VERTICAL,
            ExifInterface.ORIENTATION_TRANSPOSE -> true
            else -> false
        }

    /**
     * Returns the rotation degrees for the current image orientation. If the image is flipped,
     * i.e., [.isFlipped] returns `true`, the rotation degrees will be base on
     * the assumption that the image is first flipped horizontally (along Y-axis), and then do
     * the rotation. For example, [.ORIENTATION_TRANSPOSE] will be interpreted as flipped
     * horizontally first, and then rotate 270 degrees clockwise.
     *
     * @return The rotation degrees of the image after the horizontal flipping is applied, if any.
     *
     * @see isFlipped
     */
    val rotationDegrees: Int =
        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_TRANSVERSE -> 90
            ExifInterface.ORIENTATION_ROTATE_180,
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180
            ExifInterface.ORIENTATION_ROTATE_270,
            ExifInterface.ORIENTATION_TRANSPOSE -> 270
            ExifInterface.ORIENTATION_UNDEFINED,
            ExifInterface.ORIENTATION_NORMAL,
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> 0
            else -> 0
        }

//    val translation: Int =
//        when (exifOrientation) {
//            ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
//            ExifInterface.ORIENTATION_FLIP_VERTICAL,
//            ExifInterface.ORIENTATION_TRANSPOSE,
//            ExifInterface.ORIENTATION_TRANSVERSE -> -1
//            else -> 1
//        }

    fun applyToBitmap(inBitmap: Bitmap, bitmapPool: BitmapPool? = null): Bitmap? {
        return applyFlipAndRotation(inBitmap, isFlipped, rotationDegrees, bitmapPool, true)
    }

    fun addToBitmap(inBitmap: Bitmap, bitmapPool: BitmapPool? = null): Bitmap? {
        return applyFlipAndRotation(inBitmap, isFlipped, -rotationDegrees, bitmapPool, false)
    }

    fun applyToSize(size: Size): Size {
        val matrix = Matrix().apply {
            applyFlipAndRotationToMatrix(this, isFlipped, rotationDegrees, true)
        }
        val newRect = RectF(0f, 0f, size.width.toFloat(), size.height.toFloat())
        matrix.mapRect(newRect)
        return Size(newRect.width().toInt(), newRect.height().toInt())
    }

    fun addToSize(size: Size): Size {
        val matrix = Matrix().apply {
            applyFlipAndRotationToMatrix(this, isFlipped, -rotationDegrees, false)
        }
        val newRect = RectF(0f, 0f, size.width.toFloat(), size.height.toFloat())
        matrix.mapRect(newRect)
        return Size(newRect.width().toInt(), newRect.height().toInt())
    }

    fun addToResize(resize: Resize, imageWidth: Int, imageHeight: Int): Resize {
        val newSize = addToSize(Size(resize.width, resize.height))
        val correctedImageSize = applyToSize(Size(imageWidth, imageHeight))
        val newScale = if (correctedImageSize.width > correctedImageSize.height) {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90
                || exifOrientation == ExifInterface.ORIENTATION_TRANSVERSE
                || exifOrientation == ExifInterface.ORIENTATION_ROTATE_180
                || exifOrientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL
            ) {
                when (resize.scale) {
                    Resize.Scale.START_CROP -> Resize.Scale.END_CROP
                    Resize.Scale.CENTER_CROP -> Resize.Scale.CENTER_CROP
                    Resize.Scale.END_CROP -> Resize.Scale.START_CROP
                    Resize.Scale.FILL -> Resize.Scale.FILL
                }
            } else {
                resize.scale
            }
        } else {
            if (exifOrientation == ExifInterface.ORIENTATION_TRANSVERSE
                || exifOrientation == ExifInterface.ORIENTATION_ROTATE_180
                || exifOrientation == ExifInterface.ORIENTATION_FLIP_VERTICAL
                || exifOrientation == ExifInterface.ORIENTATION_ROTATE_270
            ) {
                when (resize.scale) {
                    Resize.Scale.START_CROP -> Resize.Scale.END_CROP
                    Resize.Scale.CENTER_CROP -> Resize.Scale.CENTER_CROP
                    Resize.Scale.END_CROP -> Resize.Scale.START_CROP
                    Resize.Scale.FILL -> Resize.Scale.FILL
                }
            } else {
                resize.scale
            }
        }
        return Resize(
            width = newSize.width,
            height = newSize.height,
            scope = resize.scope,
            scale = newScale,
            precision = resize.precision
        )
    }

    fun reverseRotateRect(srcRect: Rect, imageWidth: Int, imageHeight: Int): Rect =
        when (360 - rotationDegrees) {
            90 -> {
                Rect(
                    imageHeight - srcRect.bottom,
                    srcRect.left,
                    imageHeight - srcRect.top,
                    srcRect.right
                )
            }
            180 -> {
                Rect(
                    imageWidth - srcRect.right,
                    imageHeight - srcRect.bottom,
                    imageWidth - srcRect.left,
                    imageHeight - srcRect.top
                )
            }
            270 -> {
                Rect(
                    srcRect.top,
                    imageWidth - srcRect.right,
                    srcRect.bottom,
                    imageWidth - srcRect.left
                )
            }
            else -> srcRect
        }

    private fun applyFlipAndRotationToMatrix(
        matrix: Matrix,
        isFlipped: Boolean,
        rotationDegrees: Int,
        apply: Boolean
    ) {
        val isRotated = abs(rotationDegrees % 360) != 0
        if (apply) {
            if (isFlipped) {
                matrix.postScale(-1f, 1f)
            }
            if (isRotated) {
                matrix.postRotate(rotationDegrees.toFloat())
            }
        } else {
            if (isRotated) {
                matrix.postRotate(rotationDegrees.toFloat())
            }
            if (isFlipped) {
                matrix.postScale(-1f, 1f)
            }
        }
    }

    private fun applyFlipAndRotation(
        inBitmap: Bitmap,
        isFlipped: Boolean,
        rotationDegrees: Int,
        bitmapPool: BitmapPool? = null,
        apply: Boolean,
    ): Bitmap? {
        val isRotated = abs(rotationDegrees % 360) != 0
        if (!isFlipped && !isRotated) {
            return null
        }

        val matrix = Matrix().apply {
            applyFlipAndRotationToMatrix(this, isFlipped, rotationDegrees, apply)
        }
        val newRect = RectF(0f, 0f, inBitmap.width.toFloat(), inBitmap.height.toFloat())
        matrix.mapRect(newRect)
        matrix.postTranslate(-newRect.left, -newRect.top)

        val config = inBitmap.config ?: Bitmap.Config.ARGB_8888
        val newWidth = newRect.width().toInt()
        val newHeight = newRect.height().toInt()
        val outBitmap = bitmapPool?.getOrCreate(newWidth, newHeight, config)
            ?: Bitmap.createBitmap(newWidth, newHeight, config)

        val canvas = Canvas(outBitmap)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(inBitmap, matrix, paint)
        return outBitmap
    }
}