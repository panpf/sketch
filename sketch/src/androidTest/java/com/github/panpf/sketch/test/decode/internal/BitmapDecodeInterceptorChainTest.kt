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
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.toRequestContext
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
            val chain = BitmapDecodeInterceptorChain(
                sketch, loadRequest, loadRequest.toRequestContext(), null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }.getOrThrow()
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
            val chain = BitmapDecodeInterceptorChain(
                sketch, loadRequest, loadRequest.toRequestContext(), null, interceptors, 0
            )
            runBlocking {
                chain.proceed()
            }.getOrThrow()
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

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<BitmapDecodeResult> {
            historyList.add("TestBitmapDecoderInterceptor1")
            return chain.proceed()
        }

        override fun toString(): String {
            return "TestBitmapDecoderInterceptor1(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecoderInterceptor2(val historyList: MutableList<String>) :
        BitmapDecodeInterceptor {

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<BitmapDecodeResult> {
            historyList.add("TestBitmapDecoderInterceptor2")
            return chain.proceed()
        }

        override fun toString(): String {
            return "TestBitmapDecoderInterceptor2(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecoderInterceptor3(val historyList: MutableList<String>) :
        BitmapDecodeInterceptor {

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<BitmapDecodeResult> {
            historyList.add("TestBitmapDecoderInterceptor3")
            return Result.success(
                BitmapDecodeResult(
                    bitmap = Bitmap.createBitmap(12, 45, RGB_565),
                    imageInfo = ImageInfo(12, 45, "image/jpeg", 0),
                    dataFrom = LOCAL,
                    transformedList = null,
                    extras = null,
                )
            )
        }

        override fun toString(): String {
            return "TestBitmapDecoderInterceptor3(sortWeight=$sortWeight)"
        }
    }
}