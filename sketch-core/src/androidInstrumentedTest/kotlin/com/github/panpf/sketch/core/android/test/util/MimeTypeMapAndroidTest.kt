package com.github.panpf.sketch.core.android.test.util

import com.github.panpf.sketch.util.extensionToMimeType
import com.github.panpf.sketch.util.mimeTypeToExtension
import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypeMapAndroidTest {

    @Test
    fun testExtensionToMimeType() {
        assertEquals("image/jpeg", extensionToMimeType("jpg"))
        assertEquals("image/png", extensionToMimeType("png"))
        assertEquals("image/webp", extensionToMimeType("webp"))
    }

    @Test
    fun testMimeTypeToExtension() {
        assertEquals("jpg", mimeTypeToExtension("image/jpeg"))
        assertEquals("png", mimeTypeToExtension("image/png"))
        assertEquals("webp", mimeTypeToExtension("image/webp"))
    }
}