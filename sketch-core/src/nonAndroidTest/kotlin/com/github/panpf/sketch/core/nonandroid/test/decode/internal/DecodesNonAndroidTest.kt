package com.github.panpf.sketch.core.nonandroid.test.decode.internal

import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampleSizeForRegion
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.getMaxBitmapSize
import com.github.panpf.sketch.decode.internal.getMaxBitmapSizeOr
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DecodesNonAndroidTest {

    @Test
    fun testGetMaxBitmapSize() {
        assertNull(actual = getMaxBitmapSize())
    }

    @Test
    fun testGetMaxBitmapSizeOr() {
        assertEquals(
            expected = Size(30, 50),
            actual = getMaxBitmapSizeOr(Size(30, 50))
        )
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
            Size(503, 101),
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
            Size(503, 101),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(503, 101),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 201),
                sampleSize = 2,
                mimeType = "image/png",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(289, 101),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 201),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(503, 56),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(1005, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(289, 56),
            calculateSampledBitmapSizeForRegion(
                regionSize = Size(577, 111),
                sampleSize = 2,
                mimeType = "image/jpeg",
                imageSize = Size(1005, 201)
            )
        )
        assertEquals(
            Size(289, 56),
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
            expected = 4,
            actual = calculateSampleSize(
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
            expected = 4,
            actual = calculateSampleSize(
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
            maxSize <= 4096 -> 16
            maxSize <= 8192 -> 8
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
            4,
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
            8,
            calculateSampleSizeForRegion(
                regionSize = Size(1005, 201),
                targetSize = Size(250, 49),
                imageSize = Size(2005, 301),
                smallerSizeMode = false
            )
        )

        assertEquals(
            4,
            calculateSampleSizeForRegion(
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
            expected = 4,
            actual = calculateSampleSizeForRegion(
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
            expected = 8,
            actual = calculateSampleSizeForRegion(
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
            4,
            calculateSampleSizeForRegion(
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
            maxSize <= 4096 -> 16
            maxSize <= 8192 -> 8
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
            expected = 4,
            actual = calculateSampleSizeForRegion(
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
            expected = 8,
            actual = calculateSampleSizeForRegion(
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
            4,
            calculateSampleSizeForRegion(
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
    fun testAppliedResize() {
        // TODO test
    }

    @Test
    fun testDecode() {
        // TODO test
    }

    @Test
    fun testDecodeRegion() {
        // TODO test
    }

    @Test
    fun testReadImageInfo() {
        // TODO test
    }
}