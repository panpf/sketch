/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.core.common.test.decode.internal

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeInterceptor.Chain
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.DecodeInterceptorChain
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createImage
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DecodeInterceptorChainTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestBitmapDecoderInterceptor1(this),
                TestBitmapDecoderInterceptor2(this),
                TestBitmapDecoderInterceptor3(this)
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = DecodeInterceptorChain(
                sketch, request, request.toRequestContext(sketch), null, interceptors, 0
            )
            chain.proceed().getOrThrow()
        }.apply {
            assertEquals(
                expected = listOf(
                    "TestDecoderInterceptor1",
                    "TestDecoderInterceptor2",
                    "TestDecoderInterceptor3",
                ),
                actual = this
            )
        }

        mutableListOf<String>().apply {
            val interceptors = listOf(
                TestBitmapDecoderInterceptor2(this),
                TestBitmapDecoderInterceptor1(this),
                TestBitmapDecoderInterceptor3(this),
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = DecodeInterceptorChain(
                sketch, request, request.toRequestContext(sketch), null, interceptors, 0
            )
            chain.proceed().getOrThrow()
        }.apply {
            assertEquals(
                listOf(
                    "TestDecoderInterceptor2",
                    "TestDecoderInterceptor1",
                    "TestDecoderInterceptor3",
                ), this
            )
        }
    }

    private class TestBitmapDecoderInterceptor1(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecoderInterceptor1")
            return chain.proceed()
        }

        override fun toString(): String {
            return "TestDecoderInterceptor1(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecoderInterceptor2(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecoderInterceptor2")
            return chain.proceed()
        }

        override fun toString(): String {
            return "TestDecoderInterceptor2(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecoderInterceptor3(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecoderInterceptor3")
            return Result.success(
                DecodeResult(
                    image = createImage(12, 45),
                    imageInfo = ImageInfo(12, 45, "image/jpeg"),
                    dataFrom = LOCAL,
                    resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
                    transformeds = null,
                    extras = null,
                )
            )
        }

        override fun toString(): String {
            return "TestDecoderInterceptor3(sortWeight=$sortWeight)"
        }
    }
}