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
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.Test2BitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.Test2DrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
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
    fun testBuilder() {
        ComponentRegistry.Builder().build().apply {
            Assert.assertTrue(fetcherFactoryList.isEmpty())
            Assert.assertTrue(bitmapDecoderFactoryList.isEmpty())
            Assert.assertTrue(drawableDecoderFactoryList.isEmpty())
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    HttpUriFetcher.Factory(),
                    Base64UriFetcher.Factory(),
                    ResourceUriFetcher.Factory()
                ),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(
                    XmlDrawableBitmapDecoder.Factory(),
                    DefaultBitmapDecoder.Factory()
                ),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(
                    EngineBitmapDecodeInterceptor(),
                    BitmapTransformationDecodeInterceptor()
                ),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(EngineDrawableDecodeInterceptor()),
                drawableDecodeInterceptorList
            )
        }
    }

    @Test
    fun testNewBuilder() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }.newBuilder().build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }.newBuilder {
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(EngineBitmapDecodeInterceptor()),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(EngineDrawableDecodeInterceptor()),
                drawableDecodeInterceptorList
            )
        }
    }

    @Test
    fun testNewRegistry() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }.newRegistry().apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertTrue(requestInterceptorList.isEmpty())
            Assert.assertTrue(bitmapDecodeInterceptorList.isEmpty())
            Assert.assertTrue(drawableDecodeInterceptorList.isEmpty())
        }.newRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.apply {
            Assert.assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            Assert.assertEquals(
                listOf(XmlDrawableBitmapDecoder.Factory()),
                bitmapDecoderFactoryList
            )
            Assert.assertEquals(
                listOf(DefaultDrawableDecoder.Factory()),
                drawableDecoderFactoryList
            )
            Assert.assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            Assert.assertEquals(
                listOf(EngineBitmapDecodeInterceptor()),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(EngineDrawableDecodeInterceptor()),
                drawableDecodeInterceptorList
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
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
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
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build().apply {
            Assert.assertFalse(isEmpty())
            Assert.assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
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
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
                }
            }
        }

        ComponentRegistry.Builder().build().apply {
            assertThrow(IllegalArgumentException::class) {
                newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcher(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNull(
                newFetcherOrNull(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            )
            Assert.assertNull(
                newFetcherOrNull(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            assertNoThrow {
                newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcher(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNotNull(
                newFetcherOrNull(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            )
            Assert.assertNull(
                newFetcherOrNull(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }.build().apply {
            assertNoThrow {
                newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertNoThrow {
                newFetcher(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }

            Assert.assertNotNull(
                newFetcherOrNull(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            )
            Assert.assertNotNull(
                newFetcherOrNull(sketch, DisplayRequest(context, "http://sample.com/sample.jpeg"))
            )
        }
    }

    @Test
    fun testBitmapDecoder() {
        val context = getTestContext()
        val sketch = newSketch()
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
        val requestContext = RequestContext(request)

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newBitmapDecoder(sketch, requestContext, fetchResult)
                }
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newBitmapDecoder(sketch, requestContext, fetchResult)
            }
            Assert.assertNull(
                newBitmapDecoderOrNull(sketch, requestContext, fetchResult)
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newBitmapDecoder(sketch, requestContext, fetchResult)
            }
            Assert.assertNotNull(
                newBitmapDecoderOrNull(sketch, requestContext, fetchResult)
            )
        }
    }

    @Test
    fun testDrawableDecoder() {
        val context = getTestContext()
        val sketch = newSketch()
        val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
        val requestContext = RequestContext(request)

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalStateException::class) {
                runBlocking(Dispatchers.Main) {
                    newDrawableDecoder(sketch, requestContext, fetchResult)
                }
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newDrawableDecoder(sketch, requestContext, fetchResult)
            }
            Assert.assertNull(
                newDrawableDecoderOrNull(sketch, requestContext, fetchResult)
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newDrawableDecoder(sketch, requestContext, fetchResult)
            }
            Assert.assertNotNull(
                newDrawableDecoderOrNull(sketch, requestContext, fetchResult)
            )
        }
    }

    @Test
    fun testMerged() {
        val componentRegistry = ComponentRegistry.Builder().apply {
            addFetcher(TestFetcher.Factory())
            addBitmapDecoder(TestBitmapDecoder.Factory())
            addDrawableDecoder(TestDrawableDecoder.Factory())
            addRequestInterceptor(TestRequestInterceptor())
            addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
            addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher]," +
                        "bitmapDecoderFactoryList=[TestBitmapDecoder]," +
                        "drawableDecoderFactoryList=[TestDrawableDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[TestBitmapDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[TestDrawableDecodeInterceptor]" +
                        ")",
                toString()
            )
        }
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(Test2BitmapDecodeInterceptor())
            addDrawableDecodeInterceptor(Test2DrawableDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher]," +
                        "bitmapDecoderFactoryList=[DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[DefaultDrawableDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[Test2BitmapDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[Test2DrawableDecodeInterceptor]" +
                        ")",
                toString()
            )
        }
        Assert.assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher,HttpUriFetcher]," +
                        "bitmapDecoderFactoryList=[TestBitmapDecoder,DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[TestDrawableDecoder,DefaultDrawableDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor,EngineRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[TestBitmapDecodeInterceptor,Test2BitmapDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[TestDrawableDecodeInterceptor,Test2DrawableDecodeInterceptor]" +
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
                        "bitmapDecoderFactoryList=[]," +
                        "drawableDecoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "bitmapDecodeInterceptorList=[]," +
                        "drawableDecodeInterceptorList=[]" +
                        ")",
                toString()
            )
        }
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "bitmapDecoderFactoryList=[XmlDrawableBitmapDecoder,DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[DefaultDrawableDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[EngineBitmapDecodeInterceptor,BitmapTransformationDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[EngineDrawableDecodeInterceptor]" +
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
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
        }.build()
        val componentRegistry3 = ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build()
        val componentRegistry4 = ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()
        val componentRegistry5 = ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build()
        val componentRegistry6 = ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build()

        Assert.assertEquals(componentRegistry0, componentRegistry0)
        Assert.assertEquals(componentRegistry1, componentRegistry11)
        Assert.assertNotEquals(componentRegistry1, Any())
        Assert.assertNotEquals(componentRegistry1, null)
        Assert.assertNotEquals(componentRegistry0, componentRegistry1)
        Assert.assertNotEquals(componentRegistry0, componentRegistry2)
        Assert.assertNotEquals(componentRegistry0, componentRegistry3)
        Assert.assertNotEquals(componentRegistry0, componentRegistry4)
        Assert.assertNotEquals(componentRegistry0, componentRegistry5)
        Assert.assertNotEquals(componentRegistry0, componentRegistry6)
        Assert.assertNotEquals(componentRegistry1, componentRegistry2)
        Assert.assertNotEquals(componentRegistry1, componentRegistry3)
        Assert.assertNotEquals(componentRegistry1, componentRegistry4)
        Assert.assertNotEquals(componentRegistry1, componentRegistry5)
        Assert.assertNotEquals(componentRegistry1, componentRegistry6)
        Assert.assertNotEquals(componentRegistry2, componentRegistry3)
        Assert.assertNotEquals(componentRegistry2, componentRegistry4)
        Assert.assertNotEquals(componentRegistry2, componentRegistry5)
        Assert.assertNotEquals(componentRegistry2, componentRegistry6)
        Assert.assertNotEquals(componentRegistry3, componentRegistry4)
        Assert.assertNotEquals(componentRegistry3, componentRegistry5)
        Assert.assertNotEquals(componentRegistry3, componentRegistry6)
        Assert.assertNotEquals(componentRegistry4, componentRegistry5)
        Assert.assertNotEquals(componentRegistry4, componentRegistry6)
        Assert.assertNotEquals(componentRegistry5, componentRegistry6)
    }

    @Test
    fun testHashCode() {
        val componentRegistry0 = ComponentRegistry.Builder().build()
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry2 = ComponentRegistry.Builder().apply {
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
        }.build()
        val componentRegistry3 = ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build()
        val componentRegistry4 = ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()
        val componentRegistry5 = ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build()
        val componentRegistry6 = ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build()

        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry1.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry2.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry3.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry0.hashCode(), componentRegistry6.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry2.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry3.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry1.hashCode(), componentRegistry6.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry3.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry2.hashCode(), componentRegistry6.hashCode())
        Assert.assertNotEquals(componentRegistry3.hashCode(), componentRegistry4.hashCode())
        Assert.assertNotEquals(componentRegistry3.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry3.hashCode(), componentRegistry6.hashCode())
        Assert.assertNotEquals(componentRegistry4.hashCode(), componentRegistry5.hashCode())
        Assert.assertNotEquals(componentRegistry4.hashCode(), componentRegistry6.hashCode())
        Assert.assertNotEquals(componentRegistry5.hashCode(), componentRegistry6.hashCode())
    }
}