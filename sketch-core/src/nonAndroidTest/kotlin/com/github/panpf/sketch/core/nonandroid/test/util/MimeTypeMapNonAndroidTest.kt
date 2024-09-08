package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.util.extensionToMimeType
import com.github.panpf.sketch.util.mimeTypeToExtension
import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypeMapNonAndroidTest {

    @Test
    fun testExtensionToMimeType() {
        assertEquals(null, extensionToMimeType("jpg"))
        assertEquals(null, extensionToMimeType("png"))
        assertEquals(null, extensionToMimeType("webp"))
    }

    @Test
    fun testMimeTypeToExtension() {
        assertEquals(null, mimeTypeToExtension("image/jpeg"))
        assertEquals(null, mimeTypeToExtension("image/png"))
        assertEquals(null, mimeTypeToExtension("image/webp"))
    }
}