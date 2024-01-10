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
package com.github.panpf.sketch.core.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentRegistryBuilderTest {

    @Test
    fun testAddFetcher() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(fetcherFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    HttpUriFetcher.Factory(),
                    Base64UriFetcher.Factory(),
                    ResourceUriFetcher.Factory(),
                    TestFetcher.Factory(),
                ),
                fetcherFactoryList
            )
        }
    }

    @Test
    fun testAddDecoder() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(decoderFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecoder(DrawableDecoder.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    DrawableDecoder.Factory(),
                    BitmapFactoryDecoder.Factory(),
                    TestDecoder.Factory(),
                ),
                decoderFactoryList
            )
        }
    }

    @Test
    fun testAddRequestInterceptor() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(requestInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(TestRequestInterceptor(95))

            assertThrow(java.lang.IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    MemoryCacheRequestInterceptor(),
                    TestRequestInterceptor(95),
                    EngineRequestInterceptor()
                ),
                requestInterceptorList
            )
        }
    }

    @Test
    fun testAddDecodeInterceptor() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor(95))
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    TransformationDecodeInterceptor(),
                    TestDecodeInterceptor(95),
                    EngineDecodeInterceptor(),
                ),
                decodeInterceptorList
            )
        }
    }
}