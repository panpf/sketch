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
        ImageInfo("image/jpeg", 57, 34, 0).apply {
            Assert.assertEquals("image/jpeg", mimeType)
            Assert.assertEquals(57, width)
            Assert.assertEquals(34, height)
            Assert.assertEquals(0, exifOrientation)
        }

        ImageInfo("image/png", 570, 340, ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals(570, width)
            Assert.assertEquals(340, height)
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_180, exifOrientation)
        }
    }

    @Test
    fun testToString() {
        ImageInfo("image/jpeg", 57, 34, 0).apply {
            Assert.assertEquals(
                "ImageInfo(mimeType='image/jpeg',width=57,height=34,exifOrientation=UNDEFINED)",
                toString()
            )
        }

        ImageInfo("image/png", 570, 340, ExifInterface.ORIENTATION_ROTATE_180).apply {
            Assert.assertEquals(
                "ImageInfo(mimeType='image/png',width=570,height=340,exifOrientation=ROTATE_180)",
                toString()
            )
        }
    }

    @Test
    fun testJSON() {
        val imageInfo = ImageInfo("image/png", 570, 340, ExifInterface.ORIENTATION_ROTATE_180)
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