package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.MemoryCacheHelper
import com.github.panpf.sketch.decode.internal.MemoryCacheKeys
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class MemoryCacheHelperTest {

    @Test
    fun testMemoryCacheKeys() {
        val context = getTestContext()
        val request = DisplayRequest(context, "http://sample.com/sample.jpeg")
        MemoryCacheKeys(request).apply {
            Assert.assertEquals(request.cacheKey, cacheKey)
            Assert.assertEquals(request.cacheKey, lockKey)
        }
    }

    @Test
    fun testRead() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        sketch.memoryCache.clear()

        // Is there really no
        val helper = MemoryCacheHelper(sketch, request)
        Assert.assertNull(helper.read())

        Assert.assertNull(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                depth(Depth.LOCAL)
            }).read()
        )

        // There are the
        val countBitmap = CountBitmap(
            bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            imageUri = request.uriString,
            requestKey = request.key,
            requestCacheKey = request.cacheKey,
            imageInfo = ImageInfo(1291, 1936, "image/jpeg"),
            imageExifOrientation = 0,
            transformedList = null,
            logger = sketch.logger,
            bitmapPool = sketch.bitmapPool,
        )
        helper.write(countBitmap)
        Assert.assertNotNull(helper.read())
        Assert.assertNotNull(helper.read())

        Assert.assertNotNull(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            }).read()
        )
        Assert.assertNotNull(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(READ_ONLY)
            }).read()
        )
        Assert.assertNull(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(WRITE_ONLY)
            }).read()
        )
    }

    @Test
    fun testWrite() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        val countBitmap: () -> CountBitmap = {
            CountBitmap(
                bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                imageUri = request.uriString,
                requestKey = request.key,
                requestCacheKey = request.cacheKey,
                imageInfo = ImageInfo(1291, 1936, "image/jpeg"),
                imageExifOrientation = 0,
                transformedList = null,
                logger = sketch.logger,
                bitmapPool = sketch.bitmapPool,
            )
        }

        sketch.memoryCache.clear()
        Assert.assertNull(MemoryCacheHelper(sketch, request).read())
        Assert.assertTrue(
            MemoryCacheHelper(sketch, request).write(countBitmap())
        )
        Assert.assertNotNull(MemoryCacheHelper(sketch, request).read())

        Assert.assertFalse(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            }).write(countBitmap())
        )
        Assert.assertNotNull(MemoryCacheHelper(sketch, request).read())

        sketch.memoryCache.clear()
        Assert.assertNull(MemoryCacheHelper(sketch, request).read())
        Assert.assertTrue(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            }).write(countBitmap())
        )
        Assert.assertNotNull(MemoryCacheHelper(sketch, request).read())

        sketch.memoryCache.clear()
        Assert.assertNull(MemoryCacheHelper(sketch, request).read())
        Assert.assertFalse(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(READ_ONLY)
            }).write(countBitmap())
        )
        Assert.assertNull(MemoryCacheHelper(sketch, request).read())

        sketch.memoryCache.clear()
        Assert.assertNull(MemoryCacheHelper(sketch, request).read())
        Assert.assertTrue(
            MemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(WRITE_ONLY)
            }).write(countBitmap())
        )
        Assert.assertNotNull(MemoryCacheHelper(sketch, request).read())
    }
}