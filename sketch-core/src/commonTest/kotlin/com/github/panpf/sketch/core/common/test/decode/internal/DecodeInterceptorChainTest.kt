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
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DecodeInterceptorChainTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapDecodeInterceptor1(historyList),
                TestBitmapDecodeInterceptor2(historyList),
                TestBitmapDecodeInterceptor3(historyList)
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = DecodeInterceptorChain(
                requestContext = request.toRequestContext(sketch),
                fetchResult = null,
                interceptors = interceptors,
                index = 0
            )
            chain.proceed().getOrThrow()
            assertEquals(
                expected = listOf(
                    "TestDecodeInterceptor1",
                    "TestDecodeInterceptor2",
                    "TestDecodeInterceptor3",
                ),
                actual = historyList
            )
        }

        runBlock {
            val historyList = mutableListOf<String>()
            val interceptors = listOf(
                TestBitmapDecodeInterceptor2(historyList),
                TestBitmapDecodeInterceptor1(historyList),
                TestBitmapDecodeInterceptor3(historyList),
            )
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val chain = DecodeInterceptorChain(
                requestContext = request.toRequestContext(sketch),
                fetchResult = null,
                interceptors = interceptors,
                index = 0
            )
            chain.proceed().getOrThrow()
            assertEquals(
                expected = listOf(
                    "TestDecodeInterceptor2",
                    "TestDecodeInterceptor1",
                    "TestDecodeInterceptor3",
                ),
                actual = historyList
            )
        }
    }

    private class TestBitmapDecodeInterceptor1(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecodeInterceptor1")
            return chain.proceed()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestDecodeInterceptor1(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecodeInterceptor2(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecodeInterceptor2")
            return chain.proceed()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestDecodeInterceptor2(sortWeight=$sortWeight)"
        }
    }

    private class TestBitmapDecodeInterceptor3(val historyList: MutableList<String>) :
        DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<DecodeResult> {
            historyList.add("TestDecodeInterceptor3")
            return Result.success(
                DecodeResult(
                    image = createBitmapImage(12, 45),
                    imageInfo = ImageInfo(12, 45, "image/jpeg"),
                    dataFrom = LOCAL,
                    resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
                    transformeds = null,
                    extras = null,
                )
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestDecodeInterceptor3(sortWeight=$sortWeight)"
        }
    }
}