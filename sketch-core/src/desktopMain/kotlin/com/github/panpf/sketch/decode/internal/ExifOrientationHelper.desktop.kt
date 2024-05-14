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
import com.github.panpf.sketch.JvmBitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.flipped
import com.github.panpf.sketch.util.rotated
import java.awt.image.BufferedImage
import kotlin.math.abs

actual fun ExifOrientationHelper(exifOrientation: Int): ExifOrientationHelper {
    return DesktopExifOrientationHelper(exifOrientation)
}

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
class DesktopExifOrientationHelper constructor(
    override val exifOrientation: Int
) : ExifOrientationHelper {

    init {
        require(ExifOrientationHelper.values.any { it == exifOrientation }) { "Invalid exifOrientation: $exifOrientation" }
    }

    @WorkerThread
    override fun applyToImage(image: Image, reverse: Boolean): Image? {
        val rotationDegrees = getRotationDegrees()
        val isFlipHorizontally = isFlipHorizontally()
        val isRotated = abs(rotationDegrees % 360) != 0
        if (!isFlipHorizontally && !isRotated) {
            return null
        }
        return when (image) {
            is JvmBitmapImage -> {
                val inBitmap = image.bitmap
                val flippedBitmap: BufferedImage
                val rotatedBitmap: BufferedImage
                if (!reverse) {
                    flippedBitmap = if (isFlipHorizontally) {
                        inBitmap.flipped(horizontal = true)
                    } else {
                        inBitmap
                    }
                    rotatedBitmap = if (isRotated) {
                        flippedBitmap.rotated(rotationDegrees)
                    } else {
                        flippedBitmap
                    }
                } else {
                    flippedBitmap = if (isRotated) {
                        inBitmap.rotated(-rotationDegrees)
                    } else {
                        inBitmap
                    }
                    rotatedBitmap = if (isFlipHorizontally) {
                        flippedBitmap.flipped(horizontal = true)
                    } else {
                        flippedBitmap
                    }
                }
                rotatedBitmap.asSketchImage()
            }

            is SkiaBitmapImage -> {
                val inBitmap = image.bitmap
                val flippedBitmap: SkiaBitmap
                val rotatedBitmap: SkiaBitmap
                if (!reverse) {
                    flippedBitmap = if (isFlipHorizontally) {
                        inBitmap.flipped(horizontal = true)
                    } else {
                        inBitmap
                    }
                    rotatedBitmap = if (isRotated) {
                        flippedBitmap.rotated(rotationDegrees)
                    } else {
                        flippedBitmap
                    }
                } else {
                    flippedBitmap = if (isRotated) {
                        inBitmap.rotated(-rotationDegrees)
                    } else {
                        inBitmap
                    }
                    rotatedBitmap = if (isFlipHorizontally) {
                        flippedBitmap.flipped(horizontal = true)
                    } else {
                        flippedBitmap
                    }
                }
                rotatedBitmap.asSketchImage()
            }

            else -> {
                throw IllegalArgumentException("Only JvmBitmapImage or SkiaBitmapImage is supported: ${image::class.qualifiedName}")
            }
        }
    }
}