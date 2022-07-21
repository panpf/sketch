package com.github.panpf.sketch.test.stateimage

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.stateimage.MemoryCacheStateImage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryCacheStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.cacheKey

        Assert.assertFalse(memoryCache.exist(memoryCacheKey))

        MemoryCacheStateImage(null, null).apply {
            Assert.assertNull(getDrawable(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey).apply {
            Assert.assertNull(getDrawable(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE))).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }

        memoryCache.put(
            memoryCacheKey,
            CountBitmap(
                bitmap = Bitmap.createBitmap(100, 100, RGB_565),
                sketch = sketch,
                imageUri = request.uriString,
                requestKey = request.key,
                requestCacheKey = request.cacheKey,
                imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
                transformedList = null,
            )
        )

        Assert.assertTrue(memoryCache.exist(memoryCacheKey))

        MemoryCacheStateImage(null, null).apply {
            Assert.assertNull(getDrawable(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, null).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is SketchCountBitmapDrawable)
        }
        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE))).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is SketchCountBitmapDrawable)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MemoryCacheStateImage("key1", ColorStateImage(IntColor(Color.BLUE)))
        val element11 = MemoryCacheStateImage("key1", ColorStateImage(IntColor(Color.BLUE)))
        val element2 = MemoryCacheStateImage("key1", ColorStateImage(IntColor(Color.GREEN)))
        val element3 = MemoryCacheStateImage("key2", ColorStateImage(IntColor(Color.BLUE)))
        val element4 = MemoryCacheStateImage(null, ColorStateImage(IntColor(Color.BLUE)))
        val element5 = MemoryCacheStateImage("key1", null)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element1, element5)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element2, element5)
        Assert.assertNotSame(element3, element4)
        Assert.assertNotSame(element3, element5)
        Assert.assertNotSame(element4, element5)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element1, element5)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element2, element5)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element3, element5)
        Assert.assertNotEquals(element4, element5)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val memoryCacheKey = request.cacheKey

        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE))).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(memoryCacheKey=$memoryCacheKey, defaultImage=ColorStateImage(IntColor(${Color.BLUE})))",
                toString()
            )
        }
        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN))).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(memoryCacheKey=$memoryCacheKey, defaultImage=ColorStateImage(IntColor(${Color.GREEN})))",
                toString()
            )
        }
        MemoryCacheStateImage(null, null).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(memoryCacheKey=null, defaultImage=null)",
                toString()
            )
        }
    }
}