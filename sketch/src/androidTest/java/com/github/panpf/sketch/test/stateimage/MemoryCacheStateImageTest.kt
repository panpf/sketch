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
        MemoryCacheStateImage(memoryCacheKey, null).apply {
            Assert.assertNull(getDrawable(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE))).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is ColorDrawable)
        }

        memoryCache.put(
            memoryCacheKey,
            CountBitmap(
                initBitmap = Bitmap.createBitmap(100, 100, RGB_565),
                imageUri = request.uriString,
                requestKey = request.key,
                requestCacheKey = request.cacheKey,
                imageInfo = ImageInfo(100, 100, "image/jpeg"),
                imageExifOrientation = 0,
                transformedList = null,
                logger = sketch.logger,
                bitmapPool = sketch.bitmapPool
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
    fun testEquals() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val memoryCacheKey = request.cacheKey

        val stateImage1 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE)))
        val stateImage11 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE)))

        val stateImage2 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN)))
        val stateImage21 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN)))

        val stateImage3 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.RED)))
        val stateImage31 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.RED)))

        Assert.assertNotSame(stateImage1, stateImage11)
        Assert.assertNotSame(stateImage2, stateImage21)
        Assert.assertNotSame(stateImage3, stateImage31)

        Assert.assertEquals(stateImage1, stateImage11)
        Assert.assertEquals(stateImage2, stateImage21)
        Assert.assertEquals(stateImage3, stateImage31)

        Assert.assertNotEquals(stateImage1, stateImage2)
        Assert.assertNotEquals(stateImage1, stateImage3)
        Assert.assertNotEquals(stateImage2, stateImage3)
    }

    @Test
    fun testHashCode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val memoryCacheKey = request.cacheKey

        val stateImage1 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE)))
        val stateImage11 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE)))

        val stateImage2 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN)))
        val stateImage21 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN)))

        val stateImage3 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.RED)))
        val stateImage31 =
            MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.RED)))

        Assert.assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        Assert.assertEquals(stateImage2.hashCode(), stateImage21.hashCode())
        Assert.assertEquals(stateImage3.hashCode(), stateImage31.hashCode())

        Assert.assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
        Assert.assertNotEquals(stateImage1.hashCode(), stateImage3.hashCode())
        Assert.assertNotEquals(stateImage2.hashCode(), stateImage3.hashCode())
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val memoryCacheKey = request.cacheKey

        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.BLUE))).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(memoryCacheKey=$memoryCacheKey, defaultImage=ColorStateImage(color=IntColor(${Color.BLUE})))",
                toString()
            )
        }
        MemoryCacheStateImage(memoryCacheKey, ColorStateImage(IntColor(Color.GREEN))).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(memoryCacheKey=$memoryCacheKey, defaultImage=ColorStateImage(color=IntColor(${Color.GREEN})))",
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