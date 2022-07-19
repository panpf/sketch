package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ResultCacheHelper
import com.github.panpf.sketch.decode.internal.ResultCacheKeys
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultCacheHelperTest {

    @Test
    fun testResultCacheKeys() {
        val context = getTestContext()
        val request = LoadRequest(context, "http://sample.com/sample.jpeg")
        ResultCacheKeys(request).apply {
            Assert.assertEquals("${request.cacheKey}_result_data", bitmapDataDiskCacheKey)
            Assert.assertEquals("${request.cacheKey}_result_meta", bitmapMetaDiskCacheKey)
            Assert.assertEquals("${request.cacheKey}_result", lockKey)
        }
    }

    @Test
    fun testRead() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.resultDiskCache.clear()

        // Is there really no
        val helper = ResultCacheHelper(sketch, request)
        Assert.assertNull(helper.read())

        // There are the
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            listOf(createInSampledTransformed(4))
        )
        helper.write(bitmapDecodeResult)
        Assert.assertNotNull(helper.read())
        helper.read()!!.apply {
            Assert.assertTrue(bitmap.isMutable)
        }

        Assert.assertNotNull(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            }).read()
        )
        Assert.assertNotNull(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            }).read()
        )
        Assert.assertNull(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            }).read()
        )
    }

    @Test
    fun testWrite() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.resultDiskCache.clear()

        Assert.assertNull(ResultCacheHelper(sketch, request).read())

        // transformedList empty
        val bitmapDecodeResult = BitmapDecodeResult(
            bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            imageInfo = ImageInfo(1291, 1936, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = DataFrom.LOCAL,
            transformedList = null
        )
        Assert.assertTrue(
            ResultCacheHelper(sketch, request).write(bitmapDecodeResult)
        )
        Assert.assertNotNull(ResultCacheHelper(sketch, request).read())

        val bitmapDecodeResult1 = BitmapDecodeResult(
            bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            imageInfo = ImageInfo(1291, 1936, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = DataFrom.LOCAL,
            transformedList = listOf(createInSampledTransformed(4))
        )
        Assert.assertTrue(
            ResultCacheHelper(sketch, request).write(bitmapDecodeResult1)
        )
        Assert.assertNotNull(ResultCacheHelper(sketch, request).read())

        Assert.assertTrue(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            }).write(bitmapDecodeResult1)
        )
        Assert.assertFalse(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            }).write(bitmapDecodeResult1)
        )
        Assert.assertTrue(
            ResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            }).write(bitmapDecodeResult1)
        )

        Assert.assertNotNull(ResultCacheHelper(sketch, request).read())
    }
}