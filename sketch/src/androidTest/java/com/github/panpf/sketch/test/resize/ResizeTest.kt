package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.longImageClipPrecision
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeTest {

    @Test
    fun testConstructor() {
        Resize(100, 30).apply {
            Assert.assertEquals(100, width)
            Assert.assertEquals(30, height)
        }
        Resize(10, 20).apply {
            Assert.assertEquals(10, width)
            Assert.assertEquals(20, height)
        }

        Resize(100, 100).apply {
            Assert.assertEquals(CENTER_CROP, scale)
        }
        Resize(100, 100, scale = START_CROP).apply {
            Assert.assertEquals(START_CROP, scale)
        }
        Resize(100, 100, scale = CENTER_CROP).apply {
            Assert.assertEquals(CENTER_CROP, scale)
        }
        Resize(100, 100, scale = END_CROP).apply {
            Assert.assertEquals(END_CROP, scale)
        }
        Resize(100, 100, scale = FILL).apply {
            Assert.assertEquals(FILL, scale)
        }
    }

    @Test
    fun testCacheKey() {
        Resize(100, 100).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        Resize(100, 50).apply {
            Assert.assertEquals("Resize(100x50,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        Resize(50, 100).apply {
            Assert.assertEquals("Resize(50x100,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }

        Resize(100, 100, precision = KEEP_ASPECT_RATIO).apply {
            Assert.assertEquals("Resize(100x100,Fixed(KEEP_ASPECT_RATIO),CENTER_CROP)", key)
        }
        Resize(100, 100, precision = EXACTLY).apply {
            Assert.assertEquals("Resize(100x100,Fixed(EXACTLY),CENTER_CROP)", key)
        }
        Resize(100, 100, precision = LESS_PIXELS).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(
                "Resize(100x100,LongImageClip(EXACTLY,2.0),CENTER_CROP)",
                key
            )
        }

        Resize(100, 100, scale = START_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),START_CROP)", key)
        }
        Resize(100, 100, scale = CENTER_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        Resize(100, 100, scale = END_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),END_CROP)", key)
        }
        Resize(100, 100, scale = FILL).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),FILL)", key)
        }
    }

    @Test
    fun testShouldClip() {
        Resize(100, 100).apply {
            Assert.assertFalse(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertFalse(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertFalse(shouldClip(50, 50))
            Assert.assertTrue(shouldClip(150, 150))
        }

        Resize(100, 100, precision = KEEP_ASPECT_RATIO).apply {
            Assert.assertTrue(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertTrue(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertFalse(shouldClip(50, 50))
            Assert.assertFalse(shouldClip(150, 150))
        }

        Resize(100, 100, precision = EXACTLY).apply {
            Assert.assertTrue(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertTrue(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertTrue(shouldClip(50, 50))
            Assert.assertTrue(shouldClip(150, 150))
        }

        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertTrue(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertTrue(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertFalse(shouldClip(50, 50))
            Assert.assertTrue(shouldClip(150, 150))
        }
    }

    @Test
    fun testPrecision() {
        Resize(100, 30).apply {
            Assert.assertEquals(LESS_PIXELS, precision(0, 0))
        }
        Resize(100, 30, precision = LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, precision(0, 0))
        }
        Resize(100, 30, precision = KEEP_ASPECT_RATIO).apply {
            Assert.assertEquals(KEEP_ASPECT_RATIO, precision(0, 0))
        }
        Resize(100, 30, precision = EXACTLY).apply {
            Assert.assertEquals(EXACTLY, precision(0, 0))
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, precision(50, 50))
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, precision(40, 50))
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, precision(50, 40))
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(EXACTLY, precision(100, 50))
        }
        Resize(100, 100, precisionDecider = longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(EXACTLY, precision(50, 100))
        }
    }
}