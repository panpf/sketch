package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.BitmapDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapDecodeInterceptorChainTest {

    @Test
    fun testIntercept() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val interceptors = listOf(BitmapDecodeEngineInterceptor())
        val loadRequest = LoadRequest(newAssetUri("sample.jpeg"))
        val requestExtras = RequestExtras()
        val chain =
            BitmapDecodeInterceptorChain(interceptors, 0, sketch, loadRequest, requestExtras, null)
        val result = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(1291, result.bitmap.width)
        Assert.assertEquals(1936, result.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.exifOrientation)
        Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
        Assert.assertNull(result.transformedList)
    }
}