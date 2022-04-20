package com.github.panpf.sketch.test.cache

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.test.contextAndSketch
import com.github.panpf.sketch.transform.RotateTransformed
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountBitmapTest {

    @Test
    fun testRequestKey() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100, requestKey = "requestKey1").apply {
            Assert.assertEquals("requestKey1", requestKey)
        }

        createCountBitmap(sketch, "image2", 100, 100, requestKey = "requestKey2").apply {
            Assert.assertEquals("requestKey2", requestKey)
        }
    }

    @Test
    fun testImageUri() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals("image1", imageUri)
        }

        createCountBitmap(sketch, "image2", 100, 100).apply {
            Assert.assertEquals("image2", imageUri)
        }
    }

    @Test
    fun testImageInfo() {
        val (context, sketch) = contextAndSketch()

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
        val (context, sketch) = contextAndSketch()

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
        val (context, sketch) = contextAndSketch()

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
                setIsPending("test", true)
                setIsPending("test", false)
            }
            Assert.assertNull(bitmap)
        }
    }

    @Test
    fun testIsRecycled() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", true)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", false)
            }
            Assert.assertTrue(isRecycled)
        }
    }

    @Test
    fun testByteCount() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertEquals(100 * 100 * 4, byteCount)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", true)
            }
            Assert.assertEquals(100 * 100 * 4, byteCount)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", false)
            }
            Assert.assertEquals(0, byteCount)
        }
    }

    @Test
    fun testInfo() {
        val (context, sketch) = contextAndSketch()

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
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", true)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", true)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", false)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsDisplayed("test", false)
            }
            Assert.assertTrue(isRecycled)
        }
    }

    @Test
    fun testSetIsCached() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)

            setIsCached("test", true)
            Assert.assertFalse(isRecycled)

            setIsCached("test", true)
            Assert.assertFalse(isRecycled)

            setIsCached("test", false)
            Assert.assertFalse(isRecycled)

            setIsCached("test", false)
            Assert.assertTrue(isRecycled)
        }
    }

    @Test
    fun testSetIsPending() {
        val (context, sketch) = contextAndSketch()

        createCountBitmap(sketch, "image1", 100, 100).apply {
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsPending("test", true)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsPending("test", true)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsPending("test", false)
            }
            Assert.assertFalse(isRecycled)

            runBlocking(Dispatchers.Main) {
                setIsPending("test", false)
            }
            Assert.assertTrue(isRecycled)
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
            initBitmap = bitmap,
            requestKey = requestKey,
            imageUri = imageUri,
            imageInfo = imageInfo,
            exifOrientation = 0,
            transformedList = transformedList,
            logger = sketch.logger,
            bitmapPool = sketch.bitmapPool
        )
    }
}