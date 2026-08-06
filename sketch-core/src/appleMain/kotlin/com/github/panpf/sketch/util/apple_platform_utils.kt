/*
 * Copyright (C) 2026 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
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

@file:OptIn(
    kotlinx.cinterop.BetaInteropApi::class,
    kotlinx.cinterop.ExperimentalForeignApi::class,
)

package com.github.panpf.sketch.util

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.Foundation.NSData
import platform.Foundation.create
import platform.darwin.ByteVar
import platform.posix.memcpy

/**
 * Convert a Core Graphics image to an immutable Skia bitmap.
 */
fun CGImageRef.toBitmap(sampleSize: Int = 1, region: Rect? = null): Bitmap {
    require((sampleSize > 0) && ((sampleSize == 1) || ((sampleSize % 2) == 0))) {
        "sampleSize must be 1 or a power of 2, but was $sampleSize"
    }
    val originalWidth = CGImageGetWidth(this).toInt()
    val originalHeight = CGImageGetHeight(this).toInt()
    val fullRect = Rect(left = 0, top = 0, right = originalWidth, bottom = originalHeight)
    if (region != null) {
        require(!region.isEmpty) {
            "cropRect invalid: ${region.toShortString()}"
        }
        require(fullRect.contains(region)) {
            "cropRect out of bounds: ${region.toShortString()}, originalSize=${originalWidth}x${originalHeight}"
        }
    }

    val finalRegion = region ?: fullRect
    val croppedImage = if (finalRegion != fullRect) {
        CGImageCreateWithImageInRect(
            image = this,
            rect = CGRectMake(
                x = finalRegion.left.toDouble(),
                y = finalRegion.top.toDouble(),
                width = finalRegion.width().toDouble(),
                height = finalRegion.height().toDouble(),
            ),
        ) ?: throw Exception("Failed to create cropped CGImage")
    } else {
        this
    }

    try {
        val sampledBitmapSize = calculateSampledBitmapSize(
            imageSize = Size(finalRegion.width(), finalRegion.height()),
            sampleSize = sampleSize,
        )
        val bytesPerRow = sampledBitmapSize.width * 4
        val pixels = ByteArray(bytesPerRow * sampledBitmapSize.height)
        val colorSpace = CGColorSpaceCreateDeviceRGB()
            ?: throw Exception("Failed to create RGB color space")
        try {
            pixels.usePinned { pinned ->
                val context = CGBitmapContextCreate(
                    data = pinned.addressOf(0),
                    width = sampledBitmapSize.width.toULong(),
                    height = sampledBitmapSize.height.toULong(),
                    bitsPerComponent = 8u,
                    bytesPerRow = bytesPerRow.toULong(),
                    space = colorSpace,
                    bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value or kCGBitmapByteOrder32Big,
                ) ?: throw Exception("Failed to create bitmap context")
                try {
                    CGContextDrawImage(
                        c = context,
                        rect = CGRectMake(
                            x = 0.0,
                            y = 0.0,
                            width = sampledBitmapSize.width.toDouble(),
                            height = sampledBitmapSize.height.toDouble(),
                        ),
                        image = croppedImage,
                    )
                } finally {
                    CGContextRelease(context)
                }
            }
        } finally {
            CGColorSpaceRelease(colorSpace)
        }

        val imageInfo = org.jetbrains.skia.ImageInfo(
            width = sampledBitmapSize.width,
            height = sampledBitmapSize.height,
            colorType = ColorType.RGBA_8888,
            alphaType = ColorAlphaType.PREMUL,
            colorSpace = ColorSpace.sRGB,
        )
        return Bitmap().apply {
            check(installPixels(imageInfo, pixels, bytesPerRow)) {
                "Failed to install RGBA pixels into bitmap"
            }
            setImmutable()
        }
    } finally {
        if (finalRegion != fullRect) {
            CGImageRelease(croppedImage)
        }
    }
}

/**
 * Convert a ByteArray to NSData by pinning the byte array and creating an NSData object that references the pinned memory.
 *
 * @see com.github.panpf.sketch.core.apple.test.util.ApplePlatformUtilsTest.testByteArrayToNSData
 */
fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}

/**
 * Convert an NSData to ByteArray by creating a new ByteArray of the appropriate size and copying the bytes from the NSData into it using memcpy.
 *
 * @see com.github.panpf.sketch.core.apple.test.util.ApplePlatformUtilsTest.testNSDataToByteArray
 */
fun NSData.toByteArray(): ByteArray {
    val byteArray = ByteArray(length.toInt())
    val byteVars = bytes?.reinterpret<ByteVar>()
    if (byteVars != null) {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), byteVars, length)
        }
    }
    return byteArray
}
