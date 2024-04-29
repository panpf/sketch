/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.core.android.test.cache.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.LruMemoryCache
import com.github.panpf.sketch.cache.newCacheValueExtras
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
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())

            putBitmap("image1", 1)
            Assert.assertEquals("1MB", size.formatFileSize())

            putBitmap("image2", 2)
            Assert.assertEquals("3MB", size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGet() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertNull(get("image1"))
            Assert.assertTrue(putBitmap("image1", 1))
            Assert.assertNotNull(get("image1"))
            Assert.assertFalse(putBitmap("image1", 1))

            Assert.assertNull(get("image2"))
            putBitmap("image2", 2)
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

            val bigBitmapSize = (sketch.memoryCache.maxSize.toFloat() / 1024 / 1024 * 0.8f).toInt()
            Assert.assertFalse(putBitmap("image0", bigBitmapSize))
            Assert.assertEquals("0B", size.formatFileSize())

            Assert.assertTrue(putBitmap("image1", 1))
            Assert.assertEquals("1MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))

            Assert.assertTrue(putBitmap("image2", 2))
            Assert.assertEquals("3MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))

            Assert.assertTrue(putBitmap("image3", 3))
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))

            Assert.assertTrue(putBitmap("image4", 4))
            Assert.assertEquals("10MB", size.formatFileSize())
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))

            Assert.assertTrue(putBitmap("image5", 5))
            Assert.assertEquals("9MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertNotNull(get("image5"))

            Assert.assertTrue(putBitmap("image6", 6))
            Assert.assertEquals("6MB", size.formatFileSize())
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertNull(get("image5"))
            Assert.assertNotNull(get("image6"))

            Assert.assertTrue(putBitmap("image7", 7))
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
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap("image1", 1)
            putBitmap("image2", 2)
            putBitmap("image3", 3)
            putBitmap("image4", 4)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(0L)
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNull(get("image4"))
            Assert.assertEquals("0B", size.formatFileSize())
        }

        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap("image1", 1)
            putBitmap("image2", 2)
            putBitmap("image3", 3)
            putBitmap("image4", 4)
            Assert.assertNotNull(get("image1"))
            Assert.assertNotNull(get("image2"))
            Assert.assertNotNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("10MB", size.formatFileSize())

            trim(4L)
            Assert.assertNull(get("image1"))
            Assert.assertNull(get("image2"))
            Assert.assertNull(get("image3"))
            Assert.assertNotNull(get("image4"))
            Assert.assertEquals("4MB", size.formatFileSize())
        }
    }

    @Test
    fun testClear() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("0B", size.formatFileSize())
            putBitmap("image1", 1)
            putBitmap("image2", 2)
            putBitmap("image3", 3)
            putBitmap("image4", 4)
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
    fun testEqualsAndHashCode() {
        val element1 = LruMemoryCache(100)
        val element11 = LruMemoryCache(100)
        val element2 = LruMemoryCache(200)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        LruMemoryCache(10L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(10MB)", toString())
        }

        LruMemoryCache(100L * 1024 * 1024).apply {
            Assert.assertEquals("LruMemoryCache(100MB)", toString())
        }
    }

    private fun LruMemoryCache.putBitmap(imageUri: String, sizeMb: Int): Boolean {
        val bytes = sizeMb * 1024 * 1024
        val pixelCount = bytes / 4
        val width = 10
        val height = pixelCount / width
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        val newCacheValue = AndroidBitmapImageValue(
            image = bitmap.asSketchImage(),
            extras = newCacheValueExtras(
                imageInfo = ImageInfo(width, height, "image/jpeg"),
                transformedList = null,
                extras = null,
            )
        )
        return put(imageUri, newCacheValue) == 0
    }
}