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
package com.github.panpf.sketch.core.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.OpenGLTextureHelper
import com.github.panpf.sketch.decode.internal.appliedExifOrientation
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampleSizeForRegion
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.computeSizeMultiplier
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createSubsamplingTransformed
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.internal.decodeRegionBitmap
import com.github.panpf.sketch.decode.internal.getExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.isInBitmapError
import com.github.panpf.sketch.decode.internal.isSrcRectError
import com.github.panpf.sketch.decode.internal.isSupportInBitmap
import com.github.panpf.sketch.decode.internal.isSupportInBitmapForRegion
import com.github.panpf.sketch.decode.internal.limitedSampleSizeByMaxBitmapSize
import com.github.panpf.sketch.decode.internal.limitedSampleSizeByMaxBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactory
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.decode.internal.toSizeString()
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DecodeUtilsTest {

    @Test
    fun testCalculateSampledBitmapSize() {
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2
            )
        )
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg"
            )
        )
        Assert.assertEquals(
            Size(502, 100),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png"
            )
        )
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/bmp"
            )
        )
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/gif"
            )
        )
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/webp"
            )
        )
        Assert.assertEquals(
            Size(503, 101),
            calculateSampledBitmapSize(
                imageSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/heic"
            )
        )
        Assert.assertEquals(
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
        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.N) Size(503, 101) else Size(502, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        Assert.assertEquals(
            Size(502, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png",
                imageSize = Size(1005, 201)
            )
        )
        Assert.assertEquals(
            Size(288, 100),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        Assert.assertEquals(
            Size(502, 55),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        Assert.assertEquals(
            Size(288, 55),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        Assert.assertEquals(
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
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
            )
        )

        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg"
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png"
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp"
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp"
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif"
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic"
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif"
            )
        )
    }

    @Test
    fun testCalculateSampleSize2() {
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = false
            )
        )

        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif",
                smallerSizeMode = false
            )
        )

        // smallerSizeMode = true
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(503, 101),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(252, 51),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(251, 50),
                smallerSizeMode = true
            )
        )

        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/jpeg",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/bmp",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/webp",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/gif",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heic",
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSize(
                imageSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/heif",
                smallerSizeMode = true
            )
        )


        val maxSize = OpenGLTextureHelper.maxSize ?: 0
        val expected = when {
            maxSize <= 4096 -> 32
            maxSize <= 8192 -> 16
            else -> 4
        }
        Assert.assertEquals(
            expected,
            calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            32,
            calculateSampleSize(
                imageSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = true
            )
        )
    }

    @Test
    fun testCalculateSampleSizeForRegion() {
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
            )
        )

        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
            )
        )

        Assert.assertEquals(
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
    fun testCalculateSampleSizeForRegion2() {
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )

        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = false
            )
        )

        Assert.assertEquals(
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
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1006, 202),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            1,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(1004, 200),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(501, 99),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            4,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(251, 50),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )
        Assert.assertEquals(
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = true
            )
        )

        Assert.assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.N) 4 else 2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        Assert.assertEquals(
            2,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(502, 100),
                mimeType = "image/png",
                imageSize = Size(1005, 201),
                smallerSizeMode = true
            )
        )

        val maxSize = OpenGLTextureHelper.maxSize ?: 0
        val expected = when {
            maxSize <= 4096 -> 32
            maxSize <= 8192 -> 16
            else -> 4
        }
        Assert.assertEquals(
            expected,
            calculateSampleSizeForRegion(
                regionSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = false
            )
        )
        Assert.assertEquals(
            32,
            calculateSampleSizeForRegion(
                regionSize = Size(30000, 750),
                targetSize = Size(1080, 1920),
                smallerSizeMode = true
            )
        )
    }

    @Test
    fun testLimitedSampleSizeByMaxBitmapSize() {
        val maxSize = OpenGLTextureHelper.maxSize ?: Canvas().maximumBitmapWidth
        val targetSize = Size(10180, 1920)
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize - 1, maxSize), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize, maxSize - 1), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize - 1, maxSize - 1), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize + 1, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize, maxSize + 1), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSize(1, Size(maxSize + 1, maxSize + 1), targetSize)
        )

        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(0, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSize(-1, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSize(-1, Size(maxSize + 1, maxSize + 1), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSize(0, Size(maxSize + 1, maxSize + 1), targetSize)
        )
    }

    @Test
    fun testLimitedSampleSizeByMaxBitmapSizeForRegion() {
        val maxSize = OpenGLTextureHelper.maxSize ?: Canvas().maximumBitmapWidth
        val targetSize = Size(10180, 1920)
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize - 1, maxSize), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize, maxSize - 1), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize - 1, maxSize - 1), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize + 1, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize, maxSize + 1), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSizeForRegion(1, Size(maxSize + 1, maxSize + 1), targetSize)
        )

        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(0, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            1,
            limitedSampleSizeByMaxBitmapSizeForRegion(-1, Size(maxSize, maxSize), targetSize)
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSizeForRegion(
                -1,
                Size(maxSize + 1, maxSize + 1),
                targetSize
            )
        )
        Assert.assertEquals(
            2,
            limitedSampleSizeByMaxBitmapSizeForRegion(0, Size(maxSize + 1, maxSize + 1), targetSize)
        )
    }

    @Test
    fun testRealDecode() {
        val (context, sketch) = getTestContextAndSketch()

        val hasExifFile =
            ExifOrientationTestFileHelper(context, context.sketch, AssetImages.jpeg.fileName)
                .files().find { it.exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 }!!

        val result1 = ImageRequest(context, hasExifFile.file.path) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
        }.let {
            realDecode(
                it.toRequestContext(sketch),
                LOCAL,
                ImageInfo(1936, 1291, "image/jpeg", hasExifFile.exifOrientation),
                { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(imageInfo.size, image.getBitmapOrThrow().size)
            Assert.assertEquals(
                ImageInfo(
                    1936,
                    1291,
                    "image/jpeg",
                    ExifInterface.ORIENTATION_ROTATE_90
                ), imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, hasExifFile.file.path) {
            resizeSize(3000, 3000)
            resizePrecision(LESS_PIXELS)
            ignoreExifOrientation(true)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", hasExifFile.exifOrientation),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(imageInfo.size, image.getBitmapOrThrow().size)
            Assert.assertEquals(
                ImageInfo(
                    1936,
                    1291,
                    "image/jpeg",
                    ExifInterface.ORIENTATION_ROTATE_90
                ), imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
            Assert.assertEquals(
                result1.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result3 = ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(EXACTLY)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", hasExifFile.exifOrientation),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(121, 60), image.getBitmapOrThrow().size)
            Assert.assertEquals(
                ImageInfo(
                    1936,
                    1291,
                    "image/jpeg",
                    ExifInterface.ORIENTATION_ROTATE_90
                ), imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(
                    createInSampledTransformed(16),
                    createSubsamplingTransformed(Rect(0, 161, 1936, 1129))
                ),
                transformedList
            )
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(EXACTLY)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", 0),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            Assert.assertEquals(ImageInfo(1936, 1291, "image/jpeg", 0), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformedList
            )
            Assert.assertNotEquals(
                result3.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result5 = ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", hasExifFile.exifOrientation),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(121, 60), image.getBitmapOrThrow().size)
            Assert.assertEquals(
                ImageInfo(
                    1936,
                    1291,
                    "image/jpeg",
                    ExifInterface.ORIENTATION_ROTATE_90
                ), imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(
                    createInSampledTransformed(16),
                    createSubsamplingTransformed(Rect(0, 161, 1936, 1129))
                ),
                transformedList
            )
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(SAME_ASPECT_RATIO)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", 0),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(80, 161), image.getBitmapOrThrow().size)
            Assert.assertEquals(ImageInfo(1936, 1291, "image/jpeg", 0), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(645, 0, 1290, 1291))
                ),
                transformedList
            )
            Assert.assertNotEquals(
                result5.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result7 = ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(LESS_PIXELS)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", hasExifFile.exifOrientation),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(121, 81), image.getBitmapOrThrow().size)
            Assert.assertEquals(
                ImageInfo(
                    1936,
                    1291,
                    "image/jpeg",
                    ExifInterface.ORIENTATION_ROTATE_90
                ), imageInfo
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createInSampledTransformed(16)), transformedList)
        }

        ImageRequest(context, hasExifFile.file.path).newRequest {
            resizeSize(100, 200)
            resizePrecision(LESS_PIXELS)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(1936, 1291, "image/jpeg", 0),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                }
            ) { rect, config ->
                runBlocking {
                    sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                }.dataSource.asOrThrow<BasedStreamDataSource>()
                    .decodeRegionBitmap(rect, config.toBitmapOptions())!!
            }
        }.apply {
            Assert.assertEquals(Size(121, 81), image.getBitmapOrThrow().size)
            Assert.assertEquals(ImageInfo(1936, 1291, "image/jpeg", 0), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createInSampledTransformed(16)), transformedList)
            Assert.assertEquals(
                result7.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }

        val result9 = ImageRequest(context, AssetImages.bmp.uri) {
            resizeSize(100, 200)
            resizePrecision(EXACTLY)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(700, 1012, "image/bmp", 0),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                },
                decodeRegion = null
            )
        }.apply {
            Assert.assertEquals(Size(87, 126), image.getBitmapOrThrow().size)
            Assert.assertEquals(ImageInfo(700, 1012, "image/bmp", 0), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createInSampledTransformed(8)), transformedList)
        }

        ImageRequest(context, AssetImages.bmp.uri).newRequest {
            resizeSize(100, 200)
            resizePrecision(EXACTLY)
            ignoreExifOrientation(true)
        }.let {
            realDecode(
                requestContext = it.toRequestContext(sketch),
                dataFrom = LOCAL,
                imageInfo = ImageInfo(700, 1012, "image/jpeg", 0),
                decodeFull = { config ->
                    runBlocking {
                        sketch.components.newFetcherOrThrow(it).fetch()
                    }.getOrThrow().dataSource.asOrThrow<BasedStreamDataSource>()
                        .decodeBitmap(config.toBitmapOptions())!!
                },
                decodeRegion = null
            )
        }.apply {
            Assert.assertEquals(Size(87, 126), image.getBitmapOrThrow().size)
            Assert.assertEquals(ImageInfo(700, 1012, "image/jpeg", 0), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(listOf(createInSampledTransformed(8)), transformedList)
            Assert.assertEquals(
                result9.image.getBitmapOrThrow().corners(),
                image.getBitmapOrThrow().corners()
            )
        }
    }

    @Test
    fun testAppliedExifOrientation() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(context, AssetImages.jpeg.uri)

        val hasExifFile =
            ExifOrientationTestFileHelper(context, context.sketch, AssetImages.jpeg.fileName)
                .files().find { it.exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 }!!
        val bitmap = BitmapFactory.decodeFile(hasExifFile.file.path)

        val result = DecodeResult(
            image = bitmap.asSketchImage(),
            imageInfo = ImageInfo(
                width = bitmap.width,
                height = bitmap.height,
                mimeType = "image/jpeg",
                exifOrientation = hasExifFile.exifOrientation
            ),
            dataFrom = LOCAL,
            transformedList = null,
            extras = null,
        )
        val resultCorners = result.image.getBitmapOrThrow().corners()
        Assert.assertNull(result.transformedList?.getExifOrientationTransformed())

        result.appliedExifOrientation(
            sketch,
            request.toRequestContext(sketch)
        ).apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotSame(result.image.getBitmapOrThrow(), this.image.getBitmapOrThrow())
            Assert.assertEquals(
                Size(
                    result.image.getBitmapOrThrow().height,
                    result.image.getBitmapOrThrow().width
                ), this.image.getBitmapOrThrow().size
            )
            Assert.assertEquals(
                Size(result.imageInfo.height, result.imageInfo.width),
                this.imageInfo.size
            )
            Assert.assertNotEquals(resultCorners, this.image.getBitmapOrThrow().corners())
            Assert.assertNotNull(this.transformedList?.getExifOrientationTransformed())
        }

        val noExifOrientationResult = result.newResult(
            imageInfo = result.imageInfo.copy(exifOrientation = 0)
        )
        noExifOrientationResult.appliedExifOrientation(
            sketch,
            request.toRequestContext(sketch)
        ).apply {
            Assert.assertSame(noExifOrientationResult, this)
        }
    }

    @Test
    fun testAppliedResize() {
        val (context, sketch) = getTestContextAndNewSketch()
        var request = ImageRequest(context, AssetImages.jpeg.uri)
        val newResult: () -> DecodeResult = {
            DecodeResult(
                image = Bitmap.createBitmap(80, 50, ARGB_8888).asSketchImage(),
                imageInfo = ImageInfo(80, 50, "image/png", 0),
                dataFrom = MEMORY,
                transformedList = null,
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
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("20x13", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, LESS_PIXELS)
        }
        result = newResult()
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this === result)
        }

        /*
         * SAME_ASPECT_RATIO
         */
        // small
        request = request.newRequest {
            resize(40, 20, SAME_ASPECT_RATIO)
        }
        result = newResult()
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("40x20", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, SAME_ASPECT_RATIO)
        }
        result = newResult()
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("17x50", this.image.getBitmapOrThrow().toSizeString())
        }

        /*
         * EXACTLY
         */
        // small
        request = request.newRequest {
            resize(40, 20, EXACTLY)
        }
        result = newResult()
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("40x20", this.image.getBitmapOrThrow().toSizeString())
        }
        // big
        request = request.newRequest {
            resize(50, 150, EXACTLY)
        }
        result = newResult()
        result.appliedResize(sketch, request.toRequestContext(sketch)).apply {
            Assert.assertTrue(this !== result)
            Assert.assertEquals("50x150", this.image.getBitmapOrThrow().toSizeString())
        }
    }

    @Test
    fun testComputeSizeMultiplier() {
        Assert.assertEquals(0.2, computeSizeMultiplier(1000, 600, 200, 400, true), 0.1)
        Assert.assertEquals(0.6, computeSizeMultiplier(1000, 600, 200, 400, false), 0.1)
        Assert.assertEquals(0.3, computeSizeMultiplier(1000, 600, 400, 200, true), 0.1)
        Assert.assertEquals(0.4, computeSizeMultiplier(1000, 600, 400, 200, false), 0.1)

        Assert.assertEquals(0.6, computeSizeMultiplier(1000, 600, 2000, 400, true), 0.1)
        Assert.assertEquals(2.0, computeSizeMultiplier(1000, 600, 2000, 400, false), 0.1)
        Assert.assertEquals(0.4, computeSizeMultiplier(1000, 600, 400, 2000, true), 0.1)
        Assert.assertEquals(3.3, computeSizeMultiplier(1000, 600, 400, 2000, false), 0.1)

        Assert.assertEquals(2.0, computeSizeMultiplier(1000, 600, 2000, 4000, true), 0.1)
        Assert.assertEquals(6.6, computeSizeMultiplier(1000, 600, 2000, 4000, false), 0.1)
        Assert.assertEquals(3.3, computeSizeMultiplier(1000, 600, 4000, 2000, true), 0.1)
        Assert.assertEquals(4.0, computeSizeMultiplier(1000, 600, 4000, 2000, false), 0.1)
    }

    @Test
    fun testReadImageInfoWithBitmapFactory() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.webp.uri),
            AssetImages.webp.fileName
        )
            .readImageInfoWithBitmapFactory().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals("image/webp", mimeType)
                } else {
                    Assert.assertEquals("", mimeType)
                }
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        ResourceDataSource(
            sketch,
            ImageRequest(
                context,
                newResourceUri(com.github.panpf.sketch.test.utils.R.xml.network_security_config)
            ),
            packageName = context.packageName,
            context.resources,
            com.github.panpf.sketch.test.utils.R.xml.network_security_config
        ).readImageInfoWithBitmapFactory().apply {
            Assert.assertEquals(-1, width)
            Assert.assertEquals(-1, height)
            Assert.assertEquals("", mimeType)
            Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
        }

        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files()
            .forEach {
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactory().apply {
                        Assert.assertEquals(it.exifOrientation, exifOrientation)
                    }
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactory(true).apply {
                        Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
                    }
            }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrThrow() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }
        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.webp.uri),
            AssetImages.webp.fileName
        )
            .readImageInfoWithBitmapFactoryOrThrow().apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals("image/webp", mimeType)
                } else {
                    Assert.assertEquals("", mimeType)
                }
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        assertThrow(ImageInvalidException::class) {
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrThrow()
        }

        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files()
            .forEach {
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactoryOrThrow().apply {
                        Assert.assertEquals(it.exifOrientation, exifOrientation)
                    }
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactoryOrThrow(true).apply {
                        Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
                    }
            }
    }

    @Test
    fun testReadImageInfoWithBitmapFactoryOrNull() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
                Assert.assertEquals("image/jpeg", mimeType)
                Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, exifOrientation)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.webp.uri),
            AssetImages.webp.fileName
        )
            .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    Assert.assertEquals("image/webp", mimeType)
                } else {
                    Assert.assertEquals("", mimeType)
                }
                Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.R.xml.network_security_config
            ).readImageInfoWithBitmapFactoryOrNull()
        )

        ExifOrientationTestFileHelper(
            context,
            context.sketch,
            AssetImages.clockHor.fileName
        ).files()
            .forEach {
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactoryOrNull()!!.apply {
                        Assert.assertEquals(it.exifOrientation, exifOrientation)
                    }
                FileDataSource(sketch, ImageRequest(context, it.file.path), it.file)
                    .readImageInfoWithBitmapFactoryOrNull(true)!!.apply {
                        Assert.assertEquals(ExifInterface.ORIENTATION_UNDEFINED, exifOrientation)
                    }
            }
    }

    @Test
    fun testDecodeBitmap() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .decodeBitmap()!!.apply {
                Assert.assertEquals(1291, width)
                Assert.assertEquals(1936, height)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .decodeBitmap(BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(646, width)
                Assert.assertEquals(968, height)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.webp.uri),
            AssetImages.webp.fileName
        )
            .decodeBitmap()!!.apply {
                Assert.assertEquals(1080, width)
                Assert.assertEquals(1344, height)
            }

        Assert.assertNull(
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.R.xml.network_security_config
            ).decodeBitmap()
        )
    }

    @Test
    fun testDecodeRegionBitmap() {
        val (context, sketch) = getTestContextAndNewSketch()

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .decodeRegionBitmap(Rect(500, 500, 600, 600))!!.apply {
                Assert.assertEquals(100, width)
                Assert.assertEquals(100, height)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.jpeg.uri),
            AssetImages.jpeg.fileName
        )
            .decodeRegionBitmap(
                Rect(500, 500, 600, 600),
                BitmapFactory.Options().apply { inSampleSize = 2 })!!
            .apply {
                Assert.assertEquals(50, width)
                Assert.assertEquals(50, height)
            }

        AssetDataSource(
            sketch,
            ImageRequest(context, AssetImages.webp.uri),
            AssetImages.webp.fileName
        )
            .decodeRegionBitmap(Rect(500, 500, 700, 700))!!.apply {
                Assert.assertEquals(200, width)
                Assert.assertEquals(200, height)
            }

        assertThrow(IOException::class) {
            ResourceDataSource(
                sketch,
                ImageRequest(
                    context,
                    newResourceUri(com.github.panpf.sketch.test.utils.R.xml.network_security_config)
                ),
                packageName = context.packageName,
                context.resources,
                com.github.panpf.sketch.test.utils.R.xml.network_security_config
            ).decodeRegionBitmap(Rect(500, 500, 600, 600))
        }
    }

    @Test
    fun testSupportBitmapRegionDecoder() {
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            Assert.assertTrue(ImageFormat.HEIC.supportBitmapRegionDecoder())
        } else {
            Assert.assertFalse(ImageFormat.HEIC.supportBitmapRegionDecoder())
        }
        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            Assert.assertTrue(ImageFormat.HEIF.supportBitmapRegionDecoder())
        } else {
            Assert.assertFalse(ImageFormat.HEIF.supportBitmapRegionDecoder())
        }
        Assert.assertFalse(ImageFormat.BMP.supportBitmapRegionDecoder())
        Assert.assertFalse(ImageFormat.GIF.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.JPEG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.PNG.supportBitmapRegionDecoder())
        Assert.assertTrue(ImageFormat.WEBP.supportBitmapRegionDecoder())
    }

    @Test
    fun testIsInBitmapError() {
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("Problem decoding into existing bitmap"))
        )
        Assert.assertTrue(
            isInBitmapError(IllegalArgumentException("bitmap"))
        )

        Assert.assertFalse(
            isInBitmapError(IllegalArgumentException("Problem decoding"))
        )
        Assert.assertFalse(
            isInBitmapError(IllegalStateException("Problem decoding into existing bitmap"))
        )
    }

    @Test
    fun testIsSrcRectError() {
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("rectangle is outside the image srcRect"))
        )
        Assert.assertTrue(
            isSrcRectError(IllegalArgumentException("srcRect"))
        )

        Assert.assertFalse(
            isSrcRectError(IllegalStateException("rectangle is outside the image srcRect"))
        )
        Assert.assertFalse(
            isSrcRectError(IllegalArgumentException(""))
        )
    }

    @Test
    fun testIsSupportInBitmap() {
        Assert.assertEquals(VERSION.SDK_INT >= 16, isSupportInBitmap("image/jpeg", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/jpeg", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 16, isSupportInBitmap("image/png", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/png", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/gif", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 21, isSupportInBitmap("image/gif", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/webp", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/webp", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/bmp", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 19, isSupportInBitmap("image/bmp", 2))

        Assert.assertEquals(false, isSupportInBitmap("image/heic", 1))
        Assert.assertEquals(false, isSupportInBitmap("image/heic", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 28, isSupportInBitmap("image/heif", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 28, isSupportInBitmap("image/heif", 2))

        Assert.assertEquals(VERSION.SDK_INT >= 32, isSupportInBitmap("image/svg", 1))
        Assert.assertEquals(VERSION.SDK_INT >= 32, isSupportInBitmap("image/svg", 2))
    }

    @Test
    fun testIsSupportInBitmapForRegion() {
        Assert.assertEquals(VERSION.SDK_INT >= 16, isSupportInBitmapForRegion("image/jpeg"))
        Assert.assertEquals(VERSION.SDK_INT >= 16, isSupportInBitmapForRegion("image/png"))
        Assert.assertEquals(false, isSupportInBitmapForRegion("image/gif"))
        Assert.assertEquals(VERSION.SDK_INT >= 16, isSupportInBitmapForRegion("image/webp"))
        Assert.assertEquals(false, isSupportInBitmapForRegion("image/bmp"))
        Assert.assertEquals(VERSION.SDK_INT >= 28, isSupportInBitmapForRegion("image/heic"))
        Assert.assertEquals(VERSION.SDK_INT >= 28, isSupportInBitmapForRegion("image/heif"))
        Assert.assertEquals(VERSION.SDK_INT >= 32, isSupportInBitmapForRegion("image/svg"))
    }
}