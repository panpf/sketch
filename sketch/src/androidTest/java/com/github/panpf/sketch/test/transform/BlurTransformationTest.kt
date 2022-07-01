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
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.BlurTransformed
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlurTransformationTest {

    @Test
    fun testConstructor() {
        assertThrow(IllegalArgumentException::class) {
            BlurTransformation(0)
        }
        assertThrow(IllegalArgumentException::class) {
            BlurTransformation(101)
        }
        BlurTransformation(12).apply {
            Assert.assertEquals(12, radius)
            Assert.assertNull(maskColor)
        }
        BlurTransformation(20, Color.GREEN).apply {
            Assert.assertEquals(20, radius)
            Assert.assertEquals(Color.GREEN, maskColor)
        }
    }

    @Test
    fun testKeyAndToString() {
        BlurTransformation(12).apply {
            Assert.assertEquals("BlurTransformation(12)", key)
            Assert.assertEquals("BlurTransformation(12)", toString())
        }
        BlurTransformation(20, Color.GREEN).apply {
            Assert.assertEquals("BlurTransformation(20,${Color.GREEN})", key)
            Assert.assertEquals("BlurTransformation(20,${Color.GREEN})", toString())
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
            Assert.assertFalse(this.isMutable)
        }
        val inBitmapCorners = inBitmap.corners()

        runBlocking {
            BlurTransformation(30).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, this)
            Assert.assertNotEquals(inBitmapCorners, bitmap.corners())
            Assert.assertEquals(Size(1291, 1936), bitmap.size)
            Assert.assertEquals(BlurTransformed(30, null), transformed)
        }

        val mutableInBitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inMutable = true
            })
        }!!.apply {
            Assert.assertTrue(this.isMutable)
        }

        runBlocking {
            BlurTransformation(30).transform(sketch, request, mutableInBitmap)
        }.apply {
            Assert.assertSame(mutableInBitmap, this.bitmap)
        }
    }

    @Test
    fun testEquals() {
        val transformation1 = BlurTransformation(12)
        val transformation11 = BlurTransformation(12)

        val transformation2 = BlurTransformation(22)
        val transformation21 = BlurTransformation(22)

        val transformation3 = BlurTransformation(32)
        val transformation31 = BlurTransformation(32)

        Assert.assertNotSame(transformation1, transformation11)
        Assert.assertNotSame(transformation2, transformation21)
        Assert.assertNotSame(transformation3, transformation31)

        Assert.assertEquals(transformation1, transformation11)
        Assert.assertEquals(transformation2, transformation21)
        Assert.assertEquals(transformation3, transformation31)

        Assert.assertNotEquals(transformation1, transformation2)
        Assert.assertNotEquals(transformation1, transformation3)
        Assert.assertNotEquals(transformation2, transformation3)
    }

    @Test
    fun testHashCode() {
        val transformation1 = BlurTransformation(12)
        val transformation11 = BlurTransformation(12)

        val transformation2 = BlurTransformation(22)
        val transformation21 = BlurTransformation(22)

        val transformation3 = BlurTransformation(32)
        val transformation31 = BlurTransformation(32)

        Assert.assertEquals(transformation1.hashCode(), transformation11.hashCode())
        Assert.assertEquals(transformation2.hashCode(), transformation21.hashCode())
        Assert.assertEquals(transformation3.hashCode(), transformation31.hashCode())

        Assert.assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        Assert.assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        Assert.assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }
}