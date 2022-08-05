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
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.TestAssets
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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
                    BitmapEngineDecodeInterceptor(),
                    BitmapTransformationDecodeInterceptor()
                ),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(DrawableEngineDecodeInterceptor()),
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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
                listOf(BitmapEngineDecodeInterceptor()),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(DrawableEngineDecodeInterceptor()),
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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
                listOf(BitmapEngineDecodeInterceptor()),
                bitmapDecodeInterceptorList
            )
            Assert.assertEquals(
                listOf(DrawableEngineDecodeInterceptor()),
                drawableDecodeInterceptorList
            )
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
                    newBitmapDecoder(sketch, request, requestContext, fetchResult)
                }
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newBitmapDecoder(sketch, request, requestContext, fetchResult)
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newBitmapDecoder(sketch, request, requestContext, fetchResult)
            }
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
                    newDrawableDecoder(sketch, request, requestContext, fetchResult)
                }
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertThrow(IllegalArgumentException::class) {
                newDrawableDecoder(sketch, request, requestContext, fetchResult)
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build().apply {
            val fetcher = newFetcher(sketch, DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            val fetchResult = runBlocking { fetcher.fetch() }
            assertNoThrow {
                newDrawableDecoder(sketch, request, requestContext, fetchResult)
            }
        }
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
        }.build().apply {
            Assert.assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "bitmapDecoderFactoryList=[XmlDrawableBitmapDecoder,DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[DefaultDrawableDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor]," +
                        "bitmapDecodeInterceptorList=[BitmapEngineDecodeInterceptor,BitmapTransformationDecodeInterceptor]," +
                        "drawableDecodeInterceptorList=[DrawableEngineDecodeInterceptor]" +
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
        }.build()
        val componentRegistry6 = ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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
            addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
        }.build()
        val componentRegistry6 = ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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