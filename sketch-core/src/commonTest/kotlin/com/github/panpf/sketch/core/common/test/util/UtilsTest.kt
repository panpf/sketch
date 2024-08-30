package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.test.utils.pow
import com.github.panpf.sketch.util.computeScaleMultiplierWithFit
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.intSplit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UtilsTest {

    @Test
    fun testIfOrNull() {
        assertEquals("yes", ifOrNull(true) { "yes" })
        assertEquals(null, ifOrNull(false) { "yes" })
    }

    @Test
    fun testIfApply() {
        // TODO test
    }

    @Test
    fun testIfLet() {
        // TODO test
    }

    @Test
    fun testAsOrNull() {
        // TODO test
    }

    @Test
    fun testAsOrThrow() {
        // TODO test
    }

    @Test
    fun testGetCompletedOrNull() {
        // TODO test
    }

    @Test
    fun testMd5() {
        // TODO test
    }

    @Test
    fun testToHexString() {
        // TODO test
    }

    @Test
    fun testFloatFormat() {
        listOf(
            FormatItem(number = 6.2517f, newScale = 3, expected = 6.252f),
            FormatItem(number = 6.2517f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.2517f, newScale = 1, expected = 6.3f),
            FormatItem(number = 6.251f, newScale = 2, expected = 6.25f),
            FormatItem(number = 6.251f, newScale = 1, expected = 6.3f),

            FormatItem(number = 0.6253f, newScale = 3, expected = 0.625f),
            FormatItem(number = 0.6253f, newScale = 2, expected = 0.63f),
            FormatItem(number = 0.6253f, newScale = 1, expected = 0.6f),
            FormatItem(number = 0.625f, newScale = 2, expected = 0.62f),
            FormatItem(number = 0.625f, newScale = 1, expected = 0.6f),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0f,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    @Test
    fun testDoubleFormat() {
        listOf(
            FormatItem(number = 6.2517, newScale = 3, expected = 6.252),
            FormatItem(number = 6.2517, newScale = 2, expected = 6.25),
            FormatItem(number = 6.2517, newScale = 1, expected = 6.3),
            FormatItem(number = 6.251, newScale = 2, expected = 6.25),
            FormatItem(number = 6.251, newScale = 1, expected = 6.3),

            FormatItem(number = 0.6253, newScale = 3, expected = 0.625),
            FormatItem(number = 0.6253, newScale = 2, expected = 0.63),
            FormatItem(number = 0.6253, newScale = 1, expected = 0.6),
            FormatItem(number = 0.625, newScale = 2, expected = 0.62),
            FormatItem(number = 0.625, newScale = 1, expected = 0.6),
        ).forEach {
            assertEquals(
                expected = it.expected,
                actual = it.number.format(it.newScale),
                absoluteTolerance = 0.0,
                message = "format. number=${it.number}, newScale=${it.newScale}"
            )
        }
    }

    @Test
    fun testFormatFileSize() {
        assertEquals("0B", (0L - 1).formatFileSize())
        assertEquals("0B", 0L.formatFileSize(2))
        assertEquals("999B", 999L.formatFileSize())
        assertEquals("0.98KB", (999L + 1).formatFileSize(2))

        assertEquals("1KB", 1024L.pow(1).formatFileSize())
        assertEquals("999KB", (1024L.pow(1) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98MB", (1024L.pow(1) * 1000).formatFileSize(2))

        assertEquals("1MB", 1024L.pow(2).formatFileSize())
        assertEquals("999MB", (1024L.pow(2) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98GB", (1024L.pow(2) * 1000).formatFileSize(2))

        assertEquals("1GB", 1024L.pow(3).formatFileSize())
        assertEquals("999GB", (1024L.pow(3) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98TB", (1024L.pow(3) * 1000).formatFileSize(2))

        assertEquals("1TB", 1024L.pow(4).formatFileSize())
        assertEquals("999TB", (1024L.pow(4) * (1000 - 1)).formatFileSize(2))
        assertEquals("0.98PB", (1024L.pow(4) * 1000).formatFileSize(2))

        assertEquals("1PB", 1024L.pow(5).formatFileSize())
        assertEquals("999PB", (1024L.pow(5) * (1000 - 1)).formatFileSize(2))
        assertEquals("1000PB", (1024L.pow(5) * 1000).formatFileSize(2))

        assertEquals("1024PB", 1024L.pow(6).formatFileSize())
    }

    @Test
    fun testIntMergedAndIntSplit() {
        intSplit(intMerged(39, 25)).apply {
            assertEquals(39, first)
            assertEquals(25, second)
        }
        intSplit(intMerged(7, 43)).apply {
            assertEquals(7, first)
            assertEquals(43, second)
        }

        assertFailsWith(IllegalArgumentException::class) {
            intMerged(-1, 25)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(Short.MAX_VALUE + 1, 25)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(25, -1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            intMerged(25, Short.MAX_VALUE + 1)
        }
    }

    @Test
    fun testFloorRoundPow2() {
        // TODO test
    }

    @Test
    fun testCeilRoundPow2() {
        // TODO test
    }

    @Test
    fun testComputeScaleMultiplierWithFit() {
        assertEquals(0.2, computeScaleMultiplierWithFit(1000, 600, 200, 400, true), 0.1)
        assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 200, 400, false), 0.1)
        assertEquals(0.3, computeScaleMultiplierWithFit(1000, 600, 400, 200, true), 0.1)
        assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 200, false), 0.1)

        assertEquals(0.6, computeScaleMultiplierWithFit(1000, 600, 2000, 400, true), 0.1)
        assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 400, false), 0.1)
        assertEquals(0.4, computeScaleMultiplierWithFit(1000, 600, 400, 2000, true), 0.1)
        assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 400, 2000, false), 0.1)

        assertEquals(2.0, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, true), 0.1)
        assertEquals(6.6, computeScaleMultiplierWithFit(1000, 600, 2000, 4000, false), 0.1)
        assertEquals(3.3, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, true), 0.1)
        assertEquals(4.0, computeScaleMultiplierWithFit(1000, 600, 4000, 2000, false), 0.1)
    }

    @Test
    fun testComputeScaleMultiplierWithOneSide() {
        // TODO test
    }

    @Test
    fun testImageRequestDifference() {
        // TODO test
    }

    @Test
    fun testImageOptionsDifference() {
        // TODO test
    }

    private class FormatItem<T>(val number: T, val newScale: Int, val expected: T)
}