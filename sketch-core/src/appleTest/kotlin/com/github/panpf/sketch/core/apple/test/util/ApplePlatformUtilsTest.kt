package com.github.panpf.sketch.core.apple.test.util

import com.github.panpf.sketch.util.toByteArray
import com.github.panpf.sketch.util.toNSData
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class ApplePlatformUtilsTest {

    @Test
    fun testByteArrayToNSData() {
        val expected = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)
        val source = expected.copyOf()
        val nsData = source.toNSData()

        assertEquals(expected.size.toULong(), nsData.length)
        source[0] = 0
        assertContentEquals(expected, nsData.toByteArray())
    }

    @Test
    fun testNSDataToByteArray() {
        val expected = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)
        val nsData = expected.toNSData()

        val actual = nsData.toByteArray()
        assertContentEquals(expected, actual)
        assertNotSame(expected, actual)

        actual[0] = 0
        assertContentEquals(expected, nsData.toByteArray())
    }
}
