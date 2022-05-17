package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheDecodeInterceptor
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.test.utils.getContextAndSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapResultDiskCacheDecodeInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getContextAndSketch()
        val interceptors =
            listOf(BitmapResultDiskCacheDecodeInterceptor(), BitmapEngineDecodeInterceptor())
        val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg")) {
            resize(500, 500, LESS_PIXELS)
        }
        val requestExtras = RequestExtras()
        val chain =
            BitmapDecodeInterceptorChain(sketch, loadRequest, requestExtras, null, interceptors, 0)

        sketch.diskCache.clear()

        val result = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(322, result.bitmap.width)
        Assert.assertEquals(484, result.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.exifOrientation)
        Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
        Assert.assertEquals(
            "InSampledTransformed(4), ResizeTransformed(500x500,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))",
            result.transformedList?.joinToString()
        )

        val result1 = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(322, result1.bitmap.width)
        Assert.assertEquals(484, result1.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result1.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.exifOrientation)
        Assert.assertEquals(DataFrom.RESULT_DISK_CACHE, result1.dataFrom)
        Assert.assertEquals(
            "InSampledTransformed(4), ResizeTransformed(500x500,Fixed(LESS_PIXELS),Fixed(CENTER_CROP))",
            result.transformedList?.joinToString()
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "BitmapResultDiskCacheDecodeInterceptor",
            BitmapResultDiskCacheDecodeInterceptor().toString()
        )
    }
}