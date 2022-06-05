package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.getResizeTransformed
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeTransformedTest {

    @Test
    fun test() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertEquals(Resize(100, 50), resize)
        }
        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals(Resize(100, 50, precision = LESS_PIXELS), resize)
        }
    }

    @Test
    fun testKey() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(CENTER_CROP))", key)
        }
        ResizeTransformed(Resize(50, 100)).apply {
            Assert.assertEquals("ResizeTransformed(50x100,Fixed(EXACTLY),Fixed(CENTER_CROP))", key)
        }

        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))",
                key
            )
        }
        ResizeTransformed(Resize(100, 50, precision = SAME_ASPECT_RATIO)).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,Fixed(SAME_ASPECT_RATIO),Fixed(CENTER_CROP))",
                key
            )
        }
        ResizeTransformed(Resize(100, 50, precision = EXACTLY)).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(CENTER_CROP))",
                key
            )
        }
        ResizeTransformed(
            Resize(
                100,
                50,
                precision = longImageClipPrecision(SAME_ASPECT_RATIO)
            )
        ).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,LongImageClip(SAME_ASPECT_RATIO),Fixed(CENTER_CROP))",
                key
            )
        }
        ResizeTransformed(
            Resize(
                100,
                50,
                precision = longImageClipPrecision(EXACTLY)
            )
        ).apply {
            Assert.assertEquals(
                "ResizeTransformed(100x50,LongImageClip(EXACTLY),Fixed(CENTER_CROP))",
                key
            )
        }

        ResizeTransformed(Resize(100, 50, START_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(START_CROP))", key)
        }
        ResizeTransformed(Resize(100, 50, CENTER_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(CENTER_CROP))", key)
        }
        ResizeTransformed(Resize(100, 50, END_CROP)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(END_CROP))", key)
        }
        ResizeTransformed(Resize(100, 50, FILL)).apply {
            Assert.assertEquals("ResizeTransformed(100x50,Fixed(EXACTLY),Fixed(FILL))", key)
        }
    }

    @Test
    fun testCacheResultToDisk() {
        ResizeTransformed(Resize(100, 50)).apply {
            Assert.assertTrue(cacheResultToDisk)
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
        ResizeTransformed(Resize(100, 50, precision = SAME_ASPECT_RATIO)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(Resize(100, 50, precision = LESS_PIXELS)).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(
            Resize(100, 50, precision = longImageClipPrecision(SAME_ASPECT_RATIO))
        ).apply {
            Assert.assertEquals(key, toString())
        }
        ResizeTransformed(
            Resize(100, 50, precision = longImageClipPrecision(EXACTLY))
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
    fun testEquals() {
        val transformed1 = ResizeTransformed(Resize(100, 50, END_CROP))
        val transformed11 = ResizeTransformed(Resize(100, 50, END_CROP))

        val transformed2 = ResizeTransformed(Resize(200, 4000, START_CROP))
        val transformed21 = ResizeTransformed(Resize(200, 4000, START_CROP))

        val transformed3 = ResizeTransformed(Resize(300, 300, CENTER_CROP))
        val transformed31 = ResizeTransformed(Resize(300, 300, CENTER_CROP))

        Assert.assertNotSame(transformed1, transformed11)
        Assert.assertNotSame(transformed2, transformed21)
        Assert.assertNotSame(transformed3, transformed31)

        Assert.assertEquals(transformed1, transformed11)
        Assert.assertEquals(transformed2, transformed21)
        Assert.assertEquals(transformed3, transformed31)

        Assert.assertNotEquals(transformed1, transformed2)
        Assert.assertNotEquals(transformed1, transformed3)
        Assert.assertNotEquals(transformed2, transformed3)
    }

    @Test
    fun testHashCode() {
        val transformed1 = ResizeTransformed(Resize(100, 50, END_CROP))
        val transformed11 = ResizeTransformed(Resize(100, 50, END_CROP))

        val transformed2 = ResizeTransformed(Resize(200, 4000, START_CROP))
        val transformed21 = ResizeTransformed(Resize(200, 4000, START_CROP))

        val transformed3 = ResizeTransformed(Resize(300, 300, CENTER_CROP))
        val transformed31 = ResizeTransformed(Resize(300, 300, CENTER_CROP))

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
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

    @Test
    fun testJsonSerializable() {
        val transformed = ResizeTransformed(Resize(100, 50, END_CROP))

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}