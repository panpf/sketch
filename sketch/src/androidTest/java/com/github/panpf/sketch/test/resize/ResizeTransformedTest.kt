package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.getResizeTransformed
import com.github.panpf.sketch.resize.longImageClipPrecision
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeTransformedTest {

    @Test
    fun testKey() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        ResizeTransformed(Resize(50, 100)).apply {
            Assert.assertEquals("ResizeTransformed(50x100,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }

        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        ResizeTransformed(Resize(100, 50, precision = KEEP_ASPECT_RATIO)).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,Fixed(KEEP_ASPECT_RATIO),CENTER_CROP)",
                key
            )
        }
        ResizeTransformed(Resize(100, 50, precision = EXACTLY)).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,Fixed(EXACTLY),CENTER_CROP)",
                key
            )
        }
        ResizeTransformed(
            Resize(
                100,
                50,
                precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO)
            )
        ).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,LongImageClip(KEEP_ASPECT_RATIO,2.0),CENTER_CROP)",
                key
            )
        }
        ResizeTransformed(
            Resize(
                100,
                50,
                precisionDecider = longImageClipPrecision(EXACTLY)
            )
        ).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,LongImageClip(EXACTLY,2.0),CENTER_CROP)",
                key
            )
        }

        ResizeTransformed(Resize(100, 50, START_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),START_CROP)", key)
        }
        ResizeTransformed(Resize(100, 50, CENTER_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),CENTER_CROP)", key)
        }
        ResizeTransformed(Resize(100, 50, END_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),END_CROP)", key)
        }
        ResizeTransformed(Resize(100, 50, FILL)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(LESS_PIXELS),FILL)", key)
        }
    }

    @Test
    fun testToString() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(50, 100)).apply {
            Assert.assertEquals(key, toString())
        }

        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, precision = KEEP_ASPECT_RATIO)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(
            Resize(100, 50, precisionDecider = longImageClipPrecision(KEEP_ASPECT_RATIO))
        ).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(
            Resize(100, 50, precisionDecider = longImageClipPrecision(EXACTLY))
        ).apply {
            Assert.assertEquals(key, toString())
        }

        ResizeTransformed(Resize(100, 50, START_CROP)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, CENTER_CROP)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, END_CROP)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, FILL)).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetResizeTransformed() {
        listOf(ResizeTransformed(Resize(100, 50))).apply {
            Assert.assertNotNull(getResizeTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getResizeTransformed())
        }
    }
}