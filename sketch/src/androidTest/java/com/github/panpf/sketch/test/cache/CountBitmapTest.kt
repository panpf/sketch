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
package com.github.panpf.sketch.test.cache

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.createRotateTransformed
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
    fun testRequestKey() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100, requestKey = "requestKey1").apply {
            Assert.assertEquals("requestKey1", requestKey)
            Assert.assertEquals("requestKey1", requestCacheKey)
        }

        createCountBitmap(sketch, "image2", 100, 100, requestKey = "requestKey2").apply {
            Assert.assertEquals("requestKey2", requestKey)
            Assert.assertEquals("requestKey2", requestCacheKey)
        }
    }

    @Test
    fun testImageUri() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals("image1", imageUri)
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            Assert.assertEquals("image2", imageUri)
        }
    }

    @Test
    fun testImageInfo() {
        val sketch = newSketch()

        createCountBitmap(
            sketch,
            "image1",
            100,
            100,
            imageInfo = ImageInfo(100, 100, "image/png", 0)
        ).apply {
            Assert.assertEquals(ImageInfo(100, 100, "image/png", 0), imageInfo)
        }

        createCountBitmap(
            sketch,
            "image2",
            100,
            150,
            imageInfo = ImageInfo(100, 150, "image/gif", 0)
        ).apply {
            Assert.assertEquals(ImageInfo(100, 150, "image/gif", 0), imageInfo)
        }
    }

    @Test
    fun testTransformedList() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertNull(transformedList)
        }

        createCountBitmap(
            sketch,
            "image2",
            100,
            150,
            transformedList = listOf(createInSampledTransformed(4), createRotateTransformed(40))
        ).apply {
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(40)).toString(),
                transformedList.toString()
            )
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
    fun testInfo() {
        val sketch = newSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals(
                "CountBitmap(Bitmap(100x100,ARGB_8888,@${this.bitmap!!.toHexString()}),ImageInfo(100x100,'image/jpeg',UNDEFINED))",
                info
            )
        }

        createCountBitmap(sketch, "image1", 200, 100).apply {
            Assert.assertEquals(
                "CountBitmap(Bitmap(200x100,ARGB_8888,@${this.bitmap!!.toHexString()}),ImageInfo(200x100,'image/jpeg',UNDEFINED))",
                info
            )
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
        imageUri: String,
        width: Int,
        height: Int,
        requestKey: String = imageUri,
        imageInfo: ImageInfo = ImageInfo(width, height, "image/jpeg", 0),
        transformedList: List<String>? = null
    ): CountBitmap = CountBitmap(
        sketch = sketch,
        bitmap = Bitmap.createBitmap(width, height, ARGB_8888),
        imageUri = imageUri,
        requestKey = requestKey,
        requestCacheKey = requestKey,
        imageInfo = imageInfo,
        transformedList = transformedList,
    )
}