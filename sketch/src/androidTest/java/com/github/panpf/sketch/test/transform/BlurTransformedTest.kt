package com.github.panpf.sketch.test.transform

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.transform.BlurTransformed
import com.github.panpf.sketch.transform.CircleCropTransformed
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
        BlurTransformed(30, Color.BLACK, Color.RED).apply {
            Assert.assertEquals(30, radius)
            Assert.assertEquals(Color.BLACK, hasAlphaBitmapBgColor)
            Assert.assertEquals(Color.RED, maskColor)
        }
        BlurTransformed(60, Color.WHITE, Color.GREEN).apply {
            Assert.assertEquals(60, radius)
            Assert.assertEquals(Color.WHITE, hasAlphaBitmapBgColor)
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKeyAndToString() {
        BlurTransformed(30, Color.BLACK, Color.RED).apply {
            Assert.assertEquals("BlurTransformed(30,${Color.BLACK},${Color.RED})", key)
            Assert.assertEquals("BlurTransformed(30,${Color.BLACK},${Color.RED})", toString())
        }
        BlurTransformed(60, Color.WHITE, Color.GREEN).apply {
            Assert.assertEquals("BlurTransformed(60,${Color.WHITE},${Color.GREEN})", key)
            Assert.assertEquals("BlurTransformed(60,${Color.WHITE},${Color.GREEN})", toString())
        }
        BlurTransformed(120, null, null).apply {
            Assert.assertEquals("BlurTransformed(120,null,null)", key)
            Assert.assertEquals("BlurTransformed(120,null,null)", toString())
        }
    }

    @Test
    fun testCacheResultToDisk() {
        BlurTransformed(30, Color.WHITE, Color.RED).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
        BlurTransformed(60, Color.BLACK, Color.GREEN).apply {
            Assert.assertTrue(cacheResultToDisk)
        }
    }

    @Test
    fun testGetInSampledTransformed() {
        listOf(BlurTransformed(30, Color.BLACK, Color.RED)).apply {
            Assert.assertNotNull(getBlurTransformed())
        }
        listOf<Transformed>(CircleCropTransformed(END_CROP)).apply {
            Assert.assertNull(getBlurTransformed())
        }
        listOf<Transformed>().apply {
            Assert.assertNull(getBlurTransformed())
        }
    }

    @Test
    fun testEquals() {
        val element1 = BlurTransformed(20, null, null)
        val element11 = BlurTransformed(20, null, null)
        val element2 = BlurTransformed(10, Color.GREEN, null)
        val element3 = BlurTransformed(20, Color.BLACK, Color.BLUE)
        val element4 = BlurTransformed(20, Color.BLACK, Color.WHITE)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element11, element2)
        Assert.assertNotSame(element11, element3)
        Assert.assertNotSame(element11, element4)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element3, element4)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testJsonSerializable() {
        val transformed = BlurTransformed(30, Color.WHITE, Color.RED)

        val serializer =
            transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                .newInstance()

        val transformed1 = serializer.fromJson(serializer.toJson(transformed))

        Assert.assertNotSame(transformed, transformed1)
        Assert.assertEquals(transformed, transformed1)

        val transformed2 = serializer.fromJson(serializer.toJson(transformed).apply {
            remove("hasAlphaBitmapBgColor")
        })
        Assert.assertNotEquals(transformed, transformed2)

        val transformed3 = serializer.fromJson(serializer.toJson(transformed).apply {
            remove("maskColor")
        })
        Assert.assertNotEquals(transformed, transformed3)
    }
}