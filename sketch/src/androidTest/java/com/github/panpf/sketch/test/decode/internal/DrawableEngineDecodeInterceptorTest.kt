package com.github.panpf.sketch.test.decode.internal

import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableEngineDecodeInterceptor
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableEngineDecodeInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndNewSketch()
        val interceptors = listOf(DrawableEngineDecodeInterceptor())
        val loadRequest = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val requestContext = RequestContext(loadRequest)
        val chain = DrawableDecodeInterceptorChain(
            sketch = sketch,
            request = loadRequest,
            requestContext = requestContext,
            fetchResult = null,
            interceptors = interceptors,
            index = 0
        )
        val result = runBlocking {
            chain.proceed()
        }
        Assert.assertEquals(1291, result.drawable.intrinsicWidth)
        Assert.assertEquals(1936, result.drawable.intrinsicHeight)
        Assert.assertEquals(
            "ImageInfo(width=1291, height=1936, mimeType='image/jpeg')",
            result.imageInfo.toString()
        )
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, result.imageExifOrientation)
        Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
        Assert.assertNull(result.transformedList)
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "DrawableEngineDecodeInterceptor",
            DrawableEngineDecodeInterceptor().toString()
        )
    }
}