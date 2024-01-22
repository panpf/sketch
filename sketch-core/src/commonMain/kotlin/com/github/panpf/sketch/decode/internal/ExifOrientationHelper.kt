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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.reverse
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.flip
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.rotateInSpace
import kotlin.math.abs

expect fun ExifOrientationHelper(@ExifOrientation exifOrientation: Int): ExifOrientationHelper?

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
interface ExifOrientationHelper {

    @ExifOrientation
    val exifOrientation: Int

    /**
     * Returns if the current image orientation is flipped.
     */
    fun isFlipped(): Boolean {
        return when (exifOrientation) {
            ExifOrientation.FLIP_HORIZONTAL,
            ExifOrientation.TRANSVERSE,
            ExifOrientation.FLIP_VERTICAL,
            ExifOrientation.TRANSPOSE -> true

            else -> false
        }
    }

    /**
     * Returns the rotation degrees for the current image orientation. If the image is flipped,
     * i.e., [.isFlipped] returns `true`, the rotation degrees will be base on
     * the assumption that the image is first flipped horizontally (along Y-axis), and then do
     * the rotation. For example, [.TRANSPOSE] will be interpreted as flipped
     * horizontally first, and then rotate 270 degrees clockwise.
     *
     * @return The rotation degrees of the image after the horizontal flipping is applied, if any.
     *
     * @see isFlipped
     */
    fun getRotationDegrees(): Int = when (exifOrientation) {
        ExifOrientation.ROTATE_90,
        ExifOrientation.TRANSVERSE -> 90

        ExifOrientation.ROTATE_180,
        ExifOrientation.FLIP_VERTICAL -> 180

        ExifOrientation.ROTATE_270,
        ExifOrientation.TRANSPOSE -> 270

        ExifOrientation.UNDEFINED,
        ExifOrientation.NORMAL,
        ExifOrientation.FLIP_HORIZONTAL -> 0

        else -> 0
    }

    fun getTranslation(): Int = when (exifOrientation) {
        ExifOrientation.FLIP_HORIZONTAL,
        ExifOrientation.FLIP_VERTICAL,
        ExifOrientation.TRANSPOSE,
        ExifOrientation.TRANSVERSE -> -1

        else -> 1
    }

    fun applyToSize(size: Size, reverse: Boolean = false): Size {
        val rotationDegrees = getRotationDegrees()
        return size.rotate(if (!reverse) rotationDegrees else -rotationDegrees)
    }

    fun applyToScale(scale: Scale, imageSize: Size, reverse: Boolean = false): Scale {
        val rotationDegrees = getRotationDegrees()
        val horImage = imageSize.width > imageSize.height
        return if (!reverse) {
            when {
                horImage && (rotationDegrees == 180 || rotationDegrees == 270) -> scale.reverse()
                rotationDegrees == 90 || rotationDegrees == 180 -> scale.reverse()
                else -> scale
            }
        } else {
            when {
                horImage && (rotationDegrees == 90 || rotationDegrees == 180) -> scale.reverse()
                rotationDegrees == 180 || rotationDegrees == 270 -> scale.reverse()
                else -> scale
            }
        }
    }

    fun applyToRect(srcRect: Rect, imageSize: Size, reverse: Boolean = false): Rect {
        val isFlipped = isFlipped()
        val rotationDegrees = getRotationDegrees()
        val isRotated = abs(rotationDegrees % 360) != 0
        return if (!reverse) {
            srcRect
                .let { if (isFlipped) it.flip(imageSize, vertical = false) else it }
                .let { if (isRotated) it.rotateInSpace(imageSize, rotationDegrees) else it }
        } else {
            val rotatedImageSize = imageSize.rotate(-rotationDegrees)
            srcRect
                .let { if (isRotated) it.rotateInSpace(imageSize, -rotationDegrees) else it }
                .let { if (isFlipped) it.flip(rotatedImageSize, vertical = false) else it }
        }
    }

    fun applyToImage(image: Image, reverse: Boolean = false): Image?
}

fun ExifOrientationHelper.addToResize(resize: Resize, imageSize: Size): Resize {
    val newSize = applyToSize(Size(resize.width, resize.height), reverse = true)
    val newScale = applyToScale(resize.scale, imageSize, reverse = true)
    return Resize(
        width = newSize.width,
        height = newSize.height,
        precision = resize.precision,
        scale = newScale,
    )
}