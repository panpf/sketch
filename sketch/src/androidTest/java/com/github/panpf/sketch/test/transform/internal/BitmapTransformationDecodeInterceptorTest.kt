package com.github.panpf.sketch.test.transform.internal

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.CircleCropTransformed
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapTransformationDecodeInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndNewSketch()
        val interceptors =
            listOf(BitmapEngineDecodeInterceptor())
        val requestContext = RequestContext()

        runBlocking {
            val request = LoadRequest(context, newAssetUri("sample.jpeg"))
            val chain = BitmapDecodeInterceptorChain(
                sketch, request, requestContext, null, interceptors, 0
            )
            BitmapTransformationDecodeInterceptor().intercept(chain)
        }.apply {
            Assert.assertEquals(Size(1291, 1936), bitmap.size)
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                bitmap.corners()
            )
            Assert.assertNull(transformedList)
        }

        runBlocking {
            val request = LoadRequest(context, newAssetUri("sample.jpeg")) {
                transformations(CircleCropTransformation())
            }
            val chain = BitmapDecodeInterceptorChain(
                sketch, request, requestContext, null, interceptors, 0
            )
            BitmapTransformationDecodeInterceptor().intercept(chain)
        }.apply {
            Assert.assertEquals(Size(1291, 1291), bitmap.size)
            Assert.assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                bitmap.corners()
            )
            Assert.assertEquals(listOf(CircleCropTransformed(CENTER_CROP)), transformedList)
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "BitmapTransformationDecodeInterceptor",
            BitmapTransformationDecodeInterceptor().toString()
        )
    }
}