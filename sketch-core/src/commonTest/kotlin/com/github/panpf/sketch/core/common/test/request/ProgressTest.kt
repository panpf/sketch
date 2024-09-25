package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.request.Progress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProgressTest {

    @Test
    fun testConstructor() {
        Progress(totalLength = 100L, completedLength = 50L)
        Progress(100L, 50L).apply {
            assertEquals(expected = 100L, actual = totalLength)
            assertEquals(expected = 50L, actual = completedLength)
        }
    }

    @Test
    fun testDecimalProgress() {
        assertEquals(
            expected = 0.5f,
            actual = Progress(totalLength = 100L, completedLength = 50L).decimalProgress
        )
        assertEquals(
            expected = 0f,
            actual = Progress(totalLength = 0L, completedLength = 50L).decimalProgress
        )
        assertEquals(
            expected = 0f,
            actual = Progress(totalLength = 100L, completedLength = 0L).decimalProgress
        )
        assertEquals(
            expected = 1f,
            actual = Progress(totalLength = 100L, completedLength = 100L).decimalProgress
        )
        assertEquals(
            expected = 1.5f,
            actual = Progress(totalLength = 100L, completedLength = 150L).decimalProgress
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = Progress(totalLength = 100L, completedLength = 50L)
        val element11 = element1.copy()
        val element2 = element1.copy(totalLength = 200L)
        val element3 = element1.copy(completedLength = 60L)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
    }

    @Test
    fun testToString() {
        val element1 = Progress(totalLength = 100L, completedLength = 50L)
        assertEquals(
            expected = "Progress(totalLength=100, completedLength=50)",
            actual = element1.toString()
        )
    }
}