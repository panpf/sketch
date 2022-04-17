package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.longImageClipPrecision
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
            Assert.assertEquals(KEEP_ASPECT_RATIO, get(150, 48, 50, 50))
            Assert.assertEquals(KEEP_ASPECT_RATIO, get(150, 49, 50, 50))
            Assert.assertEquals(KEEP_ASPECT_RATIO, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 51, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(150, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 51, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY, minDifferenceOfAspectRatio = 4f).apply {
            Assert.assertEquals(EXACTLY, get(100, 24, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 25, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(100, 26, 50, 50))
        }
    }

    @Test
    fun testIsLongImage() {
        longImageClipPrecision(KEEP_ASPECT_RATIO).apply {
            Assert.assertTrue(isLongImage(150, 48, 50, 50))
            Assert.assertTrue(isLongImage(150, 49, 50, 50))
            Assert.assertTrue(isLongImage(150, 50, 50, 50))
            Assert.assertFalse(isLongImage(150, 51, 50, 50))
            Assert.assertFalse(isLongImage(150, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY).apply {
            Assert.assertTrue(isLongImage(150, 48, 50, 50))
            Assert.assertTrue(isLongImage(150, 49, 50, 50))
            Assert.assertTrue(isLongImage(150, 50, 50, 50))
            Assert.assertFalse(isLongImage(150, 51, 50, 50))
            Assert.assertFalse(isLongImage(150, 52, 50, 50))
        }

        longImageClipPrecision(EXACTLY, minDifferenceOfAspectRatio = 4f).apply {
            Assert.assertTrue(isLongImage(100, 24, 50, 50))
            Assert.assertTrue(isLongImage(100, 25, 50, 50))
            Assert.assertFalse(isLongImage(100, 26, 50, 50))
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(KEEP_ASPECT_RATIO,3.0)",
            longImageClipPrecision(KEEP_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(EXACTLY,4.0)",
            longImageClipPrecision(EXACTLY, 4f).toString()
        )
    }
}