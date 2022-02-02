package com.github.panpf.sketch.test.decode

import androidx.exifinterface.media.ExifInterface
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.ImageInfo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageInfoTest {

    @Test
    fun testConstructor() {
        ImageInfo(57, 34, "image/jpeg", 0).apply {
            Assert.assertEquals(57, width)
            Assert.assertEquals(34, height)
            Assert.assertEquals("image/jpeg", mimeType)
            Assert.assertEquals(0, exifOrientation)
        }

        ImageInfo(570, 340, "image/png", ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(570, width)
            Assert.assertEquals(340, height)
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_180, exifOrientation)
        }
    }

    @Test
    fun testToString() {
        ImageInfo(57, 34, "image/jpeg", 0).apply {
            Assert.assertEquals(
                "ImageInfo(width=57,height=34,mimeType='image/jpeg',exifOrientation=UNDEFINED)",
                toString()
            )
        }

        ImageInfo(570, 340, "image/png", ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(
                "ImageInfo(width=570,height=340,mimeType='image/png',exifOrientation=ROTATE_180)",
                toString()
            )
        }
    }

    @Test
    fun testJSON() {
        val imageInfo = ImageInfo(570, 340, "image/png", ExifInterface.ORIENTATION_ROTATE_180)
        Assert.assertEquals(
            "{\"mimeType\":\"image\\/png\",\"width\":570,\"height\":340,\"exifOrientation\":3}",
            imageInfo.toJsonString()
        )

        ImageInfo.fromJsonString(imageInfo.toJsonString()).apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals(570, width)
            Assert.assertEquals(340, height)
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_180, exifOrientation)
        }
    }
}