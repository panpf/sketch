package com.github.panpf.sketch.test.cache

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LruMemoryCacheTest {

    @Test
    fun testMaxSize() {
        val logger = Logger()
        LruMemoryCache(logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("10MB", maxSize.formatFileSize())
        }

        LruMemoryCache(logger, 100L * 1024 * 1024).apply {
            Assert.assertEquals("100MB", maxSize.formatFileSize())
        }
    }

    @Test
    fun testSize() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(sketch, "image1", 1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap(sketch, "image2", 2)
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGet() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertNull(get("image1"))
            putBitmap(sketch, "image1", 1)
            Assert.assertNotNull(get("image1"))

            Assert.assertNull(get("image2"))
            putBitmap(sketch, "image2", 2)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))

            remove("image1")
            Assert.assertNull(get("image1"))
            Assert.assertNotNull(get("image2"))

            remove("image2")
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
        }
    }

    @Test
    fun testLRU() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(sketch, "image1", 1)
            Assert.assertEquals("1MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))

            putBitmap(sketch, "image2", 2)
            Assert.assertEquals("3MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))

            putBitmap(sketch, "image3", 3)
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))

            putBitmap(sketch, "image4", 4)
            Assert.assertEquals("10MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))

            putBitmap(sketch, "image5", 5)
            Assert.assertEquals("9MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertNotNull(get("image5"))

            putBitmap(sketch, "image6", 6)
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertNull(get("image5"))
            Assert.assertNotNull(get("image6"))

            putBitmap(sketch, "image7", 7)
            Assert.assertEquals("7MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertNull(get("image5"))
            Assert.assertNull(get("image6"))
            Assert.assertNotNull(get("image7"))
        }
    }

    @Test
    fun testTrim() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image3", 4)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(ComponentCallbacks2.TRIM_MEMORY_MODERATE)
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertEquals("0B", size.formatFileSize())
        }

        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image3", 4)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("4MB", size.formatFileSize())
        }
    }

    @Test
    fun testClear() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image3", 4)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("10MB", size.formatFileSize())

            clear()
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertEquals("0B", size.formatFileSize())
        }
    }

    @Test
    fun testEditLock() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        LruMemoryCache(sketch.logger, 10L * 1024 * 1024).apply {
            Assert.assertNotNull(editLock("image1"))
            Assert.assertNotNull(editLock("image2"))
            Assert.assertNotNull(editLock("image3"))
            Assert.assertNotNull(editLock("image4"))
        }
    }

    @Test
    fun testToString() {
        val logger = Logger()
        LruMemoryCache(logger, 10L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(maxSize=10MB)", toString())
        }

        LruMemoryCache(logger, 100L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(maxSize=100MB)", toString())
        }
    }

    private fun LruMemoryCache.putBitmap(sketch: Sketch, imageUri: String, sizeMb: Int) {
        val bytes = sizeMb * 1024 * 1024
        val pixelCount = bytes / 4
        val width = 10
        val height = pixelCount / width
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        put(imageUri, CountBitmap(
            bitmap,
            imageUri,
            ImageInfo("image/jpeg", width, height, 0),
            imageUri,
            null,
            sketch.logger,
            sketch.bitmapPool
        ))
    }
}