/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.core.android.test.decode

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageInfoTest {

    @Test
    fun testConstructor() {
        ImageInfo(57, 34, "image/jpeg").apply {
            Assert.assertEquals(57, width)
            Assert.assertEquals(34, height)
            Assert.assertEquals("image/jpeg", mimeType)
        }

        ImageInfo(570, 340, "image/png").apply {
            Assert.assertEquals(570, width)
            Assert.assertEquals(340, height)
            Assert.assertEquals("image/png", mimeType)
        }
    }

    @Test
    fun testNewResult() {
        val imageInfo = ImageInfo(300, 500, "image/jpeg").apply {
            Assert.assertEquals(300, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy().apply {
            Assert.assertNotSame(imageInfo, this)
            Assert.assertEquals(imageInfo, this)
            Assert.assertEquals(300, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(Size(200, 500)).apply {
            Assert.assertNotSame(imageInfo, this)
            Assert.assertNotEquals(imageInfo, this)
            Assert.assertEquals(200, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(Size(300, 400)).apply {
            Assert.assertNotSame(imageInfo, this)
            Assert.assertNotEquals(imageInfo, this)
            Assert.assertEquals(300, width)
            Assert.assertEquals(400, height)
            Assert.assertEquals("image/jpeg", mimeType)
        }

        imageInfo.copy(mimeType = "image/png").apply {
            Assert.assertNotSame(imageInfo, this)
            Assert.assertNotEquals(imageInfo, this)
            Assert.assertEquals(300, width)
            Assert.assertEquals(500, height)
            Assert.assertEquals("image/png", mimeType)
        }
    }

    @Test
    fun testToString() {
        ImageInfo(57, 34, "image/jpeg").apply {
            Assert.assertEquals(
                "ImageInfo(width=57, height=34, mimeType='image/jpeg')",
                toString()
            )
        }

        ImageInfo(570, 340, "image/png").apply {
            Assert.assertEquals(
                "ImageInfo(width=570, height=340, mimeType='image/png')",
                toString()
            )
        }
    }

    @Test
    fun testToShortString() {
        ImageInfo(57, 34, "image/jpeg").apply {
            Assert.assertEquals(
                "ImageInfo(57x34,'image/jpeg')",
                toShortString()
            )
        }

        ImageInfo(570, 340, "image/png").apply {
            Assert.assertEquals(
                "ImageInfo(570x340,'image/png',ROTATE_90)",
                toShortString()
            )
        }
    }
}