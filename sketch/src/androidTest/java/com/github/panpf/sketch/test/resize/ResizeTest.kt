package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.resize.longImageScale
import com.github.panpf.sketch.test.contextAndSketch
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
    }

    @Test
    fun testCacheKey() {
        Resize(100, 100).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))", key)
        }
        Resize(100, 50).apply {
            Assert.assertEquals("Resize(100x50,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))", key)
        }
        Resize(50, 100).apply {
            Assert.assertEquals("Resize(50x100,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))", key)
        }

        Resize(100, 100, SAME_ASPECT_RATIO).apply {
            Assert.assertEquals("Resize(100x100,Fixed(SAME_ASPECT_RATIO),Fixed(CENTER_CROP))", key)
        }
        Resize(100, 100, EXACTLY).apply {
            Assert.assertEquals("Resize(100x100,Fixed(EXACTLY),Fixed(CENTER_CROP))", key)
        }
        Resize(100, 100, LESS_PIXELS).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))", key)
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(
                "Resize(100x100,LongImageClip(EXACTLY),Fixed(CENTER_CROP))",
                key
            )
        }

        Resize(100, 100, scale = START_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(START_CROP))", key)
        }
        Resize(100, 100, scale = CENTER_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))", key)
        }
        Resize(100, 100, scale = END_CROP).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(END_CROP))", key)
        }
        Resize(100, 100, scale = FILL).apply {
            Assert.assertEquals("Resize(100x100,Fixed(LESS_PIXELS),Fixed(FILL))", key)
        }
    }

    @Test
    fun testShouldClip() {
        val (context, sketch) = contextAndSketch()
        Resize(100, 100).apply {
            Assert.assertFalse(shouldClip(sketch, 100, 50))
            Assert.assertTrue(shouldClip(sketch, 100, 150))
            Assert.assertFalse(shouldClip(sketch, 50, 100))
            Assert.assertTrue(shouldClip(sketch, 150, 100))
            Assert.assertFalse(shouldClip(sketch, 100, 100))
            Assert.assertFalse(shouldClip(sketch, 50, 50))
            Assert.assertTrue(shouldClip(sketch, 150, 150))
        }

        Resize(100, 100, SAME_ASPECT_RATIO).apply {
            Assert.assertTrue(shouldClip(sketch, 100, 50))
            Assert.assertTrue(shouldClip(sketch, 100, 150))
            Assert.assertTrue(shouldClip(sketch, 50, 100))
            Assert.assertTrue(shouldClip(sketch, 150, 100))
            Assert.assertFalse(shouldClip(sketch, 100, 100))
            Assert.assertFalse(shouldClip(sketch, 50, 50))
            Assert.assertFalse(shouldClip(sketch, 150, 150))
        }

        Resize(100, 100, EXACTLY).apply {
            Assert.assertTrue(shouldClip(sketch, 100, 50))
            Assert.assertTrue(shouldClip(sketch, 100, 150))
            Assert.assertTrue(shouldClip(sketch, 50, 100))
            Assert.assertTrue(shouldClip(sketch, 150, 100))
            Assert.assertFalse(shouldClip(sketch, 100, 100))
            Assert.assertTrue(shouldClip(sketch, 50, 50))
            Assert.assertTrue(shouldClip(sketch, 150, 150))
        }

        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertTrue(shouldClip(sketch, 150, 50))
            Assert.assertTrue(shouldClip(sketch, 100, 250))
            Assert.assertTrue(shouldClip(sketch, 50, 150))
            Assert.assertTrue(shouldClip(sketch, 250, 100))
            Assert.assertFalse(shouldClip(sketch, 100, 100))
            Assert.assertFalse(shouldClip(sketch, 50, 50))
            Assert.assertTrue(shouldClip(sketch, 150, 150))
        }
    }

    @Test
    fun testPrecision() {
        val (context, sketch) = contextAndSketch()
        Resize(100, 30).apply {
            Assert.assertEquals(LESS_PIXELS, getPrecision(sketch, 0, 0))
        }
        Resize(100, 30, LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, getPrecision(sketch, 0, 0))
        }
        Resize(100, 30, SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, getPrecision(sketch, 0, 0))
        }
        Resize(100, 30, EXACTLY).apply {
            Assert.assertEquals(EXACTLY, getPrecision(sketch, 0, 0))
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, getPrecision(sketch, 50, 50))
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, getPrecision(sketch, 40, 50))
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(LESS_PIXELS, getPrecision(sketch, 50, 40))
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(EXACTLY, getPrecision(sketch, 150, 50))
        }
        Resize(100, 100, longImageClipPrecision(EXACTLY)).apply {
            Assert.assertEquals(EXACTLY, getPrecision(sketch, 50, 150))
        }
    }

    @Test
    fun testScale() {
        val (context, sketch) = contextAndSketch()
        Resize(100, 30).apply {
            Assert.assertEquals(CENTER_CROP, getScale(sketch, 0, 0))
        }
        Resize(100, 30, START_CROP).apply {
            Assert.assertEquals(START_CROP, getScale(sketch, 0, 0))
        }
        Resize(100, 30, CENTER_CROP).apply {
            Assert.assertEquals(CENTER_CROP, getScale(sketch, 0, 0))
        }
        Resize(100, 30, END_CROP).apply {
            Assert.assertEquals(END_CROP, getScale(sketch, 0, 0))
        }
        Resize(100, 30, FILL).apply {
            Assert.assertEquals(FILL, getScale(sketch, 0, 0))
        }
        Resize(100, 100, longImageScale(START_CROP, CENTER_CROP)).apply {
            Assert.assertEquals(CENTER_CROP, getScale(sketch, 50, 50))
        }
        Resize(100, 100, longImageScale(START_CROP, CENTER_CROP)).apply {
            Assert.assertEquals(CENTER_CROP, getScale(sketch, 40, 50))
        }
        Resize(100, 100, longImageScale(START_CROP, CENTER_CROP)).apply {
            Assert.assertEquals(CENTER_CROP, getScale(sketch, 50, 40))
        }
        Resize(100, 100, longImageScale(START_CROP, CENTER_CROP)).apply {
            Assert.assertEquals(START_CROP, getScale(sketch, 150, 50))
        }
        Resize(100, 100, longImageScale(START_CROP, CENTER_CROP)).apply {
            Assert.assertEquals(START_CROP, getScale(sketch, 50, 150))
        }
    }
}