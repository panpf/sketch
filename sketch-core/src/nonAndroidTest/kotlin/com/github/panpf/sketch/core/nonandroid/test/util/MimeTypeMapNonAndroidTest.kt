package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.util.platformExtensionToMimeType
import com.github.panpf.sketch.util.platformMimeTypeToExtension
import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypeMapNonAndroidTest {

    @Test
    fun testPlatformExtensionToMimeType() {
        assertEquals(null, platformExtensionToMimeType("jpg"))
        assertEquals(null, platformExtensionToMimeType("png"))
        assertEquals(null, platformExtensionToMimeType("webp"))
    }

    @Test
    fun testPlatformMimeTypeToExtension() {
        assertEquals(null, platformMimeTypeToExtension("image/jpeg"))
        assertEquals(null, platformMimeTypeToExtension("image/png"))
        assertEquals(null, platformMimeTypeToExtension("image/webp"))
    }
}