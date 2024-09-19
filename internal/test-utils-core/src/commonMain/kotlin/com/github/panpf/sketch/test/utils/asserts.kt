package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.Size
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun assertSizeEquals(expected: Size, actual: Size, delta: Size? = null, message: String? = null) {
    if (delta != null) {
        assertTrue(
            actual = expected.width == actual.width
                    || expected.width == actual.width + delta.width
                    || expected.width == actual.width - delta.width,
            message = "Expected <$expected>, actual <$actual>.${message?.let { " $it" } ?: ""}"
        )
        assertTrue(
            actual = expected.height == actual.height
                    || expected.height == actual.height + delta.height
                    || expected.height == actual.height - delta.height,
            message = "Expected <$expected>, actual <$actual>.${message?.let { " $it" } ?: ""}"
        )
    } else {
        assertEquals(expected, actual, message)
    }
}

operator fun Size.plus(size: Size): Size {
    return Size(this.width + size.width, this.height + size.height)
}

operator fun Size.minus(size: Size): Size {
    return Size(this.width - size.width, this.height - size.height)
}