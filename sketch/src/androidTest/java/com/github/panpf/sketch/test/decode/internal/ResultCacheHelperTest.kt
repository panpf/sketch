package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.ResultCacheHelper.MetaData
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.internal.newResultCacheHelper
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.test.utils.getContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultCacheHelperTest {

    @Test
    fun testNewBitmapResultCacheHelper() {
        val (context, sketch) = getContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        Assert.assertNotNull(
            newResultCacheHelper(sketch, request)
        )
        Assert.assertNotNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })
        )
        Assert.assertNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(DISABLED)
            })
        )
        Assert.assertNotNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })
        )
        Assert.assertNotNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })
        )
    }

    @Test
    fun testRead() {
        val (context, sketch) = getContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.diskCache.clear()

        // Is there really no
        val helper = newResultCacheHelper(sketch, request)!!
        Assert.assertNull(helper.read())

        // There are the
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            listOf(InSampledTransformed(4))
        )
        helper.write(bitmapDecodeResult)
        Assert.assertNotNull(helper.read())
        Assert.assertNotNull(helper.read())

        Assert.assertNotNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })!!.read()
        )
        Assert.assertNotNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })!!.read()
        )
        Assert.assertNull(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })!!.read()
        )
    }

    @Test
    fun testWrite() {
        val (context, sketch) = getContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.diskCache.clear()

        Assert.assertNull(newResultCacheHelper(sketch, request)!!.read())

        // transformedList empty
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            null
        )
        Assert.assertFalse(
            newResultCacheHelper(sketch, request)!!.write(bitmapDecodeResult)
        )
        Assert.assertNull(newResultCacheHelper(sketch, request)!!.read())

        val bitmapDecodeResult1 = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            listOf(InSampledTransformed(4))
        )
        Assert.assertTrue(
            newResultCacheHelper(sketch, request)!!.write(bitmapDecodeResult1)
        )
        Assert.assertNotNull(newResultCacheHelper(sketch, request)!!.read())

        Assert.assertTrue(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })!!.write(bitmapDecodeResult1)
        )
        Assert.assertFalse(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })!!.write(bitmapDecodeResult1)
        )
        Assert.assertTrue(
            newResultCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })!!.write(bitmapDecodeResult1)
        )

        Assert.assertNotNull(newResultCacheHelper(sketch, request)!!.read())
    }

    @Test
    fun testMeatDataJSON() {
        val serializer = MetaData.Serializer()
        val metaData = MetaData(
            imageInfo = ImageInfo(width = 570, height = 340, mimeType = "image/png"),
            exifOrientation = ExifInterface.ORIENTATION_ROTATE_180,
            transformedList = listOf(InSampledTransformed(4), ResizeTransformed(Resize(40, 30)))
        )
        serializer.fromJson(serializer.toJson(metaData)).apply {
            Assert.assertEquals(570, imageInfo.width)
            Assert.assertEquals(340, imageInfo.height)
            Assert.assertEquals("image/png", imageInfo.mimeType)
            Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_180, exifOrientation)
            Assert.assertEquals(
                transformedList?.joinToString(),
                listOf(InSampledTransformed(4), ResizeTransformed(Resize(40, 30))).joinToString()
            )
        }
    }
}