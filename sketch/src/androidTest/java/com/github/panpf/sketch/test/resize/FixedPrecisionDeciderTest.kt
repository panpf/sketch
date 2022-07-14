package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.fixedPrecision
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
        FixedPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 50, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 51, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(100, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 50, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 51, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, get(100, 32, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(100, 33, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(100, 34, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "Fixed(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).key
        )
        Assert.assertEquals(
            "Fixed(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "Fixed(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).key
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "FixedPrecisionDecider(precision=SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(precision=EXACTLY)",
            FixedPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(precision=LESS_PIXELS)",
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