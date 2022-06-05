package com.github.panpf.sketch.test.transform

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.transform.BlurTransformed
import com.github.panpf.sketch.transform.getBlurTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlurTransformedTest {

    @Test
    fun test() {
        BlurTransformed(30, Color.RED).apply {
            Assert.assertEquals(30, radius)
            Assert.assertEquals(Color.RED, maskColor)
        }
        BlurTransformed(60, Color.GREEN).apply {
            Assert.assertEquals(60, radius)
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKey() {
        BlurTransformed(30, Color.RED).apply {
            Assert.assertEquals("BlurTransformed(30,${Color.RED})", key)
        }
        BlurTransformed(60, Color.GREEN).apply {
            Assert.assertEquals("BlurTransformed(60,${Color.GREEN})", key)
        }
        BlurTransformed(120, null).apply {
            Assert.assertEquals("BlurTransformed(120,-1)", key)
        }
    }

    @Test
    fun testToString() {
        BlurTransformed(30, Color.RED).apply {
            Assert.assertEquals(key, toString())
        }
        BlurTransformed(60, Color.GREEN).apply {
            Assert.assertEquals(key, toString())
        }
        BlurTransformed(60, null).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        BlurTransformed(30, Color.RED).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        BlurTransformed(60, Color.GREEN).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(BlurTransformed(30, Color.RED)).apply {
            Assert.assertNotNull(getBlurTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getBlurTransformed())
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = BlurTransformed(30, Color.RED)
        val transformed11 = BlurTransformed(30, Color.RED)

        val transformed2 = BlurTransformed(60, Color.GREEN);
        val transformed21 = BlurTransformed(60, Color.GREEN)

        val transformed3 = BlurTransformed(120, Color.BLUE);
        val transformed31 = BlurTransformed(120, Color.BLUE)

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
        val transformed1 = BlurTransformed(30, Color.RED)
        val transformed11 = BlurTransformed(30, Color.RED)

        val transformed2 = BlurTransformed(60, Color.GREEN)
        val transformed21 = BlurTransformed(60, Color.GREEN)

        val transformed3 = BlurTransformed(120, Color.BLUE)
        val transformed31 = BlurTransformed(120, Color.BLUE)

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testJsonSerializable() {
        val transformed = BlurTransformed(30, Color.RED)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}