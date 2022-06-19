package com.github.panpf.sketch.test.transform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RotateTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RotateTransformationTest {

    @Test
    fun testConstructor() {
        RotateTransformation(12).apply {
            Assert.assertEquals(12, degrees)
        }
        RotateTransformation(20).apply {
            Assert.assertEquals(20, degrees)
        }
    }

    @Test
    fun testKeyAndToString() {
        RotateTransformation(12).apply {
            Assert.assertEquals("RotateTransformation(12)", key)
            Assert.assertEquals("RotateTransformation(12)", toString())
        }
        RotateTransformation(20).apply {
            Assert.assertEquals("RotateTransformation(20)", key)
            Assert.assertEquals("RotateTransformation(20)", toString())
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
        val inBitmapCorners = inBitmap.corners()

        runBlocking {
            RotateTransformation(90).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, this)
            Assert.assertEquals(
                listOf(
                    inBitmapCorners[3],
                    inBitmapCorners[0],
                    inBitmapCorners[1],
                    inBitmapCorners[2],
                ),
                bitmap.corners()
            )
            Assert.assertEquals(Size(1936, 1291), bitmap.size)
            Assert.assertEquals(
                RotateTransformed(RotateTransformation(90).degrees),
                transformed
            )
        }

        runBlocking {
            RotateTransformation(450).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, this)
            Assert.assertEquals(
                listOf(
                    inBitmapCorners[3],
                    inBitmapCorners[0],
                    inBitmapCorners[1],
                    inBitmapCorners[2],
                ),
                bitmap.corners()
            )
            Assert.assertEquals(Size(1936, 1291), bitmap.size)
            Assert.assertEquals(
                RotateTransformed(RotateTransformation(450).degrees),
                transformed
            )
        }

        runBlocking {
            RotateTransformation(45).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, this)
            Assert.assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                bitmap.corners()
            )
            Assert.assertEquals(Size(2281, 2281), bitmap.size)
            Assert.assertEquals(
                RotateTransformed(RotateTransformation(45).degrees),
                transformed
            )
        }

        val rgb565InBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            })
        }!!.apply {
            Assert.assertEquals(Bitmap.Config.RGB_565, this.config)
        }
        runBlocking {
            RotateTransformation(90).transform(sketch, request, rgb565InBitmap)
        }.apply {
            Assert.assertEquals(Bitmap.Config.RGB_565, this.bitmap.config)
        }
        runBlocking {
            RotateTransformation(45).transform(sketch, request, rgb565InBitmap)
        }.apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, this.bitmap.config)
        }
    }

    @Test
    fun testEquals() {
        val transformation1 = RotateTransformation(12)
        val transformation11 = RotateTransformation(12)

        val transformation2 = RotateTransformation(22)
        val transformation21 = RotateTransformation(22)

        val transformation3 = RotateTransformation(32)
        val transformation31 = RotateTransformation(32)

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
        val transformation1 = RotateTransformation(12)
        val transformation11 = RotateTransformation(12)

        val transformation2 = RotateTransformation(22)
        val transformation21 = RotateTransformation(22)

        val transformation3 = RotateTransformation(32)
        val transformation31 = RotateTransformation(32)

        Assert.assertEquals(transformation1.hashCode(), transformation11.hashCode())
        Assert.assertEquals(transformation2.hashCode(), transformation21.hashCode())
        Assert.assertEquals(transformation3.hashCode(), transformation31.hashCode())

        Assert.assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        Assert.assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        Assert.assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }
}