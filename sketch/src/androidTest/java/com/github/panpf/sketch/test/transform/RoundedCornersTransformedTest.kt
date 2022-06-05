package com.github.panpf.sketch.test.transform

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.transform.RoundedCornersTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoundedCornersTransformedTest {

    @Test
    fun test() {
        RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)).apply {
            Assert.assertEquals(
                floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f).toList(),
                radiusArray.toList()
            )
        }
        RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)).apply {
            Assert.assertEquals(
                floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f).toList(),
                radiusArray.toList()
            )
        }
    }

    @Test
    fun testKey() {
        RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)).apply {
            Assert.assertEquals(
                "RoundedCornersTransformed([8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0])",
                key
            )
        }
        RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)).apply {
            Assert.assertEquals(
                "RoundedCornersTransformed([10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0])",
                key
            )
        }
    }

    @Test
    fun testToString() {
        RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)).apply {
            Assert.assertEquals(key, toString())
        }
        RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)).apply {
            Assert.assertEquals(key, toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))).apply {
            Assert.assertNotNull(getRoundedCornersTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getRoundedCornersTransformed())
        }
    }

    @Test
    fun testEquals() {
        val transformed1 = RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))
        val transformed11 = RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))

        val transformed2 =
            RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
        val transformed21 =
            RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))

        val transformed3 =
            RoundedCornersTransformed(floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f))
        val transformed31 =
            RoundedCornersTransformed(floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f))

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
        val transformed1 = RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))
        val transformed11 = RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))

        val transformed2 =
            RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
        val transformed21 =
            RoundedCornersTransformed(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))

        val transformed3 =
            RoundedCornersTransformed(floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f))
        val transformed31 =
            RoundedCornersTransformed(floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f))

        Assert.assertEquals(transformed1.hashCode(), transformed11.hashCode())
        Assert.assertEquals(transformed2.hashCode(), transformed21.hashCode())
        Assert.assertEquals(transformed3.hashCode(), transformed31.hashCode())

        Assert.assertNotEquals(transformed1.hashCode(), transformed2.hashCode())
        Assert.assertNotEquals(transformed1.hashCode(), transformed3.hashCode())
        Assert.assertNotEquals(transformed2.hashCode(), transformed3.hashCode())
    }

    @Test
    fun testJsonSerializable() {
        val transformed = RoundedCornersTransformed(floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f))

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)
    }
}