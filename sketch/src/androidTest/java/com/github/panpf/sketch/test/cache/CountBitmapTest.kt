package com.github.panpf.sketch.test.cache

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.RotateTransformed
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
        }

        createCountBitmap(sketch, "image2", 100, 100, requestKey = "requestKey2").apply {
            Assert.assertEquals("requestKey2", requestKey)
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
            imageInfo = ImageInfo(100, 100, "image/png")
        ).apply {
            Assert.assertEquals(ImageInfo(100, 100, "image/png"), imageInfo)
        }

        createCountBitmap(
            sketch,
            "image2",
            100,
            150,
            imageInfo = ImageInfo(100, 150, "image/gif")
        ).apply {
            Assert.assertEquals(ImageInfo(100, 150, "image/gif"), imageInfo)
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
            transformedList = listOf(InSampledTransformed(4), RotateTransformed(40))
        ).apply {
            Assert.assertEquals(
                listOf(InSampledTransformed(4), RotateTransformed(40)).toString(),
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
            Assert.assertNull(bitmap)
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
                "CountBitmap(ImageInfo=100x100/image/jpeg/UNDEFINED,BitmapInfo=100x100/ARGB_8888/39.06KB/${this.bitmap!!.toHexString()})",
                info
            )
        }

        createCountBitmap(sketch, "image1", 200, 100).apply {
            Assert.assertEquals(
                "CountBitmap(ImageInfo=200x100/image/jpeg/UNDEFINED,BitmapInfo=200x100/ARGB_8888/78.13KB/${this.bitmap!!.toHexString()})",
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
                Assert.assertTrue(isRecycled)
                Assert.assertEquals(0, getPendingCount())

                setIsPending(false)
                Assert.assertTrue(isRecycled)
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
        imageInfo: ImageInfo = ImageInfo(width, height, "image/jpeg"),
        transformedList: List<Transformed>? = null
    ): CountBitmap {
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        return CountBitmap(
            bitmap = bitmap,
            imageUri = imageUri,
            requestKey = requestKey,
            requestCacheKey = requestKey,
            imageInfo = imageInfo,
            imageExifOrientation = 0,
            transformedList = transformedList,
            logger = sketch.logger,
            bitmapPool = sketch.bitmapPool
        )
    }
}