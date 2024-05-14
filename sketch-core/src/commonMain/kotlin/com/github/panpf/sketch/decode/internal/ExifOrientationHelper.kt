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
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.reverse
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.flipped
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.rotateInSpace
import kotlin.math.abs

expect fun ExifOrientationHelper(exifOrientation: Int): ExifOrientationHelper

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
interface ExifOrientationHelper {

    val exifOrientation: Int

    /**
     * Returns if the current image orientation is flipped.
     */
    fun isFlipHorizontally(): Boolean {
        return exifOrientation == FLIP_HORIZONTAL
                || exifOrientation == FLIP_VERTICAL
                || exifOrientation == TRANSVERSE
                || exifOrientation == TRANSPOSE
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
     * @see isFlipHorizontally
     */
    fun getRotationDegrees(): Int = when (exifOrientation) {
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
        val rotationDegrees = getRotationDegrees()
        return size.rotate(if (!reverse) rotationDegrees else -rotationDegrees)
    }

    fun applyToScale(scale: Scale, imageSize: Size, reverse: Boolean = false): Scale {
        val rotationDegrees = getRotationDegrees()
        val flipHorizontally = isFlipHorizontally()
        val apply = !reverse
        val horImage = imageSize.width > imageSize.height
        val reverseScaleFromFlip = if (horImage) flipHorizontally else false
        val reverseScaleFromRotate = when {
            horImage && apply -> rotationDegrees == 180 || rotationDegrees == 270
            horImage && !apply -> rotationDegrees == 180 || rotationDegrees == 90
            !horImage && apply -> rotationDegrees == 180 || rotationDegrees == 90
            !horImage && !apply -> rotationDegrees == 180 || rotationDegrees == 270
            else -> false
        }
        return if (
            !(reverseScaleFromFlip && reverseScaleFromRotate)
            && (reverseScaleFromFlip || reverseScaleFromRotate)
        ) {
            scale.reverse()
        } else {
            scale
        }
    }

    fun applyToRect(srcRect: Rect, imageSize: Size, reverse: Boolean = false): Rect {
        val isFlipHorizontally = isFlipHorizontally()
        val rotationDegrees = getRotationDegrees()
        val isRotated = abs(rotationDegrees % 360) != 0
        return if (!reverse) {
            srcRect
                .let { if (isFlipHorizontally) it.flipped(imageSize, vertical = false) else it }
                .let { if (isRotated) it.rotateInSpace(imageSize, rotationDegrees) else it }
        } else {
            val rotatedImageSize = imageSize.rotate(-rotationDegrees)
            srcRect
                .let { if (isRotated) it.rotateInSpace(imageSize, -rotationDegrees) else it }
                .let {
                    if (isFlipHorizontally) it.flipped(
                        rotatedImageSize,
                        vertical = false
                    ) else it
                }
        }
    }

    fun applyToImage(image: Image, reverse: Boolean = false): Image?

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
                else -> throw IllegalArgumentException("Unknown ExifOrientationHelper name: $name")
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

fun ExifOrientationHelper.addToResize(resize: Resize, imageSize: Size): Resize {
    val newSize = applyToSize(resize.size, reverse = true)
    val newScale = applyToScale(resize.scale, imageSize, reverse = true)
    return Resize(
        size = newSize,
        precision = resize.precision,
        scale = newScale,
    )
}