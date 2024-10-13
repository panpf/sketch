/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.core.common.test.decode.internal

import com.github.panpf.sketch.decode.internal.ImageFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ImageFormatTest {

    @Test
    fun testMimeType() {
        assertEquals("image/jpeg", ImageFormat.JPEG.mimeType)
        assertEquals("image/png", ImageFormat.PNG.mimeType)
        assertEquals("image/webp", ImageFormat.WEBP.mimeType)
        assertEquals("image/gif", ImageFormat.GIF.mimeType)
        assertEquals("image/bmp", ImageFormat.BMP.mimeType)
        assertEquals("image/heic", ImageFormat.HEIC.mimeType)
        assertEquals("image/heif", ImageFormat.HEIF.mimeType)
        assertEquals("image/avif", ImageFormat.AVIF.mimeType)
        assertEquals("image/svg+xml", ImageFormat.SVG.mimeType)
    }

    @Test
    fun testMimeTypeToImageFormat() {
        assertEquals(ImageFormat.JPEG, ImageFormat.parseMimeType("image/jpeg"))
        assertEquals(ImageFormat.JPEG, ImageFormat.parseMimeType("IMAGE/JPEG"))
        assertEquals(ImageFormat.PNG, ImageFormat.parseMimeType("image/png"))
        assertEquals(ImageFormat.PNG, ImageFormat.parseMimeType("IMAGE/PNG"))
        assertEquals(ImageFormat.WEBP, ImageFormat.parseMimeType("image/webp"))
        assertEquals(ImageFormat.WEBP, ImageFormat.parseMimeType("IMAGE/WEBP"))
        assertEquals(ImageFormat.GIF, ImageFormat.parseMimeType("image/gif"))
        assertEquals(ImageFormat.GIF, ImageFormat.parseMimeType("IMAGE/GIF"))
        assertEquals(ImageFormat.BMP, ImageFormat.parseMimeType("image/bmp"))
        assertEquals(ImageFormat.BMP, ImageFormat.parseMimeType("IMAGE/BMP"))
        assertEquals(ImageFormat.HEIC, ImageFormat.parseMimeType("image/heic"))
        assertEquals(ImageFormat.HEIC, ImageFormat.parseMimeType("IMAGE/HEIC"))
        assertEquals(ImageFormat.HEIF, ImageFormat.parseMimeType("image/heif"))
        assertEquals(ImageFormat.HEIF, ImageFormat.parseMimeType("IMAGE/HEIF"))
        assertEquals(ImageFormat.AVIF, ImageFormat.parseMimeType("image/avif"))
        assertEquals(ImageFormat.AVIF, ImageFormat.parseMimeType("IMAGE/AVIF"))
        assertEquals(ImageFormat.SVG, ImageFormat.parseMimeType("image/svg+xml"))
        assertEquals(ImageFormat.SVG, ImageFormat.parseMimeType("image/SVG+XML"))
        assertNull(ImageFormat.parseMimeType("image/jpeg1"))
        assertNull(ImageFormat.parseMimeType("IMAGE/JPEG1"))
    }

    @Test
    fun testValues() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "JPEG, PNG, WEBP, GIF, BMP, HEIC, HEIF, AVIF, SVG",
            actual = ImageFormat.values().joinToString()
        )
    }
}