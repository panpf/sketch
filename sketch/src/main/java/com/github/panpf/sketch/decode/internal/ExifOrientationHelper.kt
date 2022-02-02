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
import com.github.panpf.sketch.util.Size

fun DataSource.readExifOrientation(): Int =
    newInputStream().use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }

fun DataSource.readExifOrientationWithMimeType(mimeType: String): Int =
    if (ExifInterface.isSupportedMimeType(mimeType)) {
        newInputStream().use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
        }
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

    fun applyOrientation(inBitmap: Bitmap, bitmapPool: BitmapPool): Bitmap? {
        val isRotated = rotationDegrees > 0
        if (!isFlipped && !isRotated) {
            return null
        }

        val matrix = Matrix().apply {
            val centerX = inBitmap.width / 2f
            val centerY = inBitmap.height / 2f
            if (isFlipped) {
                postScale(-1f, 1f, centerX, centerY)
            }
            if (rotationDegrees > 0) {
                postRotate(rotationDegrees.toFloat(), centerX, centerY)
            }
            val rect = RectF(0f, 0f, inBitmap.width.toFloat(), inBitmap.height.toFloat())
            mapRect(rect)
            if (rect.left != 0f || rect.top != 0f) {
                postTranslate(-rect.left, -rect.top)
            }
        }

        val config = inBitmap.config ?: Bitmap.Config.ARGB_8888
        val outBitmap = if (rotationDegrees == 90 || rotationDegrees == 270) {
            bitmapPool.getOrCreate(inBitmap.height, inBitmap.width, config)
        } else {
            bitmapPool.getOrCreate(inBitmap.width, inBitmap.height, config)
        }
        val canvas = Canvas(outBitmap)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(inBitmap, matrix, paint)
        return outBitmap
    }

    fun rotateSize(size: Size): Size =
        if (rotationDegrees == 90 || rotationDegrees == 270) Size(size.height, size.width) else size

    fun reverseRotateSize(size: Size): Size =
        if (rotationDegrees == 90 || rotationDegrees == 270) Size(size.height, size.width) else size

    fun reverseRotateRect(srcRect: Rect, imageWidth: Int, imageHeight: Int) {
        when (360 - rotationDegrees) {
            90 -> {
                val top = srcRect.top
                srcRect.top = srcRect.left
                srcRect.left = imageHeight - srcRect.bottom
                srcRect.bottom = srcRect.right
                srcRect.right = imageHeight - top
            }
            180 -> {
                val left = srcRect.left
                val top = srcRect.top
                srcRect.left = imageWidth - srcRect.right
                srcRect.right = imageWidth - left
                srcRect.top = imageHeight - srcRect.bottom
                srcRect.bottom = imageHeight - top
            }
            270 -> {
                val left = srcRect.left
                srcRect.left = srcRect.top
                srcRect.top = imageWidth - srcRect.right
                srcRect.right = srcRect.bottom
                srcRect.bottom = imageWidth - left
            }
        }
    }
}