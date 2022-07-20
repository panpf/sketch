package com.github.panpf.sketch.test.decode.internal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableDecodeInterceptorChainTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndNewSketch()

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestDrawableDecoderInterceptor1(this),
                TestDrawableDecoderInterceptor2(this),
                TestDrawableDecoderInterceptor3(this)
            )
            val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg"))
            val requestContext = RequestContext(loadRequest)
            val chain = DrawableDecodeInterceptorChain(
                sketch, loadRequest, requestContext, null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }
        }.apply {
            Assert.assertEquals(
                listOf(
                    "TestDrawableDecoderInterceptor1",
                    "TestDrawableDecoderInterceptor2",
                    "TestDrawableDecoderInterceptor3",
                ), this
            )
        }

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestDrawableDecoderInterceptor2(this),
                TestDrawableDecoderInterceptor1(this),
                TestDrawableDecoderInterceptor3(this),
            )
            val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg"))
            val requestContext = RequestContext(loadRequest)
            val chain = DrawableDecodeInterceptorChain(
                sketch, loadRequest, requestContext, null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }
        }.apply {
            Assert.assertEquals(
                listOf(
                    "TestDrawableDecoderInterceptor2",
                    "TestDrawableDecoderInterceptor1",
                    "TestDrawableDecoderInterceptor3",
                ), this
            )
        }
    }

    private class TestDrawableDecoderInterceptor1(val historyList: MutableList<String>) :
        DrawableDecodeInterceptor {
        override suspend fun intercept(chain: Chain): DrawableDecodeResult {
            historyList.add("TestDrawableDecoderInterceptor1")
            return chain.proceed()
        }
    }

    private class TestDrawableDecoderInterceptor2(val historyList: MutableList<String>) :
        DrawableDecodeInterceptor {
        override suspend fun intercept(chain: Chain): DrawableDecodeResult {
            historyList.add("TestDrawableDecoderInterceptor2")
            return chain.proceed()
        }
    }

    private class TestDrawableDecoderInterceptor3(val historyList: MutableList<String>) :
        DrawableDecodeInterceptor {
        override suspend fun intercept(chain: Chain): DrawableDecodeResult {
            historyList.add("TestDrawableDecoderInterceptor3")
            return DrawableDecodeResult(
                drawable = ColorDrawable(Color.BLUE),
                imageInfo = ImageInfo(12, 45, "image/jpeg", 0),
                dataFrom = LOCAL,
                transformedList = null
            )
        }
    }
}