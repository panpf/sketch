package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixedPrecisionDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(FixedPrecisionDecider(EXACTLY), fixedPrecision(EXACTLY))
        Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), fixedPrecision(LESS_PIXELS))
        Assert.assertEquals(
            FixedPrecisionDecider(SAME_ASPECT_RATIO),
            fixedPrecision(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testGet() {
        val sketch = newSketch()
        FixedPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 100, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 100, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 100, 50, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 100, 51, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 100, 52, 50, 50))
        }

        FixedPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(sketch, 100, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 100, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 100, 50, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 100, 51, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 100, 52, 50, 50))
        }

        FixedPrecisionDecider(LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, get(sketch, 100, 32, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 100, 33, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 100, 34, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "FixedPrecisionDecider(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).key
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).key
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "FixedPrecisionDecider(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).toString()
        )
    }

    @Test
    fun testSerializer() {
        val precisionDecider = FixedPrecisionDecider(LESS_PIXELS)

        val serializer =
            precisionDecider.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(precisionDecider))

        Assert.assertNotSame(precisionDecider, transformed1)
        Assert.assertEquals(precisionDecider, transformed1)
    }
}