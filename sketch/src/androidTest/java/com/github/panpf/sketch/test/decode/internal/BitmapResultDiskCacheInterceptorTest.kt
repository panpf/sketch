package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.BitmapDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheInterceptor
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapResultDiskCacheInterceptorTest {

    @Test
    fun testIntercept() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val interceptors = listOf(BitmapResultDiskCacheInterceptor(), BitmapDecodeEngineInterceptor())
        val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg")) {
            resizeSize(500, 500)
        }
        val requestExtras = RequestExtras()
        val chain =
            BitmapDecodeInterceptorChain(interceptors, 0, sketch, loadRequest, requestExtras, null)

        sketch.diskCache.clear()

        val result = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(323, result.bitmap.width)
        Assert.assertEquals(484, result.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.exifOrientation)
        Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
        Assert.assertEquals("InSampledTransformed(4)", result.transformedList?.joinToString())

        val result1 = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(323, result1.bitmap.width)
        Assert.assertEquals(484, result1.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result1.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.exifOrientation)
        Assert.assertEquals(DataFrom.RESULT_DISK_CACHE, result1.dataFrom)
        Assert.assertEquals("InSampledTransformed(4)", result.transformedList?.joinToString())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "BitmapResultDiskCacheInterceptor",
            BitmapResultDiskCacheInterceptor().toString()
        )
    }
}