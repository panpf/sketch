package com.github.panpf.sketch.test.transform

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.transform.RotateTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RotateTransformedTest {

    @Test
    fun test() {
        RotateTransformed(45).apply {
            Assert.assertEquals(45, degrees)
        }
        RotateTransformed(90).apply {
            Assert.assertEquals(90, degrees)
        }
    }

    @Test
    fun testKey() {
        RotateTransformed(45).apply {
            Assert.assertEquals("RotateTransformed(45)", key)
        }
        RotateTransformed(90).apply {
            Assert.assertEquals("RotateTransformed(90)", key)
        }
    }

    @Test
    fun testToString() {
        RotateTransformed(45).apply {
            Assert.assertEquals(key, toString())
        }
        RotateTransformed(90).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        RotateTransformed(45).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        RotateTransformed(90).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(RotateTransformed(45)).apply {
            Assert.assertNotNull(getRotateTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getRotateTransformed())
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = RotateTransformed(45)
        val transformed11 = RotateTransformed(45)

        val transformed2 = RotateTransformed(90)
        val transformed21 = RotateTransformed(90)

        val transformed3 = RotateTransformed(135)
        val transformed31 = RotateTransformed(135)

        Assert.assertNotSame(transformed1, transformed11)
        Assert.assertNotSame(transformed2, transformed21)
        Assert.assertNotSame(transformed3, transformed31)

        Assert.assertEquals(transformed1, transformed1)
        Assert.assertEquals(transformed1, transformed11)
        Assert.assertEquals(transformed2, transformed21)
        Assert.assertEquals(transformed3, transformed31)

        Assert.assertNotEquals(transformed1, transformed2)
        Assert.assertNotEquals(transformed1, transformed3)
        Assert.assertNotEquals(transformed2, transformed3)

        Assert.assertNotEquals(transformed2, null)
        Assert.assertNotEquals(transformed2, Any())
    }

    @Test
    fun testHashCode() {
        val transformed1 = RotateTransformed(45)
        val transformed11 = RotateTransformed(45)

        val transformed2 = RotateTransformed(90)
        val transformed21 = RotateTransformed(90)

        val transformed3 = RotateTransformed(135)
        val transformed31 = RotateTransformed(135)

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testSerializer() {
        val transformed = RotateTransformed(45)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}