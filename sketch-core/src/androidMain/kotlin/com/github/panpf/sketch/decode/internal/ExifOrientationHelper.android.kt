/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import android.graphics.RectF
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.safeConfig
import okio.buffer
import java.io.IOException
import kotlin.math.abs

@Throws(IOException::class)
fun DataSource.readExifOrientation(): Int =
    openSource().buffer().inputStream().use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }

@Throws(IOException::class)
fun DataSource.readExifOrientationWithMimeType(mimeType: String): Int =
    if (ExifInterface.isSupportedMimeType(mimeType)) {
        readExifOrientation()
    } else {
        ExifInterface.ORIENTATION_UNDEFINED
    }

actual fun ExifOrientationHelper(@ExifOrientation exifOrientation: Int): ExifOrientationHelper? {
    return AndroidExifOrientationHelper(exifOrientation)
}

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
class AndroidExifOrientationHelper constructor(
    @ExifOrientation override val exifOrientation: Int
) : ExifOrientationHelper {

    @WorkerThread
    override fun applyToImage(image: Image, reverse: Boolean): Image? {
        val inBitmap = image.asOrNull<AndroidBitmapImage>()?.bitmap ?: return null
        val rotationDegrees = getRotationDegrees().let {
            if (reverse) it * -1 else it
        }
        val outBitmap = applyFlipAndRotation(
            inBitmap = inBitmap,
            isFlipped = isFlipped(),
            rotationDegrees = rotationDegrees,
            apply = !reverse
        ) ?: return null
        return outBitmap.asSketchImage()
    }

    @WorkerThread
    private fun applyFlipAndRotation(
        inBitmap: Bitmap,
        isFlipped: Boolean,
        rotationDegrees: Int,
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

        val config = inBitmap.safeConfig
        val newWidth = newRect.width().toInt()
        val newHeight = newRect.height().toInt()
        val outBitmap = Bitmap.createBitmap(
            /* width = */ newWidth,
            /* height = */ newHeight,
            /* config = */ config,
        )

        val canvas = Canvas(outBitmap)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(inBitmap, matrix, paint)
        return outBitmap
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
}