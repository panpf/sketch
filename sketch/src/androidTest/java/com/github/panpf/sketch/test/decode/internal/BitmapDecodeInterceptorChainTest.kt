package com.github.panpf.sketch.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapDecodeInterceptorChainTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndNewSketch()

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestBitmapDecoderInterceptor1(this),
                TestBitmapDecoderInterceptor2(this),
                TestBitmapDecoderInterceptor3(this)
            )
            val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg"))
            val requestContext = RequestContext(loadRequest)
            val chain = BitmapDecodeInterceptorChain(
                sketch, loadRequest, requestContext, null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }
        }.apply {
            Assert.assertEquals(
                listOf(
                    "TestBitmapDecoderInterceptor1",
                    "TestBitmapDecoderInterceptor2",
                    "TestBitmapDecoderInterceptor3",
                ), this
            )
        }

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestBitmapDecoderInterceptor2(this),
                TestBitmapDecoderInterceptor1(this),
                TestBitmapDecoderInterceptor3(this),
            )
            val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg"))
            val requestContext = RequestContext(loadRequest)
            val chain = BitmapDecodeInterceptorChain(
                sketch, loadRequest, requestContext, null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }
        }.apply {
            Assert.assertEquals(
                listOf(
                    "TestBitmapDecoderInterceptor2",
                    "TestBitmapDecoderInterceptor1",
                    "TestBitmapDecoderInterceptor3",
                ), this
            )
        }
    }

    private class TestBitmapDecoderInterceptor1(val historyList: MutableList<String>) :
        BitmapDecodeInterceptor {
        override suspend fun intercept(chain: Chain): BitmapDecodeResult {
            historyList.add("TestBitmapDecoderInterceptor1")
            return chain.proceed()
        }
    }

    private class TestBitmapDecoderInterceptor2(val historyList: MutableList<String>) :
        BitmapDecodeInterceptor {
        override suspend fun intercept(chain: Chain): BitmapDecodeResult {
            historyList.add("TestBitmapDecoderInterceptor2")
            return chain.proceed()
        }
    }

    private class TestBitmapDecoderInterceptor3(val historyList: MutableList<String>) :
        BitmapDecodeInterceptor {
        override suspend fun intercept(chain: Chain): BitmapDecodeResult {
            historyList.add("TestBitmapDecoderInterceptor3")
            return BitmapDecodeResult(
                bitmap = Bitmap.createBitmap(12, 45, RGB_565),
                imageInfo = ImageInfo(12, 45, "image/jpeg", 0),
                dataFrom = LOCAL,
                transformedList = null
            )
        }
    }
}