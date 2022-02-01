package com.github.panpf.sketch.test.decode.internal

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.BitmapDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.ExifOrientationInterceptor
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
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
        val interceptors = listOf(ExifOrientationInterceptor(), BitmapDecodeEngineInterceptor())
        val loadRequest = LoadRequest(newAssetUri("sample.jpeg"))
        val chain =
            BitmapDecodeInterceptorChain(loadRequest, interceptors, 0, sketch, loadRequest, null)
        val result = runBlocking {
            chain.proceed(loadRequest)
        }
        Assert.assertEquals(1291, result.bitmap.width)
        Assert.assertEquals(1936, result.bitmap.height)
        Assert.assertEquals(
            "ImageInfo(mimeType='image/jpeg',width=1291,height=1936,exifOrientation=NORMAL)",
            result.imageInfo.toString()
        )
        Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
        Assert.assertNull(result.transformedList)
    }
}