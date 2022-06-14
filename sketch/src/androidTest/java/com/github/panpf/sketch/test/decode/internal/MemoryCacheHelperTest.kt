package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.newMemoryCacheHelper
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryCacheHelperTest {

    @Test
    fun testNewBitmapMemoryCacheHelper() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request)
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            })
        )
        Assert.assertNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(DISABLED)
            })
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(READ_ONLY)
            })
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(WRITE_ONLY)
            })
        )
    }

    @Test
    fun testRead() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        sketch.memoryCache.clear()

        // Is there really no
        val helper = newMemoryCacheHelper(sketch, request)!!
        Assert.assertNull(helper.read())

        Assert.assertNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                depth(Depth.LOCAL)
            })!!.read()
        )

        // There are the
        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            LOCAL,
            null
        )
        helper.write(bitmapDecodeResult)
        Assert.assertNotNull(helper.read())
        Assert.assertNotNull(helper.read())

        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            })!!.read()
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(READ_ONLY)
            })!!.read()
        )
        Assert.assertNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(WRITE_ONLY)
            })!!.read()
        )
    }

    @Test
    fun testWrite() {
        val (context, sketch) = getTestContextAndNewSketch()
        val imageView = ImageView(context)
        val request = DisplayRequest(imageView, newAssetUri("sample.jpeg"))

        sketch.memoryCache.clear()

        Assert.assertNull(newMemoryCacheHelper(sketch, request)!!.read())

        val bitmapDecodeResult = BitmapDecodeResult(
            Bitmap.createBitmap(100, 100, ARGB_8888),
            ImageInfo(1291, 1936, "image/jpeg"),
            0,
            LOCAL,
            null
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request)!!.write(bitmapDecodeResult)
        )

        Assert.assertNotNull(newMemoryCacheHelper(sketch, request)!!.read())

        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(ENABLED)
            })!!.write(bitmapDecodeResult)
        )
        Assert.assertNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(READ_ONLY)
            })!!.write(bitmapDecodeResult)
        )
        Assert.assertNotNull(
            newMemoryCacheHelper(sketch, request.newDisplayRequest {
                memoryCachePolicy(WRITE_ONLY)
            })!!.write(bitmapDecodeResult)
        )

        Assert.assertNotNull(newMemoryCacheHelper(sketch, request)!!.read())
    }
}