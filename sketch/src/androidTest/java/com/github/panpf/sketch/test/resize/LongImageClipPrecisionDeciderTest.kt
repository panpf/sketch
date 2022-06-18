package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageClipPrecisionDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(LongImageClipPrecisionDecider(EXACTLY), longImageClipPrecision(EXACTLY))
        Assert.assertEquals(
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
            longImageClipPrecision(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testConstructor() {
        val sketch = newSketch()
        LongImageClipPrecisionDecider().apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(sketch, 150, 48, 50, 50))
        }
        assertThrow(IllegalArgumentException::class) {
            LongImageClipPrecisionDecider(LESS_PIXELS)
        }
    }

    @Test
    fun testGet() {
        val sketch = newSketch()
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(sketch, 150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 77, 50, 50))
        }

        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(sketch, 150, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 150, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(sketch, 150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(sketch, 150, 77, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(EXACTLY)",
            LongImageClipPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(SAME_ASPECT_RATIO)",
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).key
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(EXACTLY)",
            LongImageClipPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(SAME_ASPECT_RATIO)",
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
    }

    @Test
    fun testSerializer() {
        val transformed = LongImageClipPrecisionDecider(SAME_ASPECT_RATIO)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}