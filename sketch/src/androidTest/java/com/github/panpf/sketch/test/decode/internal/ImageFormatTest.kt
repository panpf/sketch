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
package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ImageFormat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageFormatTest {

    @Test
    fun testMimeType() {
        Assert.assertEquals("image/jpeg", ImageFormat.JPEG.mimeType)
        Assert.assertEquals("image/png", ImageFormat.PNG.mimeType)
        Assert.assertEquals("image/webp", ImageFormat.WEBP.mimeType)
        Assert.assertEquals("image/gif", ImageFormat.GIF.mimeType)
        Assert.assertEquals("image/bmp", ImageFormat.BMP.mimeType)
        Assert.assertEquals("image/heic", ImageFormat.HEIC.mimeType)
        Assert.assertEquals("image/heif", ImageFormat.HEIF.mimeType)
    }

    @Test
    fun testMimeTypeToImageFormat() {
        Assert.assertEquals(ImageFormat.JPEG, ImageFormat.parseMimeType("image/jpeg"))
        Assert.assertEquals(ImageFormat.JPEG, ImageFormat.parseMimeType("IMAGE/JPEG"))
        Assert.assertEquals(ImageFormat.PNG, ImageFormat.parseMimeType("image/png"))
        Assert.assertEquals(ImageFormat.PNG, ImageFormat.parseMimeType("IMAGE/PNG"))
        Assert.assertEquals(ImageFormat.WEBP, ImageFormat.parseMimeType("image/webp"))
        Assert.assertEquals(ImageFormat.WEBP, ImageFormat.parseMimeType("IMAGE/WEBP"))
        Assert.assertEquals(ImageFormat.GIF, ImageFormat.parseMimeType("image/gif"))
        Assert.assertEquals(ImageFormat.GIF, ImageFormat.parseMimeType("IMAGE/GIF"))
        Assert.assertEquals(ImageFormat.BMP, ImageFormat.parseMimeType("image/bmp"))
        Assert.assertEquals(ImageFormat.BMP, ImageFormat.parseMimeType("IMAGE/BMP"))
        Assert.assertEquals(ImageFormat.HEIC, ImageFormat.parseMimeType("image/heic"))
        Assert.assertEquals(ImageFormat.HEIC, ImageFormat.parseMimeType("IMAGE/HEIC"))
        Assert.assertEquals(ImageFormat.HEIF, ImageFormat.parseMimeType("image/heif"))
        Assert.assertEquals(ImageFormat.HEIF, ImageFormat.parseMimeType("IMAGE/HEIF"))
        Assert.assertNull(ImageFormat.parseMimeType("image/jpeg1"))
        Assert.assertNull(ImageFormat.parseMimeType("IMAGE/JPEG1"))
    }
}