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

package com.github.panpf.sketch.core.android.test.decode.internal

import android.graphics.Canvas
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampleSizeForRegion
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.decode
import com.github.panpf.sketch.decode.internal.decodeRegion
import com.github.panpf.sketch.decode.internal.getMaxBitmapSize
import com.github.panpf.sketch.decode.internal.getMaxBitmapSizeOr
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.readImageInfo
import com.github.panpf.sketch.decode.internal.readImageInfoWithIgnoreExifOrientation
import com.github.panpf.sketch.decode.internal.resize
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.chunkingFour
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.expectedRgb565
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isSameAspectRatio
import com.github.panpf.sketch.util.size
import com.github.panpf.sketch.util.times
import com.github.panpf.sketch.util.toAndroidRect
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DecodesAndroidTest {

    @Test
    fun testGetMaxBitmapSize() {
        val maxSize = getMaxBitmapSize()
        assertTrue(
            actual = arrayOf(
                Size(2048, 2048),
                Size(4096, 4096),
                Size(8192, 8192),
                Size(16384, 16384)
            ).any { it == maxSize },
            message = "maxSize=$maxSize"
        )
    }

    @Test
    fun testGetMaxBitmapSizeOr() {
        val maxSize = getMaxBitmapSize()
        if (maxSize != null) {
            assertEquals(
                expected = maxSize,
                actual = getMaxBitmapSizeOr(Size(30, 50))
            )
        } else {
            assertEquals(
                expected = Size(30, 50) * 2f,
                actual = getMaxBitmapSizeOr(Size(30, 50))
            )
        }
    }

    @Test
    fun testCalculateSampledBitmapSize() {
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg"
            )
        )
        assertEquals(
            expected = Size(width = 502, height = 100),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png"
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/bmp"
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/gif"
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/webp"
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/heic"
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/heif"
            )
        )
        // TODO test real read
    }

    @Test
    fun testCalculateSampledBitmapSizeForRegion() {
        assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.N) Size(503, 101) else Size(502, 100),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(502, 100),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(288, 100),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(502, 55),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(288, 55),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(288, 55),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
            )
        )
        // TODO test real read
    }

    @Test
    fun testCalculateSampleSize() {
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = false
            )
        )

        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif",
                smallerSizeMode = false
            )
        )

        // smallerSizeMode = true
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = true
            )
        )

        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif",
                smallerSizeMode = true
            )
        )


        val maxSize = getMaxBitmapSize()?.width ?: 0
        val expected = when {
            maxSize <= 4096 -> 32
            maxSize <= 8192 -> 16
            else -> 4
        }
        assertEquals(
            expected = expected,
            actual = calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 32,
            actual = calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = true
            )
        )
    }

    @Test
    fun testCalculateSampleSize2() {
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
            )
        )

        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg"
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png"
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp"
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp"
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif"
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic"
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif"
            )
        )
    }

    @Test
    fun testCalculateSampleSizeForRegion() {
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )

        assertEquals(
            expected = if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        // smallerSizeMode = true
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )

        assertEquals(
            expected = if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        val maxSize = getMaxBitmapSize()?.width ?: 0
        val expected = when {
            maxSize <= 4096 -> 32
            maxSize <= 8192 -> 16
            else -> 4
        }
        assertEquals(
            expected = expected,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = false
            )
        )
        assertEquals(
            32,
            calculateSampleSizeForRegion(
                regionSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = true
            )
        )
    }

    @Test
    fun testCalculateSampleSizeForRegion2() {
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        assertEquals(
            expected = 1,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            expected = 8,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
            )
        )

        assertEquals(
            expected = if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
            )
        )

        assertEquals(
            expected = 2,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
            )
        )
    }

    @Test
    fun testResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        data class Item(
            val size: Size,
            val precision: Precision,
            val scale: Scale,
            val change: Boolean,
            val expected: Size,
        )

        val sourceImage = ResourceImages.jpeg.decode().apply {
            assertEquals(Size(1291, 1936), size)
        }
        val decodeResult = DecodeResult(
            image = sourceImage,
            imageInfo = ImageInfo(sourceImage.width, sourceImage.height, "image/jpeg"),
            dataFrom = MEMORY,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = null,
            extras = null,
        )

        val executeResize: suspend (Size, Precision, Scale) -> DecodeResult =
            { size, precision, scale ->
                val request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    resize(size, precision, scale)
                }
                val realResize = request.toRequestContext(sketch)
                    .computeResize(decodeResult.imageInfo.size)
                decodeResult.resize(realResize)
            }

        listOf(
            Item(Size(500, 400), LESS_PIXELS, CENTER_CROP, change = true, Size(323, 484)),
            Item(Size(2000, 2500), LESS_PIXELS, CENTER_CROP, change = false, Size(1291, 1936)),
            Item(Size(500, 400), SAME_ASPECT_RATIO, CENTER_CROP, change = true, Size(500, 400)),
            Item(Size(2000, 2500), SAME_ASPECT_RATIO, CENTER_CROP, change = true, Size(1291, 1614)),
            Item(Size(500, 400), EXACTLY, CENTER_CROP, change = true, Size(500, 400)),
            Item(Size(2000, 2500), EXACTLY, CENTER_CROP, change = true, Size(2000, 2500)),
        ).forEach { item ->
            val (size, precision, scale, change, expected) = item
            val resizeResult = executeResize(size, precision, scale)
            assertEquals(
                expected = expected,
                actual = resizeResult.image.getBitmapOrThrow().size,
                message = "item=$item"
            )
            if (change) {
                assertEquals(
                    expected = true,
                    actual = resizeResult !== decodeResult,
                    message = "item=$item"
                )
                assertEquals(
                    expected = true,
                    actual = resizeResult.image !== decodeResult.image,
                    message = "item=$item"
                )
                assertEquals(
                    expected = true,
                    actual = resizeResult.image.getBitmapOrThrow() !== decodeResult.image.getBitmapOrThrow(),
                    message = "item=$item"
                )
                if (!sourceImage.size.isSameAspectRatio(resizeResult.image.size, 0.2f)) {
                    val similarity =
                        sourceImage.bitmap.similarity(resizeResult.image.getBitmapOrThrow())
                    assertTrue(
                        actual = similarity > 0,
                        message = "similarity=$similarity, item=$item"
                    )
                }
                assertNotNull(resizeResult.transformeds?.getResizeTransformed())
            } else {
                assertNull(resizeResult.transformeds?.getResizeTransformed())
                assertEquals(
                    expected = false,
                    actual = resizeResult !== decodeResult,
                    message = "item=$item"
                )
            }
        }

        val startCropBitmap = executeResize(Size(500, 400), SAME_ASPECT_RATIO, START_CROP)
            .image.getBitmapOrThrow()
        val centerCropBitmap = executeResize(Size(500, 400), SAME_ASPECT_RATIO, CENTER_CROP)
            .image.getBitmapOrThrow()
        val endCropBitmap = executeResize(Size(500, 400), SAME_ASPECT_RATIO, END_CROP)
            .image.getBitmapOrThrow()
        assertEquals(startCropBitmap.size, centerCropBitmap.size)
        assertEquals(startCropBitmap.size, endCropBitmap.size)
        startCropBitmap.similarity(centerCropBitmap).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }
        startCropBitmap.similarity(endCropBitmap).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }
        centerCropBitmap.similarity(endCropBitmap).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }

        val startCropBitmap2 = executeResize(Size(2000, 2500), EXACTLY, START_CROP)
            .image.getBitmapOrThrow()
        val centerCropBitmap2 = executeResize(Size(2000, 2500), EXACTLY, CENTER_CROP)
            .image.getBitmapOrThrow()
        val endCropBitmap2 = executeResize(Size(2000, 2500), EXACTLY, END_CROP)
            .image.getBitmapOrThrow()
        assertEquals(startCropBitmap2.size, centerCropBitmap2.size)
        assertEquals(startCropBitmap2.size, endCropBitmap2.size)
        startCropBitmap2.similarity(centerCropBitmap2).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }
        startCropBitmap2.similarity(endCropBitmap2).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }
        centerCropBitmap2.similarity(endCropBitmap2).apply {
            assertTrue(actual = this > 0f, message = "similarity=$this")
        }
    }

    @Test
    fun testReadImageInfoWithExifOrientation() {
        val context = getTestContext()
        assertEquals(
            expected = "ImageInfo(1291x1936,'image/jpeg')",
            actual = ResourceImages.jpeg.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x719,'image/png')",
            actual = ResourceImages.png.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(700x1012,'image/bmp')",
            actual = ResourceImages.bmp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1080x1344,'image/webp')",
            actual = ResourceImages.webp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x480,'image/gif')",
            actual = ResourceImages.animGif.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x270,'image/webp')",
            actual = ResourceImages.animWebp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation().toShortString()
        )
        assertFailsWith(ImageInvalidException::class) {
            ResourceImages.svg.toDataSource(context).readImageInfoWithIgnoreExifOrientation()
        }
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertEquals(
                expected = "ImageInfo(750x932,'image/heif')",
                actual = ResourceImages.heic.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation().toShortString()
            )
            assertEquals(
                expected = "ImageInfo(256x144,'image/heif')",
                actual = ResourceImages.animHeif.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation()
                    .toShortString()
            )
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ResourceImages.heic.toDataSource(context).readImageInfoWithIgnoreExifOrientation()
            }
            assertFailsWith(ImageInvalidException::class) {
                ResourceImages.animHeif.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation()
            }
        }
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifFlipHorizontal.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifFlipVertical.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifNormal.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ResourceImages.clockExifRotate90.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifRotate180.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ResourceImages.clockExifRotate270.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ResourceImages.clockExifTranspose.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ResourceImages.clockExifTransverse.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifUndefined.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
    }

    @Test
    fun testReadImageInfo() {
        val context = getTestContext()
        assertEquals(
            expected = "ImageInfo(1291x1936,'image/jpeg')",
            actual = ResourceImages.jpeg.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x719,'image/png')",
            actual = ResourceImages.png.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(700x1012,'image/bmp')",
            actual = ResourceImages.bmp.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1080x1344,'image/webp')",
            actual = ResourceImages.webp.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x480,'image/gif')",
            actual = ResourceImages.animGif.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x270,'image/webp')",
            actual = ResourceImages.animWebp.toDataSource(context).readImageInfo().toShortString()
        )
        assertFailsWith(ImageInvalidException::class) {
            ResourceImages.svg.toDataSource(context).readImageInfo()
        }
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertEquals(
                expected = "ImageInfo(750x932,'image/heif')",
                actual = ResourceImages.heic.toDataSource(context).readImageInfo().toShortString()
            )
            assertEquals(
                expected = "ImageInfo(256x144,'image/heif')",
                actual = ResourceImages.animHeif.toDataSource(context).readImageInfo()
                    .toShortString()
            )
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ResourceImages.heic.toDataSource(context).readImageInfo()
            }
            assertFailsWith(ImageInvalidException::class) {
                ResourceImages.animHeif.toDataSource(context).readImageInfo()
            }
        }
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifFlipHorizontal.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifFlipVertical.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifNormal.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifRotate90.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifRotate180.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifRotate270.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifTranspose.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifTransverse.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ResourceImages.clockExifUndefined.toDataSource(context).readImageInfo()
                .toShortString()
        )
    }

    @Test
    fun testDecode() {
        val context = getTestContext()

        data class Item(
            val imageFile: ResourceImageFile,
            val error: Boolean
        )
        listOf(
            Item(ResourceImages.jpeg, error = false),
            Item(ResourceImages.png, error = false),
            Item(ResourceImages.bmp, error = false),
            Item(ResourceImages.webp, error = false),
            Item(ResourceImages.heic, error = VERSION.SDK_INT < VERSION_CODES.P),
            Item(ResourceImages.svg, error = true),
            Item(ResourceImages.animGif, error = false),
            Item(ResourceImages.animWebp, error = false),
            Item(ResourceImages.animHeif, error = VERSION.SDK_INT < VERSION_CODES.P),
            Item(ResourceImages.clockExifFlipHorizontal, error = false),
            Item(ResourceImages.clockExifFlipVertical, error = false),
            Item(ResourceImages.clockExifNormal, error = false),
            Item(ResourceImages.clockExifRotate90, error = false),
            Item(ResourceImages.clockExifRotate180, error = false),
            Item(ResourceImages.clockExifRotate270, error = false),
            Item(ResourceImages.clockExifTranspose, error = false),
            Item(ResourceImages.clockExifTransverse, error = false),
            Item(ResourceImages.clockExifUndefined, error = false),
        ).forEach { item ->
            val (imageFile, error) = item
            val dataSource = imageFile.toDataSource(context)
            if (error) {
                assertFailsWith(
                    exceptionClass = ImageInvalidException::class,
                    message = "item=$item"
                ) {
                    dataSource.decode()
                }
                return@forEach
            }

            assertEquals(
                expected = "Bitmap(${imageFile.size},ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = dataSource.decode().toShortInfoString(),
                message = "item=$item"
            )

            val config1 = DecodeConfig().apply {
                sampleSize = 2
                colorType = ColorType.RGB_565
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    colorSpace = ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
                }
            }
            val imageInfo = dataSource.readImageInfo()
            val bitmapSize1 = calculateSampledBitmapSize(
                imageSize = imageFile.size,
                sampleSize = config1.sampleSize ?: 1,
                mimeType = imageInfo.mimeType
            )
            val colorType = config1.colorType!!.expectedRgb565(imageInfo.mimeType)
            assertEquals(
                expected = "Bitmap(${bitmapSize1},$colorType${shortInfoColorSpace("LINEAR_SRGB")})",
                actual = dataSource.decode(config1).toShortInfoString(),
                message = "item=$item"
            )
        }
    }

    @Test
    fun testDecodeRegion() {
        val context = getTestContext()

        listOf(
            ResourceImages.jpeg,
            ResourceImages.png,
            ResourceImages.bmp,
            ResourceImages.webp,
            ResourceImages.heic,
            ResourceImages.svg,
            ResourceImages.animGif,
            ResourceImages.animWebp,
            ResourceImages.animHeif,
            ResourceImages.clockExifFlipHorizontal,
            ResourceImages.clockExifFlipVertical,
            ResourceImages.clockExifNormal,
            ResourceImages.clockExifRotate90,
            ResourceImages.clockExifRotate180,
            ResourceImages.clockExifRotate270,
            ResourceImages.clockExifTranspose,
            ResourceImages.clockExifTransverse,
            ResourceImages.clockExifUndefined,
        ).forEach { imageFile ->
            val dataSource = imageFile.toDataSource(context)
            if (ImageFormat.parseMimeType(imageFile.mimeType)
                    ?.supportBitmapRegionDecoder() != true
            ) {
                assertFailsWith(
                    exceptionClass = IOException::class,
                    message = "imageFile=$imageFile"
                ) {
                    dataSource.decodeRegion(srcRect = imageFile.size.toRect())
                }
                return@forEach
            }

            val sourceBitmap = try {
                dataSource.decodeRegion(
                    srcRect = imageFile.size.toRect(),
                    imageInfo = ImageInfo(imageFile.size, imageFile.mimeType),
                )
            } catch (e: Exception) {
                throw Exception("imageFile=$imageFile", e)
            }.apply {
                val colorSpace = shortInfoColorSpace("SRGB")
                assertEquals(
                    expected = "Bitmap(${imageFile.size},ARGB_8888$colorSpace)",
                    actual = toShortInfoString(),
                    message = "imageFile=$imageFile"
                )
            }

            val (topLeftRect, topRightRect, bottomLeftRect, bottomRightRect) =
                sourceBitmap.size.toRect().chunkingFour()
            listOf(
                DecodeConfig(),
                DecodeConfig().apply {
                    sampleSize = 2
                    colorType = ColorType.RGB_565
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        colorSpace = ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
                    }
                }
            ).forEach { decodeConfig ->
                // Divide into four pieces
                val topLeftBitmap = dataSource.decodeRegion(topLeftRect, decodeConfig)
                val topRightBitmap = dataSource.decodeRegion(topRightRect, decodeConfig)
                val bottomLeftBitmap = dataSource.decodeRegion(bottomLeftRect, decodeConfig)
                val bottomRightBitmap = dataSource.decodeRegion(bottomRightRect, decodeConfig)
                listOf(
                    topLeftBitmap to topLeftRect,
                    topRightBitmap to topRightRect,
                    bottomLeftBitmap to bottomLeftRect,
                    bottomRightBitmap to bottomRightRect
                ).forEach { (bitmap, tileRect) ->
                    assertEquals(
                        expected = calculateSampledBitmapSizeForRegion(
                            regionSize = tileRect.size,
                            sampleSize = decodeConfig.sampleSize ?: 1,
                            mimeType = imageFile.mimeType,
                            imageSize = null
                        ),
                        actual = bitmap.size,
                        message = "imageFile=$imageFile, decodeConfig=$decodeConfig, tileRect=$tileRect, sourceSize=${sourceBitmap.size}"
                    )
                    assertEquals(
                        expected = decodeConfig.colorType?.expectedRgb565(imageFile.mimeType)
                            ?: ColorType.ARGB_8888,
                        actual = bitmap.colorType,
                        message = "imageFile=$imageFile, decodeConfig=$decodeConfig, tileRect=$tileRect, sourceSize=${sourceBitmap.size}"
                    )
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        assertEquals(
                            expected = decodeConfig.colorSpace
                                ?: ColorSpace.get(ColorSpace.Named.SRGB),
                            actual = bitmap.colorSpace,
                            message = "imageFile=$imageFile, decodeConfig=$decodeConfig, tileRect=$tileRect, sourceSize=${sourceBitmap.size}"
                        )
                    }
                }
                listOf(
                    topLeftBitmap.similarity(topRightBitmap),
                    topLeftBitmap.similarity(bottomLeftBitmap),
                    topLeftBitmap.similarity(bottomRightBitmap),
                    topRightBitmap.similarity(bottomLeftBitmap),
                    topRightBitmap.similarity(bottomRightBitmap),
                    bottomLeftBitmap.similarity(bottomRightBitmap)
                ).forEachIndexed { index, similarity ->
                    assertTrue(
                        actual = similarity >= 4,
                        message = "index=$index, similarity=$similarity, imageFile=$imageFile, decodeConfig: $decodeConfig"
                    )
                }

                // Merge four pictures
                val mergedBitmap = AndroidBitmap(
                    width = sourceBitmap.width,
                    height = sourceBitmap.height,
                    config = topLeftBitmap.config
                ).apply {
                    val canvas = Canvas(this)
                    canvas.drawBitmap(
                        /* bitmap = */ topLeftBitmap,
                        /* src = */ topLeftBitmap.size.toRect().toAndroidRect(),
                        /* dst = */ topLeftRect.toAndroidRect(),
                        /* paint = */ null
                    )
                    canvas.drawBitmap(
                        /* bitmap = */ topRightBitmap,
                        /* src = */ topRightBitmap.size.toRect().toAndroidRect(),
                        /* dst = */ topRightRect.toAndroidRect(),
                        /* paint = */ null
                    )
                    canvas.drawBitmap(
                        /* bitmap = */ bottomLeftBitmap,
                        /* src = */ bottomLeftBitmap.size.toRect().toAndroidRect(),
                        /* dst = */ bottomLeftRect.toAndroidRect(),
                        /* paint = */ null
                    )
                    canvas.drawBitmap(
                        /* bitmap = */ bottomRightBitmap,
                        /* src = */ bottomRightBitmap.size.toRect().toAndroidRect(),
                        /* dst = */ bottomRightRect.toAndroidRect(),
                        /* paint = */ null
                    )
                }
                sourceBitmap.similarity(mergedBitmap).apply {
                    assertTrue(
                        actual = this <= 1,
                        message = "similarity=$this, imageFile=$imageFile, decodeConfig: $decodeConfig"
                    )
                }
            }
        }
    }

    @Test
    fun testSupportBitmapRegionDecoder() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertTrue(ImageFormat.HEIC.supportBitmapRegionDecoder())
        } else {
            assertFalse(ImageFormat.HEIC.supportBitmapRegionDecoder())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            assertTrue(ImageFormat.HEIF.supportBitmapRegionDecoder())
        } else {
            assertFalse(ImageFormat.HEIF.supportBitmapRegionDecoder())
        }
        assertFalse(ImageFormat.BMP.supportBitmapRegionDecoder())
        assertFalse(ImageFormat.GIF.supportBitmapRegionDecoder())
        assertTrue(ImageFormat.JPEG.supportBitmapRegionDecoder())
        assertTrue(ImageFormat.PNG.supportBitmapRegionDecoder())
        assertTrue(ImageFormat.WEBP.supportBitmapRegionDecoder())
    }
}