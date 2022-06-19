package com.github.panpf.sketch.test.transform

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformed
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoundedCornersTransformationTest {

    @Test
    fun testConstructor() {
        assertThrow(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f))
        }
        assertThrow(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f))
        }
        assertThrow(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, -4f, 5f, 6f, 7f, 8f))
        }

        RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f)).apply {
            Assert.assertEquals(
                floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f).toList(),
                radiusArray.toList()
            )
        }

        RoundedCornersTransformation(
            topLeft = 1f,
            topRight = 2f,
            bottomLeft = 3f,
            bottomRight = 4f
        ).apply {
            Assert.assertEquals(
                floatArrayOf(1f, 1f, 2f, 2f, 3f, 3f, 4f, 4f).toList(),
                radiusArray.toList()
            )
        }

        RoundedCornersTransformation(
            allRadius = 1f,
        ).apply {
            Assert.assertEquals(
                floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f).toList(),
                radiusArray.toList()
            )
        }
    }

    @Test
    fun testKeyAndToString() {
        RoundedCornersTransformation(1f).apply {
            Assert.assertEquals(
                "RoundedCornersTransformation(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0)",
                key
            )
            Assert.assertEquals(
                "RoundedCornersTransformation(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0)",
                toString()
            )
        }
        RoundedCornersTransformation(2f).apply {
            Assert.assertEquals(
                "RoundedCornersTransformation(2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0)",
                key
            )
            Assert.assertEquals(
                "RoundedCornersTransformation(2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0)",
                toString()
            )
        }
    }

    @Test
    fun testTransform() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        val inBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }.apply {
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                this.corners()
            )
            Assert.assertEquals(
                Size(1291, 1936),
                this.size
            )
        }

        runBlocking {
            RoundedCornersTransformation(20f).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, bitmap)
            Assert.assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                bitmap.corners()
            )
            Assert.assertEquals(Size(1291, 1936), bitmap.size)
            Assert.assertEquals(
                RoundedCornersTransformed(RoundedCornersTransformation(20f).radiusArray),
                transformed
            )
        }
    }

    @Test
    fun testEquals() {
        val transformation1 = RoundedCornersTransformation(10f)
        val transformation11 = RoundedCornersTransformation(10f)

        val transformation2 = RoundedCornersTransformation(20f)
        val transformation21 = RoundedCornersTransformation(20f)

        val transformation3 = RoundedCornersTransformation(30f)
        val transformation31 = RoundedCornersTransformation(30f)

        Assert.assertNotSame(transformation1, transformation11)
        Assert.assertNotSame(transformation2, transformation21)
        Assert.assertNotSame(transformation3, transformation31)

        Assert.assertEquals(transformation1, transformation1)
        Assert.assertEquals(transformation1, transformation11)
        Assert.assertEquals(transformation2, transformation21)
        Assert.assertEquals(transformation3, transformation31)

        Assert.assertNotEquals(transformation1, transformation2)
        Assert.assertNotEquals(transformation1, transformation3)
        Assert.assertNotEquals(transformation2, transformation3)

        Assert.assertNotEquals(transformation2, null)
        Assert.assertNotEquals(transformation2, Any())
    }

    @Test
    fun testHashCode() {
        val transformation1 = RoundedCornersTransformation(10f)
        val transformation11 = RoundedCornersTransformation(10f)

        val transformation2 = RoundedCornersTransformation(20f)
        val transformation21 = RoundedCornersTransformation(20f)

        val transformation3 = RoundedCornersTransformation(30f)
        val transformation31 = RoundedCornersTransformation(30f)

        Assert.assertEquals(transformation1.hashCode(), transformation11.hashCode())
        Assert.assertEquals(transformation2.hashCode(), transformation21.hashCode())
        Assert.assertEquals(transformation3.hashCode(), transformation31.hashCode())

        Assert.assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        Assert.assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        Assert.assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }
}