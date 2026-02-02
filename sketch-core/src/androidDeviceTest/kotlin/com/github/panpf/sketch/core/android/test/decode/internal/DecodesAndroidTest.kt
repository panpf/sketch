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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
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
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageData
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
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.chunkingFour
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.div
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.isSameAspectRatio
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.safeToSoftware
import com.github.panpf.sketch.util.size
import com.github.panpf.sketch.util.times
import com.github.panpf.sketch.util.toAndroidRect
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png"
            )
        )
        assertEquals(
            expected = Size(503, 101),
            actual = calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/bmp"
            )
        )
        assertEquals(
            expected = Size(503, 101),
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

//        val context = getTestContext()
//        listOf(
//            ComposeResImageFiles.formatJpeg,
//            ComposeResImageFiles.formatPng,
//            ComposeResImageFiles.formatBmp,
//            ComposeResImageFiles.formatWebp,
//            ComposeResImageFiles.formatHeic,
//            ComposeResImageFiles.svg,
//            ComposeResImageFiles.formatAnimGif,
//            ComposeResImageFiles.formatAnimWebp,
//            ComposeResImageFiles.animHeif,
//            ComposeResImageFiles.clockExifFlipHorizontal,
//            ComposeResImageFiles.clockExifFlipVertical,
//            ComposeResImageFiles.clockExifNormal,
//            ComposeResImageFiles.clockExifRotate90,
//            ComposeResImageFiles.clockExifRotate180,
//            ComposeResImageFiles.clockExifRotate270,
//            ComposeResImageFiles.clockExifTranspose,
//            ComposeResImageFiles.clockExifTransverse,
//            ComposeResImageFiles.clockExifUndefined,
//        ).forEach { imageFile ->
//            listOf(
//                DecodeConfig(sampleSize = 1),
//                DecodeConfig(sampleSize = 2),
//            ).forEach { decodeConfig ->
//                val dataSource = imageFile.toDataSource(context)
//                try {
//                    val imageInfo = dataSource.readImageInfo()
//                    val bitmap = dataSource.decode(decodeConfig)
//                    assertEquals(
//                        expected = calculateSampledBitmapSize(
//                            imageSize = imageInfo.size,
//                            sampleSize = decodeConfig.sampleSize ?: 1,
//                            mimeType = imageInfo.mimeType,
//                        ),
//                        actual = bitmap.size,
//                        message = "imageFile=${imageFile.uri}, decodeConfig=$decodeConfig, imageSize=${imageInfo.size}"
//                    )
//                } catch (e: ImageInvalidException) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    @Test
    fun testCalculateSampledBitmapSizeForRegion() {
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(width = 503, height = 101),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(289, 101),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(503, 56),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(289, 56),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            expected = Size(289, 56),
            actual = calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
            )
        )

