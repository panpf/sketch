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
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.util.flipped
import com.github.panpf.sketch.util.rotated
import kotlin.math.abs

actual fun ExifOrientationHelper(@ExifOrientation exifOrientation: Int): ExifOrientationHelper? {
    return DesktopExifOrientationHelper(exifOrientation)
}

/**
 * Rotate and flip the image according to the 'orientation' attribute of Exif so that the image is presented to the user at a normal angle
 */
class DesktopExifOrientationHelper constructor(
    @ExifOrientation override val exifOrientation: Int
) : ExifOrientationHelper {

    @WorkerThread
    override fun applyToImage(image: Image, reverse: Boolean): Image? {
        require(image is SkiaBitmapImage) { "Only SkiaBitmapImage is supported: ${image::class.qualifiedName}" }
        val inBitmap = image.bitmap
        val rotationDegrees = getRotationDegrees()
        val isFlipped = isFlipped()
        val isRotated = abs(rotationDegrees % 360) != 0
        if (!isFlipped && !isRotated) {
            return null
        }
        val flippedBitmap: SkiaBitmap
        val rotatedBitmap: SkiaBitmap
        if (!reverse) {
            flippedBitmap = if (isFlipped) {
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
            rotatedBitmap = if (isFlipped) {
                flippedBitmap.flipped(horizontal = true)
            } else {
                flippedBitmap
            }
        }
        return rotatedBitmap.asSketchImage()
    }
}