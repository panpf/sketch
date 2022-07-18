package com.github.panpf.sketch.test.cache.internal

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.util.formatFileSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LruMemoryCacheTest {

    @Test
    fun testMaxSize() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("10MB", maxSize.formatFileSize())
        }

        LruMemoryCache(100L * 1024 * 1024).apply {
            Assert.assertEquals("100MB", maxSize.formatFileSize())
        }
    }

    @Test
    fun testSize() {
        val sketch = newSketch()
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap(sketch, "image1", 1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap(sketch, "image2", 2)
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGet() {
        val sketch = newSketch()
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertNull(get("image1"))
            Assert.assertTrue(putBitmap(sketch, "image1", 1))
            Assert.assertNotNull(get("image1"))
            Assert.assertFalse(putBitmap(sketch, "image1", 1))

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
        val sketch = newSketch()
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

//            val bigBitmapSize =
//                sqrt(sketch.memoryCache.maxSize.toFloat() * 0.8f / 1024 / 1024).toInt()
//            Assert.assertFalse(putBitmap(sketch, "image0", bigBitmapSize))
//            Assert.assertEquals("0B", size.formatFileSize())

            Assert.assertTrue(putBitmap(sketch, "image1", 1))
            Assert.assertEquals("1MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))

            Assert.assertTrue(putBitmap(sketch, "image2", 2))
            Assert.assertEquals("3MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))

            Assert.assertTrue(putBitmap(sketch, "image3", 3))
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))

            Assert.assertTrue(putBitmap(sketch, "image4", 4))
            Assert.assertEquals("10MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))

            Assert.assertTrue(putBitmap(sketch, "image5", 5))
            Assert.assertEquals("9MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertNotNull(get("image5"))

            Assert.assertTrue(putBitmap(sketch, "image6", 6))
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertNull(get("image5"))
            Assert.assertNotNull(get("image6"))

            Assert.assertTrue(putBitmap(sketch, "image7", 7))
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
        val sketch = newSketch()
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image4", 4)
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

        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image4", 4)
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
        val sketch = newSketch()
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap(sketch, "image1", 1)
            putBitmap(sketch, "image2", 2)
            putBitmap(sketch, "image3", 3)
            putBitmap(sketch, "image4", 4)
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
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertNotNull(editLock("image1"))
            Assert.assertNotNull(editLock("image2"))
            Assert.assertNotNull(editLock("image3"))
            Assert.assertNotNull(editLock("image4"))
        }
    }

    @Test
    fun testToString() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(maxSize=10MB)", toString())
        }

        LruMemoryCache(100L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(maxSize=100MB)", toString())
        }
    }

    private fun LruMemoryCache.putBitmap(sketch: Sketch, imageUri: String, sizeMb: Int): Boolean {
        val bytes = sizeMb * 1024 * 1024
        val pixelCount = bytes / 4
        val width = 10
        val height = pixelCount / width
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        return put(
            imageUri, CountBitmap(
                bitmap = bitmap,
                imageUri = imageUri,
                requestKey = imageUri,
                requestCacheKey = imageUri,
                imageInfo = ImageInfo(width, height, "image/jpeg"),
                imageExifOrientation = 0,
                transformedList = null,
                logger = sketch.logger,
                bitmapPool = sketch.bitmapPool
            )
        )
    }
}