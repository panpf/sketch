package com.github.panpf.sketch.test.decode.resize

import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.decode.resize.Precision.EXACTLY
import com.github.panpf.sketch.decode.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.decode.resize.longImageClipPrecision
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageClipPrecisionDeciderTest {

    @Test
    fun testConstructor() {
        longImageClipPrecision(KEEP_ASPECT_RATIO)
        longImageClipPrecision(EXACTLY)
        assertThrow(IllegalArgumentException::class) {
            longImageClipPrecision(LESS_PIXELS)
        }
    }

    @Test
    fun testPrecision() {
        longImageClipPrecision(KEEP_ASPECT_RATIO).apply {
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision(100, 48, 50, 50))
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision(100, 49, 50, 50))
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision(100, 50, 50, 50))
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision(100, 51, 50, 50))
            Assert.assertEquals(LESS_PIXELS, precision(100, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, precision(100, 48, 50, 50))
            Assert.assertEquals(EXACTLY, precision(100, 49, 50, 50))
            Assert.assertEquals(EXACTLY, precision(100, 50, 50, 50))
            Assert.assertEquals(EXACTLY, precision(100, 51, 50, 50))
            Assert.assertEquals(LESS_PIXELS, precision(100, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY, minDifferenceOfAspectRatio = 3f).apply {
            Assert.assertEquals(EXACTLY, precision(100, 32, 50, 50))
            Assert.assertEquals(EXACTLY, precision(100, 33, 50, 50))
            Assert.assertEquals(LESS_PIXELS, precision(100, 34, 50, 50))
        }
    }

    @Test
    fun testIsLongImage() {
        longImageClipPrecision(KEEP_ASPECT_RATIO).apply {
            Assert.assertTrue(isLongImage(100, 48, 50, 50))
            Assert.assertTrue(isLongImage(100, 49, 50, 50))
            Assert.assertTrue(isLongImage(100, 50, 50, 50))
            Assert.assertTrue(isLongImage(100, 51, 50, 50))
            Assert.assertFalse(isLongImage(100, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY).apply {
            Assert.assertTrue(isLongImage(100, 48, 50, 50))
            Assert.assertTrue(isLongImage(100, 49, 50, 50))
            Assert.assertTrue(isLongImage(100, 50, 50, 50))
            Assert.assertTrue(isLongImage(100, 51, 50, 50))
            Assert.assertFalse(isLongImage(100, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY, minDifferenceOfAspectRatio = 3f).apply {
            Assert.assertTrue(isLongImage(100, 32, 50, 50))
            Assert.assertTrue(isLongImage(100, 33, 50, 50))
            Assert.assertFalse(isLongImage(100, 34, 50, 50))
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(KEEP_ASPECT_RATIO,2.0)",
            longImageClipPrecision(KEEP_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(EXACTLY,3.0)",
            longImageClipPrecision(EXACTLY, 3f).toString()
        )
    }
}