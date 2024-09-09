package com.github.panpf.sketch.core.android.test.util

import com.github.panpf.sketch.util.platformExtensionToMimeType
import com.github.panpf.sketch.util.platformMimeTypeToExtension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MimeTypeMapAndroidTest {

    @Test
    fun testPlatformExtensionToMimeType() {
        assertEquals("image/jpeg", platformExtensionToMimeType("jpg"))
        assertEquals("image/png", platformExtensionToMimeType("png"))
        assertEquals("image/webp", platformExtensionToMimeType("webp"))
    }

    @Test
    fun testPlatformMimeTypeToExtension() {
        assertTrue(
            platformMimeTypeToExtension("image/jpeg") == "jpeg"
                    || platformMimeTypeToExtension("image/jpeg") == "jpg"
        )
        assertEquals("png", platformMimeTypeToExtension("image/png"))
        assertEquals("webp", platformMimeTypeToExtension("image/webp"))
    }
}