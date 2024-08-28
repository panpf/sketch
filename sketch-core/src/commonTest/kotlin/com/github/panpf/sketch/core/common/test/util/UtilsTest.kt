package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.test.utils.pow
import com.github.panpf.sketch.util.formatFileSize
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun testFormatFileSize() {
        assertEquals("0B", (0L-1).formatFileSize())
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
}