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

package com.github.panpf.sketch.core.common.test.decode

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class ImageInfoTest {

    @Test
    fun testConstructor() {
        ImageInfo(57, 34, "image/jpeg").apply {
            assertEquals(57, width)
            assertEquals(34, height)
            assertEquals("image/jpeg", mimeType)
        }

        ImageInfo(570, 340, "image/png").apply {
            assertEquals(570, width)
            assertEquals(340, height)
            assertEquals("image/png", mimeType)
        }
    }

    @Test
    fun testNewResult() {
        val imageInfo = ImageInfo(300, 500, "image/jpeg").apply {
            assertEquals(300, width)
            assertEquals(500, height)
            assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy().apply {
            assertNotSame(imageInfo, this)
            assertEquals(imageInfo, this)
            assertEquals(300, width)
            assertEquals(500, height)
            assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(Size(200, 500)).apply {
            assertNotSame(imageInfo, this)
            assertNotEquals(imageInfo, this)
            assertEquals(200, width)
            assertEquals(500, height)
            assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(Size(300, 400)).apply {
            assertNotSame(imageInfo, this)
            assertNotEquals(imageInfo, this)
            assertEquals(300, width)
            assertEquals(400, height)
            assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(mimeType = "image/png").apply {
            assertNotSame(imageInfo, this)
            assertNotEquals(imageInfo, this)
            assertEquals(300, width)
            assertEquals(500, height)
            assertEquals("image/png", mimeType)
        }
    }

    @Test
    fun testToString() {
        ImageInfo(57, 34, "image/jpeg").apply {
            assertEquals(
                "ImageInfo(size=57x34, mimeType='image/jpeg')",
                toString()
            )
        }

        ImageInfo(570, 340, "image/png").apply {
            assertEquals(
                "ImageInfo(size=570x340, mimeType='image/png')",
                toString()
            )
        }
    }

    @Test
    fun testToShortString() {
        ImageInfo(57, 34, "image/jpeg").apply {
            assertEquals(
                "ImageInfo(57x34,'image/jpeg')",
                toShortString()
            )
        }

        ImageInfo(570, 340, "image/png").apply {
            assertEquals(
                "ImageInfo(570x340,'image/png')",
                toShortString()
            )
        }
    }
}