//        val context = getTestContext()
//        listOf(
//            ComposeResImageFiles.formatJpeg,
//            ComposeResImageFiles.formatPng,
//            ComposeResImageFiles.formatBmp,
//            ComposeResImageFiles.formatWebp,
//            ComposeResImageFiles.formatHeic,
//            ComposeResImageFiles.svg,
//            ComposeResImageFiles.formatAnimGif,
//            ComposeResImageFiles.formatAnimWebp,
//            ComposeResImageFiles.animHeif,
//            ComposeResImageFiles.clockExifFlipHorizontal,
//            ComposeResImageFiles.clockExifFlipVertical,
//            ComposeResImageFiles.clockExifNormal,
//            ComposeResImageFiles.clockExifRotate90,
//            ComposeResImageFiles.clockExifRotate180,
//            ComposeResImageFiles.clockExifRotate270,
//            ComposeResImageFiles.clockExifTranspose,
//            ComposeResImageFiles.clockExifTransverse,
//            ComposeResImageFiles.clockExifUndefined,
//        ).forEach { imageFile ->
//            listOf(
//                DecodeConfig(sampleSize = 1),
//                DecodeConfig(sampleSize = 2),
//            ).forEach { decodeConfig ->
//                val dataSource = imageFile.toDataSource(context)
//                try {
//                    val imageInfo = dataSource.readImageInfo()
//                    val fullBitmap = dataSource.decodeRegion(imageInfo.size.toRect()).apply {
//                        assertSizeEquals(
//                            expected = calculateSampledBitmapSizeForRegion(
//                                regionSize = imageInfo.size,
//                                sampleSize = 1,
//                                mimeType = imageInfo.mimeType,
//                                imageSize = imageInfo.size
//                            ),
//                            actual = this.size,
//                            delta = Size(1, 1),
//                            message = "imageFile=${imageFile.uri}, decodeConfig=$decodeConfig, imageSize=${imageInfo.size}"
//                        )
//                    }
//
//                    val (topLeftRect, topRightRect, bottomLeftRect, bottomRightRect) =
//                        fullBitmap.size.toRect().chunkingFour()
//                    val topLeftBitmap = dataSource.decodeRegion(topLeftRect, decodeConfig)
//                    val topRightBitmap = dataSource.decodeRegion(topRightRect, decodeConfig)
//                    val bottomLeftBitmap = dataSource.decodeRegion(bottomLeftRect, decodeConfig)
//                    val bottomRightBitmap = dataSource.decodeRegion(bottomRightRect, decodeConfig)
//                    listOf(
//                        topLeftBitmap to topLeftRect,
//                        topRightBitmap to topRightRect,
//                        bottomLeftBitmap to bottomLeftRect,
//                        bottomRightBitmap to bottomRightRect
//                    ).forEach { (bitmap, tileRect) ->
//                        assertSizeEquals(
//                            expected = calculateSampledBitmapSizeForRegion(
//                                regionSize = tileRect.size,
//                                sampleSize = decodeConfig.sampleSize ?: 1,
//                                mimeType = imageFile.mimeType,
//                                imageSize = imageInfo.size
//                            ),
//                            actual = bitmap.size,
//                            delta = Size(1, 1),
//                            message = "imageFile=${imageFile.uri}, decodeConfig=$decodeConfig, tileRect=$tileRect, imageSize=${imageInfo.size}"
//                        )
//                    }
//                } catch (e: ImageInvalidException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
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
            expected = 4,
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
            expected = 4,
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
            expected = 4,
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
            expected = 4,
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
            expected = 8,
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
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        assertEquals(
            expected = 4,
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
            expected = 4,
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
            expected = 8,
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
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        assertEquals(
            expected = 4,
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
            expected = 32,
            actual = calculateSampleSizeForRegion(
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
            expected = 4,
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
            expected = 8,
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
            expected = 4,
            actual = calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
            )
        )

        assertEquals(
            expected = 4,
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

        val sourceImage = ComposeResImageFiles.jpeg.decode().apply {
            assertEquals(Size(1291, 1936), size)
        }
        val imageData = ImageData(
            image = sourceImage,
            imageInfo = ImageInfo(sourceImage.width, sourceImage.height, "image/jpeg"),
            dataFrom = MEMORY,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = null,
            extras = null,
        )

        val executeResize: suspend (Size, Precision, Scale) -> ImageData =
            { size, precision, scale ->
                val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                    resize(size, precision, scale)
                }
                val realResize = request.toRequestContext(sketch)
                    .computeResize(imageData.imageInfo.size)
                imageData.resize(realResize)
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
                    actual = resizeResult !== imageData,
                    message = "item=$item"
                )
                assertEquals(
                    expected = true,
                    actual = resizeResult.image !== imageData.image,
                    message = "item=$item"
                )
                assertEquals(
                    expected = true,
                    actual = resizeResult.image.getBitmapOrThrow() !== imageData.image.getBitmapOrThrow(),
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
                    actual = resizeResult !== imageData,
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
    fun testReadImageInfoWithIgnoreExifOrientation() = runTest {
        val context = getTestContext()
        assertEquals(
            expected = "ImageInfo(1291x1936,'image/jpeg')",
            actual = ComposeResImageFiles.jpeg.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x719,'image/png')",
            actual = ComposeResImageFiles.png.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(700x1012,'image/bmp')",
            actual = ComposeResImageFiles.bmp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1080x1344,'image/webp')",
            actual = ComposeResImageFiles.webp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x480,'image/gif')",
            actual = ComposeResImageFiles.animGif.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x270,'image/webp')",
            actual = ComposeResImageFiles.animWebp.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertFailsWith(ImageInvalidException::class) {
            ComposeResImageFiles.svg.toDataSource(context).readImageInfoWithIgnoreExifOrientation()
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
            ComposeResImageFiles.heic.toDataSource(context).readImageInfoWithIgnoreExifOrientation()
                .apply {
                    assertSizeEquals(ComposeResImageFiles.heic.size, size, Size(1, 1))
                    assertEquals(expected = "image/heif", actual = mimeType)
                }
            ComposeResImageFiles.animHeif.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .apply {
                    assertSizeEquals(ComposeResImageFiles.animHeif.size, size, Size(1, 1))
                    assertEquals(expected = "image/heif", actual = mimeType)
                }
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.heic.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation()
            }
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.animHeif.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation()
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            ComposeResImageFiles.avif.toDataSource(context).readImageInfoWithIgnoreExifOrientation()
                .apply {
                    assertSizeEquals(ComposeResImageFiles.avif.size, size, Size(1, 1))
                    assertEquals(expected = "image/avif", actual = mimeType)
                }
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.avif.toDataSource(context)
                    .readImageInfoWithIgnoreExifOrientation()
            }
        }
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifFlipHorizontal.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifFlipVertical.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifNormal.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate90.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate180.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate270.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifTranspose.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x1500,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifTransverse.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifUndefined.toDataSource(context)
                .readImageInfoWithIgnoreExifOrientation()
                .toShortString()
        )
    }

    @Test
    fun testReadImageInfo() = runTest {
        val context = getTestContext()
        assertEquals(
            expected = "ImageInfo(1291x1936,'image/jpeg')",
            actual = ComposeResImageFiles.jpeg.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(750x719,'image/png')",
            actual = ComposeResImageFiles.png.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(700x1012,'image/bmp')",
            actual = ComposeResImageFiles.bmp.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1080x1344,'image/webp')",
            actual = ComposeResImageFiles.webp.toDataSource(context).readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x480,'image/gif')",
            actual = ComposeResImageFiles.animGif.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertEquals(
            expected = "ImageInfo(480x270,'image/webp')",
            actual = ComposeResImageFiles.animWebp.toDataSource(context).readImageInfo()
                .toShortString()
        )
        assertFailsWith(ImageInvalidException::class) {
            ComposeResImageFiles.svg.toDataSource(context).readImageInfo()
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
            ComposeResImageFiles.heic.toDataSource(context).readImageInfo().apply {
                assertSizeEquals(ComposeResImageFiles.heic.size, size, Size(1, 1))
                assertEquals(expected = "image/heif", actual = mimeType)
            }
            ComposeResImageFiles.animHeif.toDataSource(context).readImageInfo().apply {
                assertSizeEquals(ComposeResImageFiles.animHeif.size, size, Size(1, 1))
                assertEquals(expected = "image/heif", actual = mimeType)
            }
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.heic.toDataSource(context).readImageInfo()
            }
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.animHeif.toDataSource(context).readImageInfo()
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            ComposeResImageFiles.avif.toDataSource(context).readImageInfo().apply {
                assertSizeEquals(ComposeResImageFiles.avif.size, size, Size(1, 1))
                assertEquals(expected = "image/avif", actual = mimeType)
            }
        } else {
            assertFailsWith(ImageInvalidException::class) {
                ComposeResImageFiles.avif.toDataSource(context).readImageInfo()
            }
        }
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifFlipHorizontal.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifFlipVertical.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifNormal.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate90.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate180.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifRotate270.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifTranspose.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifTransverse.toDataSource(context)
                .readImageInfo().toShortString()
        )
        assertEquals(
            expected = "ImageInfo(1500x750,'image/jpeg')",
            actual = ComposeResImageFiles.clockExifUndefined.toDataSource(context)
                .readImageInfo().toShortString()
        )

        val drawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        assertEquals(
            expected = "ImageInfo(${drawable.intrinsicSize},'image/jpeg')",
            actual = drawable.readImageInfo("image/jpeg").toShortString()
        )
        assertEquals(
            expected = "ImageInfo(${drawable.intrinsicSize},'image/png')",
            actual = drawable.readImageInfo().toShortString()
        )
    }

    @Test
    fun testDecode() = runTest {
        val context = getTestContext()

        /*
         * config: sampleSize
         */
        val imageFile = ComposeResImageFiles.jpeg
        val dataSource = imageFile.toDataSource(context)
        dataSource
            .decode()
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
        dataSource
            .decode(DecodeConfig(sampleSize = 2))
            .apply {
                assertSizeEquals(
                    expected = calculateSampledBitmapSize(imageFile.size, 2),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * config: colorType
         */
        dataSource
            .decode()
            .apply {
                assertEquals(
                    expected = ColorType.ARGB_8888,
                    actual = colorType,
                )
            }
        dataSource
            .decode(DecodeConfig(colorType = ColorType.RGB_565))
            .apply {
                assertEquals(
                    expected = ColorType.RGB_565,
                    actual = colorType,
                )
            }

        /*
         * config: colorSpace
         */
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            dataSource
                .decode()
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
            dataSource
                .decode(DecodeConfig(colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)))
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                        actual = colorSpace,
                    )
                }
        }

        /*
         * config: preferQualityOverSpeed
         */
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            val bitmap = dataSource
                .decode(DecodeConfig(preferQualityOverSpeed = true))
            var preferSpeedBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferSpeedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferSpeedBitmap = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                    .decode(DecodeConfig(preferQualityOverSpeed = false))
            }

            var preferQualityBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferQualityBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferQualityBitmap = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                    .decode(DecodeConfig(preferQualityOverSpeed = true))
            }

            val preferSpeedSimilarity = bitmap.similarity(preferSpeedBitmap)
            val preferQualitySimilarity = bitmap.similarity(preferQualityBitmap)
            assertEquals(expected = 10, actual = preferSpeedSimilarity)
            assertEquals(expected = 0, actual = preferQualitySimilarity)
        }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ComposeResImageFiles.clockExifs.forEach { exifImageFile ->
            val bitmap = exifImageFile.toDataSource(context).decode()
            assertEquals(
                expected = exifImageFile.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            val firstBitmap1 = firstBitmap ?: bitmap.apply { firstBitmap = this }
            assertEquals(
                expected = firstBitmap1.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            assertEquals(
                expected = 0,
                actual = firstBitmap1.similarity(bitmap),
                message = "imageFile=${exifImageFile.uri}"
            )
        }

        /*
         * exifOrientationHelper
         */
        dataSource
            .decode()
            .apply {
                assertEquals(
                    expected = imageFile.size,
                    actual = size,
                )
            }
        dataSource
            .decode(exifOrientationHelper = ExifOrientationHelper(ExifOrientationHelper.ROTATE_90))
            .apply {
                assertEquals(
                    expected = imageFile.size.rotate(90),
                    actual = size,
                )
            }

        /*
         * error
         */
        assertFailsWith(ImageInvalidException::class) {
            ComposeResImageFiles.svg.toDataSource(context).decode()
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val context = getTestContext()

        /*
         * srcRect
         */
        val imageFile = ComposeResImageFiles.jpeg
        val dataSource = imageFile.toDataSource(context)
        val imageInfo = dataSource.readImageInfo()

        val sourceBitmap = dataSource.decodeRegion(
            srcRect = imageInfo.size.toRect(),
            imageSize = imageInfo.size,
        ).apply {
            assertEquals(
                expected = imageInfo.size,
                actual = this.size,
            )
        }

        // Divide into four pieces
        val (topLeftRect, topRightRect, bottomLeftRect, bottomRightRect) =
            imageInfo.size.toRect().chunkingFour()
        val topLeftBitmap = dataSource.decodeRegion(topLeftRect)
        val topRightBitmap = dataSource.decodeRegion(topRightRect)
        val bottomLeftBitmap = dataSource.decodeRegion(bottomLeftRect)
        val bottomRightBitmap = dataSource.decodeRegion(bottomRightRect)
        listOf(
            topLeftBitmap to topLeftRect,
            topRightBitmap to topRightRect,
            bottomLeftBitmap to bottomLeftRect,
            bottomRightBitmap to bottomRightRect
        ).forEach { (bitmap, tileRect) ->
            assertSizeEquals(
                expected = calculateSampledBitmapSizeForRegion(
                    regionSize = tileRect.size,
                    sampleSize = 1,
                ),
                actual = bitmap.size,
                delta = Size(1, 1),
                message = "tileRect=$tileRect"
            )
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
                message = "index=$index, similarity=$similarity"
            )
        }

        // Merge four pictures
        val mergedBitmap = createBitmap(
            width = imageInfo.width,
            height = imageInfo.height,
            config = topLeftBitmap.colorType.safeToSoftware()
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
                actual = this <= 6,
                message = "similarity=$this"
            )
        }

        /*
         * config: sampleSize
         */
        dataSource.decodeRegion(imageInfo.size.toRect())
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
        dataSource.decodeRegion(imageInfo.size.toRect(), DecodeConfig(sampleSize = 2))
            .apply {
                assertSizeEquals(
                    expected = calculateSampledBitmapSize(imageFile.size, 2),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * config: colorType
         */
        dataSource
            .decodeRegion(imageInfo.size.toRect())
            .apply {
                assertEquals(
                    expected = ColorType.ARGB_8888,
                    actual = colorType,
                )
            }
        dataSource
            .decodeRegion(imageInfo.size.toRect(), DecodeConfig(colorType = ColorType.RGB_565))
            .apply {
                assertEquals(
                    expected = ColorType.RGB_565,
                    actual = colorType,
                )
            }

        /*
         * config: colorSpace
         */
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            dataSource
                .decodeRegion(imageInfo.size.toRect())
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
            dataSource
                .decodeRegion(
                    srcRect = imageInfo.size.toRect(),
                    config = DecodeConfig(colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
                )
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                        actual = colorSpace,
                    )
                }
        }

        /*
         * config: preferQualityOverSpeed
         */
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            val bitmap = dataSource
                .decodeRegion(
                    imageInfo.size.toRect(),
                    DecodeConfig(preferQualityOverSpeed = true)
                )
            var preferSpeedBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferSpeedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferSpeedBitmap = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                    .decodeRegion(
                        imageInfo.size.toRect(),
                        DecodeConfig(preferQualityOverSpeed = false)
                    )
            }

            var preferQualityBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferQualityBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferQualityBitmap = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                    .decodeRegion(
                        imageInfo.size.toRect(),
                        DecodeConfig(preferQualityOverSpeed = true)
                    )
            }

            val preferSpeedSimilarity = bitmap.similarity(preferSpeedBitmap)
            val preferQualitySimilarity = bitmap.similarity(preferQualityBitmap)
            assertEquals(expected = 10, actual = preferSpeedSimilarity)
            assertEquals(expected = 0, actual = preferQualitySimilarity)
        }

        /*
         * imageSize
         */
        dataSource
            .decodeRegion(imageInfo.size.toRect(), imageSize = imageInfo.size)
            .apply {
                assertEquals(
                    expected = imageInfo.size,
                    actual = size,
                )
            }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ComposeResImageFiles.clockExifs.forEach { exifImageFile ->
            val exifDataSource = exifImageFile.toDataSource(context)
            val bitmap = exifDataSource.decodeRegion(exifImageFile.size.toRect())
            assertEquals(
                expected = exifImageFile.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            val firstBitmap1 = firstBitmap ?: bitmap.apply { firstBitmap = this }
            assertEquals(
                expected = firstBitmap1.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            assertEquals(
                expected = 0,
                actual = firstBitmap1.similarity(bitmap),
                message = "imageFile=${exifImageFile.uri}"
            )
        }

        /*
         * exifOrientationHelper
         */
        dataSource
            .decodeRegion(imageInfo.size.toRect())
            .apply {
                assertEquals(
                    expected = imageInfo.size,
                    actual = size,
                )
            }
        dataSource
            .decodeRegion(
                srcRect = imageInfo.size.rotate(90).toRect(),
                exifOrientationHelper = ExifOrientationHelper(ExifOrientationHelper.ROTATE_90)
            )
            .apply {
                assertEquals(
                    expected = imageInfo.size.rotate(90),
                    actual = size,
                )
            }

        /*
         * error
         */
        // IOException: Image format is not supported
        assertFailsWith(IOException::class) {
            val svgImageFile = ComposeResImageFiles.svg
            svgImageFile.toDataSource(context).decodeRegion(svgImageFile.size.toRect())
        }
        // IllegalArgumentException: rectangle is outside the image
        assertFailsWith(IllegalArgumentException::class) {
            ComposeResImageFiles.jpeg.toDataSource(context).decodeRegion(Size.Empty.toRect())
        }
    }

    @Test
    fun testSupportBitmapRegionDecoder() = runTest {
        val context = getTestContext()
        listOf(
            ComposeResImageFiles.jpeg,
            ComposeResImageFiles.png,
            ComposeResImageFiles.bmp,
            ComposeResImageFiles.webp,
            ComposeResImageFiles.heic,
            ComposeResImageFiles.avif,
            ComposeResImageFiles.svg,
            ComposeResImageFiles.animGif,
            ComposeResImageFiles.animWebp,
            ComposeResImageFiles.animHeif,
        ).forEach { imageFile ->
            val dataSource = imageFile.toDataSource(context)
            val result = runCatching {
                dataSource.decodeRegion(srcRect = (imageFile.size / 2f).toRect())
            }
            if (ImageFormat.HEIC.mimeType == imageFile.mimeType || ImageFormat.HEIF.mimeType == imageFile.mimeType) {
                // Images in heif format will report an error on the 33 simulator, but the real machine is OK.
                val expected =
                    supportBitmapRegionDecoder(imageFile.mimeType, imageFile.animated) == true
                if (expected) {
                    assertEquals(
                        expected = true,
                        actual = result.isSuccess || result.isFailure,
                        message = "imageFile=${imageFile.uri}, failure: '${result.exceptionOrNull()}'"
                    )
                } else {
                    assertEquals(
                        expected = false,
                        actual = result.isSuccess,
                        message = "imageFile=${imageFile.uri}, failure: '${result.exceptionOrNull()}'"
                    )
                }
            } else {
                assertEquals(
                    expected = supportBitmapRegionDecoder(imageFile.mimeType, imageFile.animated),
                    actual = result.isSuccess,
                    message = "imageFile=${imageFile.uri}, failure: '${result.exceptionOrNull()}'"
                )
            }
        }

        assertEquals(
            expected = null,
            actual = supportBitmapRegionDecoder("image/fake", animated = true)
        )
        assertEquals(
            expected = null,
            actual = supportBitmapRegionDecoder("image/fake", animated = false)
        )
        assertEquals(
            expected = false,
            actual = supportBitmapRegionDecoder("video/mp4", animated = false)
        )
    }
}