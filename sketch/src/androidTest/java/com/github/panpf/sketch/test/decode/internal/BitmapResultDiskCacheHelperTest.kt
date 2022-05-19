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
import com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheHelper.MetaData
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.internal.newBitmapResultDiskCacheHelper
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeTransformed
import com.github.panpf.sketch.test.utils.getContextAndSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapResultDiskCacheHelperTest {

    @Test
    fun testNewBitmapResultCacheHelper() {
        val (context, sketch) = getContextAndSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        Assert.assertNotNull(
            newBitmapResultDiskCacheHelper(sketch, request)
        )
        Assert.assertNotNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })
        )
        Assert.assertNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(DISABLED)
            })
        )
        Assert.assertNotNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })
        )
        Assert.assertNotNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })
        )
    }

    @Test
    fun testRead() {
        val (context, sketch) = getContextAndSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.diskCache.clear()

        // Is there really no
        val helper = newBitmapResultDiskCacheHelper(sketch, request)!!
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
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })!!.read()
        )
        Assert.assertNotNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })!!.read()
        )
        Assert.assertNull(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })!!.read()
        )
    }

    @Test
    fun testWrite() {
        val (context, sketch) = getContextAndSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))

        sketch.diskCache.clear()

        Assert.assertNull(newBitmapResultDiskCacheHelper(sketch, request)!!.read())

        // transformedList empty
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            null
        )
        Assert.assertFalse(
            newBitmapResultDiskCacheHelper(sketch, request)!!.write(bitmapDecodeResult)
        )
        Assert.assertNull(newBitmapResultDiskCacheHelper(sketch, request)!!.read())

        val bitmapDecodeResult1 = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            DataFrom.LOCAL,
            listOf(InSampledTransformed(4))
        )
        Assert.assertTrue(
            newBitmapResultDiskCacheHelper(sketch, request)!!.write(bitmapDecodeResult1)
        )
        Assert.assertNotNull(newBitmapResultDiskCacheHelper(sketch, request)!!.read())

        Assert.assertTrue(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(ENABLED)
            })!!.write(bitmapDecodeResult1)
        )
        Assert.assertFalse(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(READ_ONLY)
            })!!.write(bitmapDecodeResult1)
        )
        Assert.assertTrue(
            newBitmapResultDiskCacheHelper(sketch, request.newLoadRequest {
                resultCachePolicy(WRITE_ONLY)
            })!!.write(bitmapDecodeResult1)
        )

        Assert.assertNotNull(newBitmapResultDiskCacheHelper(sketch, request)!!.read())
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