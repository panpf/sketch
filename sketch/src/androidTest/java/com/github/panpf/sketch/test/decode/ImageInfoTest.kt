package com.github.panpf.sketch.test.decode

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.ImageInfo
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
}