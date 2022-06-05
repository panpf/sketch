package com.github.panpf.sketch.test.transform

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircleCropTransformedTest {

    @Test
    fun test() {
        CircleCropTransformed(Scale.START_CROP).apply {
            Assert.assertEquals(Scale.START_CROP, scale)
        }
        CircleCropTransformed(Scale.CENTER_CROP).apply {
            Assert.assertEquals(Scale.CENTER_CROP, scale)
        }
    }

    @Test
    fun testKey() {
        CircleCropTransformed(Scale.START_CROP).apply {
            Assert.assertEquals("CircleCropTransformed(START_CROP)", key)
        }
        CircleCropTransformed(Scale.CENTER_CROP).apply {
            Assert.assertEquals("CircleCropTransformed(CENTER_CROP)", key)
        }
    }

    @Test
    fun testToString() {
        CircleCropTransformed(Scale.START_CROP).apply {
            Assert.assertEquals(key, toString())
        }
        CircleCropTransformed(Scale.CENTER_CROP).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        CircleCropTransformed(Scale.START_CROP).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        CircleCropTransformed(Scale.CENTER_CROP).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(CircleCropTransformed(Scale.START_CROP)).apply {
            Assert.assertNotNull(getCircleCropTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getCircleCropTransformed())
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = CircleCropTransformed(Scale.START_CROP)
        val transformed11 = CircleCropTransformed(Scale.START_CROP)

        val transformed2 = CircleCropTransformed(Scale.CENTER_CROP)
        val transformed21 = CircleCropTransformed(Scale.CENTER_CROP)

        val transformed3 = CircleCropTransformed(Scale.END_CROP)
        val transformed31 = CircleCropTransformed(Scale.END_CROP)

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
        val transformed1 = CircleCropTransformed(Scale.START_CROP)
        val transformed11 = CircleCropTransformed(Scale.START_CROP)

        val transformed2 = CircleCropTransformed(Scale.CENTER_CROP)
        val transformed21 = CircleCropTransformed(Scale.CENTER_CROP)

        val transformed3 = CircleCropTransformed(Scale.END_CROP)
        val transformed31 = CircleCropTransformed(Scale.END_CROP)

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testJsonSerializable() {
        val transformed = CircleCropTransformed(Scale.START_CROP)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}