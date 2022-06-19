package com.github.panpf.sketch.test.transform

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CircleCropTransformationTest {

    @Test
    fun testConstructor() {
        CircleCropTransformation().apply {
            Assert.assertEquals(Scale.CENTER_CROP, scale)
        }
        CircleCropTransformation(Scale.START_CROP).apply {
            Assert.assertEquals(Scale.START_CROP, scale)
        }
    }

    @Test
    fun testKeyAndToString() {
        CircleCropTransformation(Scale.CENTER_CROP).apply {
            Assert.assertEquals("CircleCropTransformation(CENTER_CROP)", key)
            Assert.assertEquals("CircleCropTransformation(CENTER_CROP)", toString())
        }
        CircleCropTransformation(Scale.START_CROP).apply {
            Assert.assertEquals("CircleCropTransformation(START_CROP)", key)
            Assert.assertEquals("CircleCropTransformation(START_CROP)", toString())
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
            CircleCropTransformation(Scale.START_CROP).transform(sketch, request, inBitmap)
        }.apply {
            Assert.assertNotSame(inBitmap, bitmap)
            Assert.assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                bitmap.corners()
            )
            Assert.assertEquals(Size(1291, 1291), bitmap.size)
            Assert.assertEquals(CircleCropTransformed(Scale.START_CROP), transformed)
        }
    }

    @Test
    fun testEquals() {
        val transformation1 = CircleCropTransformation(Scale.START_CROP)
        val transformation11 = CircleCropTransformation(Scale.START_CROP)

        val transformation2 = CircleCropTransformation(Scale.CENTER_CROP)
        val transformation21 = CircleCropTransformation(Scale.CENTER_CROP)

        val transformation3 = CircleCropTransformation(Scale.END_CROP)
        val transformation31 = CircleCropTransformation(Scale.END_CROP)

        Assert.assertNotSame(transformation1, transformation11)
        Assert.assertNotSame(transformation2, transformation21)
        Assert.assertNotSame(transformation3, transformation31)

        Assert.assertEquals(transformation1, transformation11)
        Assert.assertEquals(transformation2, transformation21)
        Assert.assertEquals(transformation3, transformation31)

        Assert.assertNotEquals(transformation1, transformation2)
        Assert.assertNotEquals(transformation1, transformation3)
        Assert.assertNotEquals(transformation2, transformation3)

        Assert.assertNotEquals(transformation2, null)
    }

    @Test
    fun testHashCode() {
        val transformation1 = CircleCropTransformation(Scale.START_CROP)
        val transformation11 = CircleCropTransformation(Scale.START_CROP)

        val transformation2 = CircleCropTransformation(Scale.CENTER_CROP)
        val transformation21 = CircleCropTransformation(Scale.CENTER_CROP)

        val transformation3 = CircleCropTransformation(Scale.END_CROP)
        val transformation31 = CircleCropTransformation(Scale.END_CROP)

        Assert.assertEquals(transformation1.hashCode(), transformation11.hashCode())
        Assert.assertEquals(transformation2.hashCode(), transformation21.hashCode())
        Assert.assertEquals(transformation3.hashCode(), transformation31.hashCode())

        Assert.assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        Assert.assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        Assert.assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }
}