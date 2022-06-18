package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InSampledTransformedTest {

    @Test
    fun test() {
        InSampledTransformed(2).apply {
            Assert.assertEquals(2, inSampleSize)
        }
        InSampledTransformed(4).apply {
            Assert.assertEquals(4, inSampleSize)
        }
    }

    @Test
    fun testKey() {
        InSampledTransformed(2).apply {
            Assert.assertEquals("InSampledTransformed(2)", key)
        }
        InSampledTransformed(4).apply {
            Assert.assertEquals("InSampledTransformed(4)", key)
        }
    }

    @Test
    fun testToString() {
        InSampledTransformed(2).apply {
            Assert.assertEquals(key, toString())
        }
        InSampledTransformed(4).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        InSampledTransformed(2).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        InSampledTransformed(4).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(InSampledTransformed(2)).apply {
            Assert.assertNotNull(getInSampledTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getInSampledTransformed())
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = InSampledTransformed(2)
        val transformed11 = InSampledTransformed(2)

        val transformed2 = InSampledTransformed(4)
        val transformed21 = InSampledTransformed(4)

        val transformed3 = InSampledTransformed(8)
        val transformed31 = InSampledTransformed(8)

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
        val transformed1 = InSampledTransformed(2)
        val transformed11 = InSampledTransformed(2)

        val transformed2 = InSampledTransformed(4)
        val transformed21 = InSampledTransformed(4)

        val transformed3 = InSampledTransformed(8)
        val transformed31 = InSampledTransformed(8)

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testSerializer() {
        val transformed = InSampledTransformed(2)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}