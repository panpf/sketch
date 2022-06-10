package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.test.utils.getContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageClipPrecisionDeciderTest {

    @Test
    fun testConstructor() {
        longImageClipPrecision(SAME_ASPECT_RATIO)
        longImageClipPrecision(EXACTLY)
        assertThrow(IllegalArgumentException::class) {
            longImageClipPrecision(LESS_PIXELS)
        }
    }

    @Test
    fun testPrecision() {
        val (_, sketch) = getContextAndNewSketch()
        longImageClipPrecision(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 77, 50, 50))
        }

        longImageClipPrecision(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(sketch, 150, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 150, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 77, 50, 50))
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(SAME_ASPECT_RATIO)",
            longImageClipPrecision(SAME_ASPECT_RATIO).toString()
        )
    }
}