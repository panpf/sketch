package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.mimeTypeToImageFormat
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
        Assert.assertEquals(ImageFormat.JPEG, mimeTypeToImageFormat("image/jpeg"))
        Assert.assertEquals(ImageFormat.JPEG, mimeTypeToImageFormat("IMAGE/JPEG"))
        Assert.assertEquals(ImageFormat.PNG, mimeTypeToImageFormat("image/png"))
        Assert.assertEquals(ImageFormat.PNG, mimeTypeToImageFormat("IMAGE/PNG"))
        Assert.assertEquals(ImageFormat.WEBP, mimeTypeToImageFormat("image/webp"))
        Assert.assertEquals(ImageFormat.WEBP, mimeTypeToImageFormat("IMAGE/WEBP"))
        Assert.assertEquals(ImageFormat.GIF, mimeTypeToImageFormat("image/gif"))
        Assert.assertEquals(ImageFormat.GIF, mimeTypeToImageFormat("IMAGE/GIF"))
        Assert.assertEquals(ImageFormat.BMP, mimeTypeToImageFormat("image/bmp"))
        Assert.assertEquals(ImageFormat.BMP, mimeTypeToImageFormat("IMAGE/BMP"))
        Assert.assertEquals(ImageFormat.HEIC, mimeTypeToImageFormat("image/heic"))
        Assert.assertEquals(ImageFormat.HEIC, mimeTypeToImageFormat("IMAGE/HEIC"))
        Assert.assertEquals(ImageFormat.HEIF, mimeTypeToImageFormat("image/heif"))
        Assert.assertEquals(ImageFormat.HEIF, mimeTypeToImageFormat("IMAGE/HEIF"))
        Assert.assertNull(mimeTypeToImageFormat("image/jpeg1"))
        Assert.assertNull(mimeTypeToImageFormat("IMAGE/JPEG1"))
    }
}