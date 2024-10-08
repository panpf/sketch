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
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampleSizeForRegion
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createSubsamplingTransformed
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.internal.decodeRegionBitmap
import com.github.panpf.sketch.decode.internal.getMaxBitmapSize
import com.github.panpf.sketch.decode.internal.newDecodeConfigByQualityParams
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactory
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.test.utils.toSizeString
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toAndroidRect
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DecodesAndroidTest {

    @Test
    fun testGetMaxBitmapSize() {
        // TODO test
    }

    @Test
    fun testCalculateSampledBitmapSize() {
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg"
            )
        )
        assertEquals(
            Size(502, 100),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png"
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/bmp"
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/gif"
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/webp"
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/heic"
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/heif"
            )
        )
    }

    @Test
    fun testCalculateSampledBitmapSizeForRegion() {
        assertEquals(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Size(503, 101) else Size(502, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(502, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(288, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(502, 55),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(288, 55),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(288, 55),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
            )
        )
    }

    @Test
    fun testCalculateSampleSize() {
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = false
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = false
            )
        )
        assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = false
            )
        )

        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = false
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif",
                smallerSizeMode = false
            )
        )

        // smallerSizeMode = true
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = true
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = true
            )
        )
        assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = true
            )
        )

        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = true
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
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
            expected,
            calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = false
            )
        )
        assertEquals(
            32,
            calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = true
            )
        )
    }

    @Test
    fun testCalculateSampleSize2() {
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
            )
        )
        assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
            )
        )

        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg"
            )
        )
        assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png"
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp"
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp"
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif"
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic"
            )
        )
        assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif"
            )
        )
    }

    @Test
    fun testCalculateSampleSizeForRegion() {
        assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )

        assertEquals(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        // smallerSizeMode = true
        assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )

        assertEquals(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        assertEquals(
            2,
            calculateSampleSizeForRegion(
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
            expected,
            calculateSampleSizeForRegion(
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
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
            )
        )
        assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
            )
        )

        assertEquals(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
            )
        )

        assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
            )
        )
    }

    @Test
    fun testRealDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val hasExifFile =
            ExifOrientationTestFileHelper(context, ResourceImages.jpeg.resourceName)
                .files().find { it.exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 }!!

        val result1 = ImageRequest(context, hasExifFile.file.path) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(imageInfo.size, image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }

        ImageRequest(context, hasExifFile.file.path) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(imageInfo.size, image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(
                result1.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result3 = ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(EXACTLY)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformeds
            )
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(EXACTLY)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformeds
            )
            assertEquals(
                result3.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result5 = ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(SAME_ASPECT_RATIO)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformeds
            )
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(SAME_ASPECT_RATIO)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformeds
            )
            assertEquals(
                result5.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result7 = ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(LESS_PIXELS)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(121, 81), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(16)), transformeds)
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            size(100, 200)
            precision(LESS_PIXELS)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(1936, 1291, "image/jpeg")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = { rect, sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeRegionBitmap(rect.toAndroidRect(), decodeOptions)!!
                        .asSketchImage()
                }
            )
        }.apply {
            assertEquals(Size(121, 81), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(1936, 1291, "image/jpeg"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(16)), transformeds)
            assertEquals(
                result7.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result9 = ImageRequest(context, ResourceImages.bmp.uri) {
            size(100, 200)
            precision(EXACTLY)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(700, 1012, "image/bmp")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = null
            )
        }.apply {
            assertEquals(Size(87, 126), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(700, 1012, "image/bmp"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(8)), transformeds)
        }

        ImageRequest(context, ResourceImages.bmp.uri).newRequest {
            size(100, 200)
            precision(EXACTLY)
        }.let { request ->
            val fetchResult = sketch.components.newFetcherOrThrow(request).fetch().getOrThrow()
            val imageInfo = ImageInfo(700, 1012, "image/bmp")
            realDecode(
                requestContext = request.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    val decodeOptions =
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .apply { inSampleSize = sampleSize }
                            .toBitmapOptions()
                    fetchResult.dataSource.decodeBitmap(decodeOptions)!!.asSketchImage()
                },
                decodeRegion = null
            )
        }.apply {
            assertEquals(Size(87, 126), image.getBitmapOrThrow().size)
            assertEquals(ImageInfo(700, 1012, "image/bmp"), imageInfo)
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(8)), transformeds)
            assertEquals(
                result9.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }
    }

    @Test
    fun testAppliedResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        var request = ImageRequest(context, ResourceImages.jpeg.uri)
        val newResult: () -> DecodeResult = {
            DecodeResult(
                image = Bitmap.createBitmap(80, 50, ARGB_8888).asSketchImage(),
                imageInfo = ImageInfo(80, 50, "image/png"),
                dataFrom = MEMORY,
                resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
                transformeds = null,
                extras = null,
            )
        }

        /*
         * LESS_PIXELS
         */
        // small
        request = request.newRequest {
            resize(40, 20, LESS_PIXELS, CENTER_CROP)
        }
        var result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this !== result)
            assertEquals("20x13", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, LESS_PIXELS)
        }
        result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this === result)
        }

        /*
         * SAME_ASPECT_RATIO
         */
        // small
        request = request.newRequest {
            resize(40, 20, SAME_ASPECT_RATIO)
        }
        result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this !== result)
            assertEquals("40x20", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, SAME_ASPECT_RATIO)
        }
        result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this !== result)
            assertEquals("17x50", this.image.getBitmapOrThrow().toSizeString())
        }

        /*
         * EXACTLY
         */
        // small
        request = request.newRequest {
            resize(40, 20, EXACTLY)
        }
        result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this !== result)
            assertEquals("40x20", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, EXACTLY)
        }
        result = newResult()
        result.appliedResize(request.toRequestContext(sketch)).apply {
            assertTrue(this !== result)
            assertEquals("50x150", this.image.getBitmapOrThrow().toSizeString())
        }
    }

    @Test
    fun testReadImageInfoWithBitmapFactory() {
        val context = getTestContext()

        AssetDataSource(
            context,
            ResourceImages.jpeg.resourceName
        )
            .readImageInfoWithBitmapFactory().apply {
                assertEquals(1291, width)
                assertEquals(1936, height)
                assertEquals("image/jpeg", mimeType)
            }

        AssetDataSource(
            context,
            ResourceImages.webp.resourceName
        )
            .readImageInfoWithBitmapFactory().apply {
                assertEquals(1080, width)
                assertEquals(1344, height)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals("image/webp", mimeType)
                } else {
                    assertEquals("", mimeType)
                }
            }

        ResourceDataSource(
            resources = context.resources,
            packageName = context.packageName,
            resId = com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
        ).readImageInfoWithBitmapFactory().apply {
            assertEquals(-1, width)
            assertEquals(-1, height)
            assertEquals("", mimeType)
        }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrThrow() {
        val context = getTestContext()

        AssetDataSource(
            context,
            ResourceImages.jpeg.resourceName
        )
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                assertEquals(1291, width)
                assertEquals(1936, height)
                assertEquals("image/jpeg", mimeType)
            }
        AssetDataSource(
            context,
            ResourceImages.webp.resourceName
        )
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                assertEquals(1080, width)
                assertEquals(1344, height)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals("image/webp", mimeType)
                } else {
                    assertEquals("", mimeType)
                }
            }

        assertThrow(ImageInvalidException::class) {
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrThrow()
        }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrNull() {
        val context = getTestContext()

        AssetDataSource(
            context,
            ResourceImages.jpeg.resourceName
        ).readImageInfoWithBitmapFactoryOrNull()!!.apply {
            assertEquals(1291, width)
            assertEquals(1936, height)
            assertEquals("image/jpeg", mimeType)
        }

        AssetDataSource(
            context,
            ResourceImages.webp.resourceName
        ).readImageInfoWithBitmapFactoryOrNull()!!.apply {
            assertEquals(1080, width)
            assertEquals(1344, height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assertEquals("image/webp", mimeType)
            } else {
                assertEquals("", mimeType)
            }
        }

        assertNull(
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrNull()
        )
    }

    @Test
    fun testDecodeBitmap() {
        val context = getTestContext()

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).decodeBitmap()!!.apply {
            assertEquals(1291, width)
            assertEquals(1936, height)
        }

        AssetDataSource(
            context = context,
            fileName = ResourceImages.jpeg.resourceName
        ).decodeBitmap(BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                assertEquals(646, width)
                assertEquals(968, height)
            }

        AssetDataSource(
            context,
            ResourceImages.webp.resourceName
        ).decodeBitmap()!!.apply {
            assertEquals(1080, width)
            assertEquals(1344, height)
        }

        assertNull(
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).decodeBitmap()
        )
    }

    @Test
    fun testDecodeRegionBitmap() {
        val context = getTestContext()

        AssetDataSource(
            context,
            ResourceImages.jpeg.resourceName
        )
            .decodeRegionBitmap(android.graphics.Rect(500, 500, 600, 600))!!.apply {
                assertEquals(100, width)
                assertEquals(100, height)
            }

        AssetDataSource(
            context,
            ResourceImages.jpeg.resourceName
        )
            .decodeRegionBitmap(
                android.graphics.Rect(500, 500, 600, 600),
                BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                assertEquals(50, width)
                assertEquals(50, height)
            }

        AssetDataSource(
            context,
            ResourceImages.webp.resourceName
        )
            .decodeRegionBitmap(android.graphics.Rect(500, 500, 700, 700))!!.apply {
                assertEquals(200, width)
                assertEquals(200, height)
            }

        assertThrow(IOException::class) {
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = com.github.panpf.sketch.test.utils.core.R.xml.network_security_config
            ).decodeRegionBitmap(android.graphics.Rect(500, 500, 600, 600))
        }
    }

    @Test
    fun testNewDecodeConfigByQualityParams() {
        // TODO test
    }

    @Test
    fun testSupportBitmapRegionDecoder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            assertTrue(ImageFormat.HEIC.supportBitmapRegionDecoder())
        } else {
            assertFalse(ImageFormat.HEIC.supportBitmapRegionDecoder())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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