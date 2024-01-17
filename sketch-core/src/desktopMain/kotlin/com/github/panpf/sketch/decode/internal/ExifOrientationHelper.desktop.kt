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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.rotate
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
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
    override fun applyToImage(
        image: Image,
//        bitmapPool: BitmapPool,
//        disallowReuseBitmap: Boolean
        reverse: Boolean
    ): Image? {
        val bufferedImage = image.asOrNull<BufferedImageImage>()?.bufferedImage ?: return null
        val rotationDegrees = getRotationDegrees()
        val isFlipped = isFlipped()

        val bufferedImage2: BufferedImage
        val bufferedImage3: BufferedImage
        val isRotated = abs(rotationDegrees % 360) != 0
        if (!reverse) {
            bufferedImage2 = if (isFlipped) {
                flipImage(bufferedImage, vertical = false)
            } else {
                bufferedImage
            }
            bufferedImage3 = if (isRotated) {
                rotateImage(bufferedImage2, rotationDegrees)
            } else {
                bufferedImage2
            }
        } else {
            bufferedImage2 = if (isRotated) {
                rotateImage(bufferedImage, -rotationDegrees)
            } else {
                bufferedImage
            }
            bufferedImage3 = if (isFlipped) {
                flipImage(bufferedImage2, vertical = false)
            } else {
                bufferedImage2
            }
        }
        return bufferedImage3.asSketchImage()
    }

    private fun flipImage(
        source: BufferedImage,
        @Suppress("SameParameterValue") vertical: Boolean = false
    ): BufferedImage {
        val flipped = BufferedImage(source.width, source.height, source.type)
        val graphics = flipped.createGraphics()
        val transform = if (!vertical) {
            AffineTransform.getTranslateInstance(source.width.toDouble(), 0.0)
        } else {
            AffineTransform.getTranslateInstance(0.0, source.height.toDouble())
        }.apply {
            val flip = if (!vertical) {
                AffineTransform.getScaleInstance(-1.0, 1.0)
            } else {
                AffineTransform.getScaleInstance(1.0, -1.0)
            }
            concatenate(flip)
        }
        graphics.transform = transform
        graphics.drawImage(source, 0, 0, null)
        graphics.dispose()
        return flipped
    }

    private fun rotateImage(source: BufferedImage, degree: Int): BufferedImage {
        val sourceSize = Size(source.width, source.height)
        val newSize = sourceSize.rotate(degree)
        val type = source.colorModel.transparency
        val newImage = BufferedImage(newSize.width, newSize.height, type)
        val graphics: Graphics2D = newImage.createGraphics()
//        graphics.setRenderingHint(
//            RenderingHints.KEY_INTERPOLATION,
//            RenderingHints.VALUE_INTERPOLATION_BILINEAR
//        )
        graphics.translate(
            /* tx = */ (newSize.width - sourceSize.width) / 2.0,
            /* ty = */ (newSize.height - sourceSize.height) / 2.0
        )
        graphics.rotate(
            /* theta = */ Math.toRadians(degree.toDouble()),
            /* x = */ (sourceSize.width / 2).toDouble(),
            /* y = */ (sourceSize.height / 2).toDouble()
        )
        graphics.drawImage(source, 0, 0, null)
        graphics.dispose()
        return newImage
    }
}