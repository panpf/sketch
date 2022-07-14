package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.longImageClipPrecision
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
        LongImageClipPrecisionDecider().apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(150, 48, 50, 50))
        }
        assertThrow(IllegalArgumentException::class) {
            LongImageClipPrecisionDecider(LESS_PIXELS)
        }
    }

    @Test
    fun testGet() {
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }

        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(150, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "LongImageClip(EXACTLY,Default(2.5,5.0))",
            LongImageClipPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "LongImageClip(SAME_ASPECT_RATIO,Default(2.5,5.0))",
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).key
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(precision=EXACTLY, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
            LongImageClipPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(precision=SAME_ASPECT_RATIO, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
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