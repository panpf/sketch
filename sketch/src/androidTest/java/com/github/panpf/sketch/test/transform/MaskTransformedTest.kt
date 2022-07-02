package com.github.panpf.sketch.test.transform

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.transform.MaskTransformed
import com.github.panpf.sketch.transform.getMaskTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaskTransformedTest {

    @Test
    fun test() {
        MaskTransformed(Color.RED).apply {
            Assert.assertEquals(Color.RED, maskColor)
        }
        MaskTransformed(Color.GREEN).apply {
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKey() {
        MaskTransformed(Color.RED).apply {
            Assert.assertEquals("MaskTransformed(${Color.RED})", key)
        }
        MaskTransformed(Color.GREEN).apply {
            Assert.assertEquals("MaskTransformed(${Color.GREEN})", key)
        }
    }

    @Test
    fun testToString() {
        MaskTransformed(Color.RED).apply {
            Assert.assertEquals(key, toString())
        }
        MaskTransformed(Color.GREEN).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        MaskTransformed(Color.RED).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        MaskTransformed(Color.GREEN).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(MaskTransformed(Color.RED)).apply {
            Assert.assertNotNull(getMaskTransformed())
        }
        listOf<Transformed>(CircleCropTransformed(END_CROP)).apply {
            Assert.assertNull(getMaskTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getMaskTransformed())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MaskTransformed(Color.RED)
        val element11 = MaskTransformed(Color.RED)
        val element2 = MaskTransformed(Color.BLACK)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testJsonSerializable() {
        val transformed = MaskTransformed(Color.RED)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}