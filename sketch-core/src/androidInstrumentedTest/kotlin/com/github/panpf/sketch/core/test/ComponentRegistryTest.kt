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
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.tools4j.test.ktx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentRegistryTest {

    @Test
    fun testNewBuilder() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(DrawableDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }.newBuilder().build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }.newBuilder {
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(EngineDecodeInterceptor()),
                decodeInterceptorList
            )
        }
    }

    @Test
    fun testNewRegistry() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(DrawableDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }.newRegistry().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }.newRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(DrawableDecoder.Factory()),
                decoderFactoryList
            )
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(EngineDecodeInterceptor()),
                decodeInterceptorList
            )
        }
    }

    @Test
    fun testIsEmpty() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(isEmpty())
            Assert.assertFalse(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecoder(BitmapFactoryDecoder.Factory())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testNewFetcher() {
        val context = getTestContext()
        val sketch = newSketch()

        ComponentRegistry.Builder().build().apply {
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, MyImages.jpeg.uri))
            )
            Assert.assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            assertNoThrow {
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, MyImages.jpeg.uri))
            )
            Assert.assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }.build().apply {
            assertNoThrow {
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            }
            assertNoThrow {
                newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, MyImages.jpeg.uri))
            )
            Assert.assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }
    }

    @Test
    fun testDecoder() {
        val context = getTestContext()
        val sketch = newSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
            Assert.assertNull(
                newDecoderOrNull(requestContext, fetchResult)
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, ImageRequest(context, MyImages.jpeg.uri))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertNoThrow {
                newDecoderOrThrow(requestContext, fetchResult)
            }
            Assert.assertNotNull(
                newDecoderOrNull(requestContext, fetchResult)
            )
        }
    }

    @Test
    fun testMerged() {
        val componentRegistry = ComponentRegistry.Builder().apply {
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addRequestInterceptor(TestRequestInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher]," +
                        "decoderFactoryList=[TestDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor(sortWeight=0)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor2())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher]," +
                        "decoderFactoryList=[BitmapFactoryDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[Test2DecodeInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        Assert.assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher,HttpUriFetcher]," +
                        "decoderFactoryList=[TestDecoder,BitmapFactoryDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor(sortWeight=0),EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0),Test2DecodeInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        Assert.assertNotEquals(componentRegistry, componentRegistry2)
        Assert.assertNotEquals(componentRegistry1, componentRegistry2)

        Assert.assertSame(componentRegistry, componentRegistry.merged(null))
        Assert.assertSame(componentRegistry, null.merged(componentRegistry))
    }

    @Test
    fun testToString() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                toString()
            )
        }
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addDecoder(DrawableDecoder.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "decoderFactoryList=[DrawableDecoder,BitmapFactoryDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TransformationDecodeInterceptor(sortWeight=90),EngineDecodeInterceptor(sortWeight=100)]" +
                        ")",
                toString()
            )
        }
    }

    @Test
    fun testEquals() {
        val componentRegistry0 = ComponentRegistry.Builder().build()
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry11 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry2 = ComponentRegistry.Builder().apply {
            addDecoder(DrawableDecoder.Factory())
        }.build()
        val componentRegistry4 = ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()
        val componentRegistry5 = ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build()

        Assert.assertEquals(componentRegistry0, componentRegistry0)
        Assert.assertEquals(componentRegistry1, componentRegistry11)
        Assert.assertNotEquals(componentRegistry1, Any())
        Assert.assertNotEquals(componentRegistry1, null)
        Assert.assertNotEquals(componentRegistry0, componentRegistry1)
        Assert.assertNotEquals(componentRegistry0, componentRegistry2)
        Assert.assertNotEquals(componentRegistry0, componentRegistry4)
        Assert.assertNotEquals(componentRegistry0, componentRegistry5)
        Assert.assertNotEquals(componentRegistry1, componentRegistry2)
        Assert.assertNotEquals(componentRegistry1, componentRegistry4)
        Assert.assertNotEquals(componentRegistry1, componentRegistry5)
        Assert.assertNotEquals(componentRegistry2, componentRegistry4)
        Assert.assertNotEquals(componentRegistry2, componentRegistry5)
        Assert.assertNotEquals(componentRegistry4, componentRegistry5)
    }

    @Test
    fun testHashCode() {
        val componentRegistry0 = ComponentRegistry.Builder().build()
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry2 = ComponentRegistry.Builder().apply {
            addDecoder(DrawableDecoder.Factory())
        }.build()
        val componentRegistry4 = ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()
        val componentRegistry5 = ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build()

        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry1.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry2.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry2.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry4.hashCode(), componentRegistry5.hashCode())
    }
}