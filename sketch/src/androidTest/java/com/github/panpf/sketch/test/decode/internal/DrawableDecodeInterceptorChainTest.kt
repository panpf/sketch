/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

        override val key: String? = null

        override suspend fun intercept(chain: Chain): DrawableDecodeResult {
            historyList.add("TestDrawableDecoderInterceptor1")
            return chain.proceed()
        }
    }

    private class TestDrawableDecoderInterceptor2(val historyList: MutableList<String>) :
        DrawableDecodeInterceptor {

        override val key: String? = null

        override suspend fun intercept(chain: Chain): DrawableDecodeResult {
            historyList.add("TestDrawableDecoderInterceptor2")
            return chain.proceed()
        }
    }

    private class TestDrawableDecoderInterceptor3(val historyList: MutableList<String>) :
        DrawableDecodeInterceptor {

        override val key: String? = null

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