package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.fixedPrecision
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixedPrecisionDeciderTest {

    @Test
    fun testPrecision() {
        val context = InstrumentationRegistry.getInstrumentation().context
        fixedPrecision(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(context, 100, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(context, 100, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(context, 100, 50, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(context, 100, 51, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(context, 100, 52, 50, 50))
        }

        fixedPrecision(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(context, 100, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(context, 100, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(context, 100, 50, 50, 50))
            Assert.assertEquals(EXACTLY, get(context, 100, 51, 50, 50))
            Assert.assertEquals(EXACTLY, get(context, 100, 52, 50, 50))
        }

        fixedPrecision(LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, get(context, 100, 32, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(context, 100, 33, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(context, 100, 34, 50, 50))
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "FixedPrecisionDecider(SAME_ASPECT_RATIO)",
            fixedPrecision(SAME_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(EXACTLY)",
            fixedPrecision(EXACTLY).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(LESS_PIXELS)",
            fixedPrecision(LESS_PIXELS).toString()
        )
    }
}