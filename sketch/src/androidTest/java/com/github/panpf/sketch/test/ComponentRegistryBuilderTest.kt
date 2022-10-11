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
package com.github.panpf.sketch.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineBitmapDecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineDrawableDecodeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
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
    fun testAddBitmapDecoder() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(bitmapDecoderFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addBitmapDecoder(TestBitmapDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    XmlDrawableBitmapDecoder.Factory(),
                    DefaultBitmapDecoder.Factory(),
                    TestBitmapDecoder.Factory(),
                ),
                bitmapDecoderFactoryList
            )
        }
    }

    @Test
    fun testAddDrawableDecoder() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(drawableDecoderFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addDrawableDecoder(TestDrawableDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    DefaultDrawableDecoder.Factory(),
                    TestDrawableDecoder.Factory()
                ),
                drawableDecoderFactoryList
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
    fun testAddBitmapDecodeInterceptor() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor(95))
            assertThrow(java.lang.IllegalArgumentException::class) {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    BitmapTransformationDecodeInterceptor(),
                    TestBitmapDecodeInterceptor(95),
                    EngineBitmapDecodeInterceptor(),
                ),
                bitmapDecodeInterceptorList
            )
        }
    }

    @Test
    fun testAddDrawableDecodeInterceptor() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
            addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(90))
            addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(95))
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    TestDrawableDecodeInterceptor(90),
                    TestDrawableDecodeInterceptor(95),
                    EngineDrawableDecodeInterceptor(),
                ),
                drawableDecodeInterceptorList
            )
        }
    }
}