package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.MimeTypeMap
import kotlin.test.Test
import kotlin.test.assertEquals

class MimeTypeMapTest {

    @Test
    fun testGetExtensionFromUrl() {
        assertEquals(
            expected = "jpg",
            actual = MimeTypeMap.getExtensionFromUrl("http://example.com/image.jpg")
        )
        assertEquals(
            expected = "png",
            actual = MimeTypeMap.getExtensionFromUrl("http://example.com/image.png?query=123")
        )
        assertEquals(
            expected = "webp",
            actual = MimeTypeMap.getExtensionFromUrl("http://example.com/image.webp#fragment")
        )
        assertEquals(
            expected = "jpeg",
            actual = MimeTypeMap.getExtensionFromUrl("http://example.com/path/to/image.jpeg")
        )
        assertEquals(
            expected = null,
            actual = MimeTypeMap.getExtensionFromUrl("http://example.com/image")
        )
        assertEquals(
            expected = null, actual = MimeTypeMap.getExtensionFromUrl("")
        )
    }

    @Test
    fun testGetMimeTypeFromUrl() {
        assertEquals(
            expected = "image/jpeg",
            actual = MimeTypeMap.getMimeTypeFromUrl("http://example.com/image.jpg")
        )
        assertEquals(
            expected = "image/png",
            actual = MimeTypeMap.getMimeTypeFromUrl("http://example.com/image.png?query=123")
        )
        assertEquals(
            expected = "image/webp",
            actual = MimeTypeMap.getMimeTypeFromUrl("http://example.com/image.webp#fragment")
        )
        assertEquals(
            expected = "image/jpeg",
            actual = MimeTypeMap.getMimeTypeFromUrl("http://example.com/path/to/image.jpeg")
        )
        assertEquals(
            expected = null,
            actual = MimeTypeMap.getMimeTypeFromUrl("http://example.com/image")
        )
        assertEquals(
            expected = null,
            actual = MimeTypeMap.getMimeTypeFromUrl("")
        )
    }

    @Test
    fun testGetMimeTypeFromExtension() {
        assertEquals(expected = "image/jpeg", actual = MimeTypeMap.getMimeTypeFromExtension("jpg"))
        assertEquals(expected = "image/png", actual = MimeTypeMap.getMimeTypeFromExtension("png"))
        assertEquals(expected = "image/webp", actual = MimeTypeMap.getMimeTypeFromExtension("webp"))
        assertEquals(expected = "image/jpeg", actual = MimeTypeMap.getMimeTypeFromExtension("jpeg"))
        assertEquals(expected = null, actual = MimeTypeMap.getMimeTypeFromExtension("unknown"))
        assertEquals(expected = null, actual = MimeTypeMap.getMimeTypeFromExtension(""))
    }

    @Test
    fun testGetExtensionFromMimeType() {
        assertEquals(expected = "jpeg", actual = MimeTypeMap.getExtensionFromMimeType("image/jpeg"))
        assertEquals(expected = "png", actual = MimeTypeMap.getExtensionFromMimeType("image/png"))
        assertEquals(expected = "webp", actual = MimeTypeMap.getExtensionFromMimeType("image/webp"))
        assertEquals(expected = "jpeg", actual = MimeTypeMap.getExtensionFromMimeType("image/jpeg"))
        assertEquals(expected = null, actual = MimeTypeMap.getExtensionFromMimeType("unknown/mime"))
        assertEquals(expected = null, actual = MimeTypeMap.getExtensionFromMimeType(""))
    }
}