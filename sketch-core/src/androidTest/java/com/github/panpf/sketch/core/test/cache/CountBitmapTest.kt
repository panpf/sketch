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
package com.github.panpf.sketch.core.test.cache

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.core.test.newSketch
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountBitmapTest {

    @Test
    fun testCacheKey() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals("image1", cacheKey)
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            Assert.assertEquals("image2", cacheKey)
        }
    }

    @Test
    fun testBitmap() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals(100, bitmap!!.width)
            Assert.assertEquals(100, bitmap!!.height)
        }

        createCountBitmap(sketch, "image1", 120, 300).apply {
            Assert.assertEquals(120, bitmap!!.width)
            Assert.assertEquals(300, bitmap!!.height)
        }

        createCountBitmap(sketch, "image1", 120, 300).apply {
            Assert.assertNotNull(bitmap)
            runBlocking(Dispatchers.Main) {
                setIsPending(true)
                setIsPending(false)
            }
            Assert.assertNotNull(bitmap)
        }
    }

    @Test
    fun testIsRecycled() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                Assert.assertFalse(isRecycled)

                setIsDisplayed(true)
                Assert.assertFalse(isRecycled)

                setIsDisplayed(false)
                Assert.assertTrue(isRecycled)
            }
        }
    }

    @Test
    fun testByteCount() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                Assert.assertEquals(100 * 100 * 4, byteCount)

                setIsDisplayed(true)
                Assert.assertEquals(100 * 100 * 4, byteCount)

                setIsDisplayed(false)
                Assert.assertEquals(0, byteCount)
            }
        }
    }

    @Test
    fun testToString() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,0/0/0,'image1')", toString())
        }

        createCountBitmap(sketch, "image1", 200, 100).apply {
            val bitmapLogString = "Bitmap(200x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,0/0/0,'image1')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,0/0/0,'image2')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                setIsPending(true)
            }
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,1/0/0,'image2')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            setIsCached(true)
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,0/1/0,'image2')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                setIsDisplayed(true)
            }
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,0/0/1,'image2')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                setIsPending(true)
            }
            setIsCached(true)
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,1/1/0,'image2')", toString())
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            runBlocking(Dispatchers.Main) {
                setIsPending(true)
            }
            setIsCached(true)
            runBlocking(Dispatchers.Main) {
                setIsDisplayed(true)
            }
            val bitmapLogString = "Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()})"
            Assert.assertEquals("CountBitmap($bitmapLogString,1/1/1,'image2')", toString())
        }
    }

    @Test
    fun testSetIsDisplayed() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            assertThrow(IllegalStateException::class) {
                setIsDisplayed(true)
            }
            assertThrow(IllegalStateException::class) {
                getDisplayedCount()
            }

            runBlocking(Dispatchers.Main) {
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(0, getDisplayedCount())

                setIsDisplayed(true)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(1, getDisplayedCount())

                setIsDisplayed(true)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(2, getDisplayedCount())

                setIsDisplayed(false)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(1, getDisplayedCount())

                setIsDisplayed(false)
                Assert.assertTrue(isRecycled)
                Assert.assertEquals(0, getDisplayedCount())

                setIsDisplayed(false)
                Assert.assertTrue(isRecycled)
                Assert.assertEquals(0, getDisplayedCount())
            }
        }
    }

    @Test
    fun testSetIsCached() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)
            Assert.assertEquals(0, getCachedCount())

            setIsCached(true)
            Assert.assertFalse(isRecycled)
            Assert.assertEquals(1, getCachedCount())

            setIsCached(true)
            Assert.assertFalse(isRecycled)
            Assert.assertEquals(2, getCachedCount())

            setIsCached(false)
            Assert.assertFalse(isRecycled)
            Assert.assertEquals(1, getCachedCount())

            setIsCached(false)
            Assert.assertTrue(isRecycled)
            Assert.assertEquals(0, getCachedCount())

            setIsCached(false)
            Assert.assertTrue(isRecycled)
            Assert.assertEquals(0, getCachedCount())
        }
    }

    @Test
    fun testSetIsPending() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            assertThrow(IllegalStateException::class) {
                setIsPending(true)
            }
            assertThrow(IllegalStateException::class) {
                getPendingCount()
            }

            runBlocking(Dispatchers.Main) {
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(0, getPendingCount())

                setIsPending(true)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(1, getPendingCount())

                setIsPending(true)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(2, getPendingCount())

                setIsPending(false)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(1, getPendingCount())

                setIsPending(false)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(0, getPendingCount())

                setIsPending(false)
                Assert.assertFalse(isRecycled)
                Assert.assertEquals(0, getPendingCount())
            }
        }
    }

    @Test
    fun testRecycled() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)
            bitmap!!.recycle()
            assertThrow(IllegalStateException::class) {
                setIsCached(true)
            }
        }
    }

    private fun createCountBitmap(
        sketch: Sketch,
        cacheKey: String,
        width: Int,
        height: Int,
    ): CountBitmap = CountBitmap(
        cacheKey = cacheKey,
        originBitmap = Bitmap.createBitmap(width, height, ARGB_8888),
        bitmapPool = sketch.bitmapPool,
        disallowReuseBitmap = false,
    )
}