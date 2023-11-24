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

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.GifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableBitmapDecoder
import com.github.panpf.sketch.decode.internal.EngineBitmapDecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineDrawableDecodeInterceptor
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.test.utils.AllFetcher
import com.github.panpf.sketch.test.utils.Test2BitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.Test2DrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.Test2RequestInterceptor
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.core.test.newSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.tools4j.test.ktx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentsTest {

    @Test
    fun testRequestInterceptors() {
        val (context, sketch) = getTestContextAndNewSketch()
        val emptyRequest = DisplayRequest(context, "")
        val notEmptyRequest = DisplayRequest(context, "") {
            components {
                addRequestInterceptor(TestRequestInterceptor(95))
                addRequestInterceptor(Test2RequestInterceptor())
            }
        }

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertEquals(
                listOf<RequestInterceptor>(),
                getRequestInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(Test2RequestInterceptor(), TestRequestInterceptor(95)),
                getRequestInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                listOf(MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                getRequestInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    Test2RequestInterceptor(),
                    MemoryCacheRequestInterceptor(),
                    TestRequestInterceptor(95),
                    EngineRequestInterceptor()
                ),
                getRequestInterceptorList(notEmptyRequest)
            )
        }
    }

    @Test
    fun testBitmapDecodeInterceptors() {
        val (context, sketch) = getTestContextAndNewSketch()
        val emptyRequest = DisplayRequest(context, "")
        val notEmptyRequest = DisplayRequest(context, "") {
            components {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor(95))
                addBitmapDecodeInterceptor(Test2BitmapDecodeInterceptor())
            }
        }

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertEquals(
                listOf<BitmapDecodeInterceptor>(),
                getBitmapDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    Test2BitmapDecodeInterceptor(),
                    TestBitmapDecodeInterceptor(95),
                ),
                getBitmapDecodeInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                listOf(
                    BitmapTransformationDecodeInterceptor(),
                    EngineBitmapDecodeInterceptor()
                ),
                getBitmapDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    Test2BitmapDecodeInterceptor(),
                    BitmapTransformationDecodeInterceptor(),
                    TestBitmapDecodeInterceptor(95),
                    EngineBitmapDecodeInterceptor()
                ),
                getBitmapDecodeInterceptorList(notEmptyRequest)
            )
        }
    }

    @Test
    fun testDrawableDecodeInterceptors() {
        val (context, sketch) = getTestContextAndNewSketch()
        val emptyRequest = DisplayRequest(context, "")
        val notEmptyRequest = DisplayRequest(context, "") {
            components {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(95))
                addDrawableDecodeInterceptor(Test2DrawableDecodeInterceptor())
            }
        }

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertEquals(
                listOf<DrawableDecodeInterceptor>(),
                getDrawableDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    Test2DrawableDecodeInterceptor(),
                    TestDrawableDecodeInterceptor(95),
                ),
                getDrawableDecodeInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor(90))
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                listOf(TestDrawableDecodeInterceptor(90), EngineDrawableDecodeInterceptor()),
                getDrawableDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    Test2DrawableDecodeInterceptor(),
                    TestDrawableDecodeInterceptor(90),
                    TestDrawableDecodeInterceptor(95),
                    EngineDrawableDecodeInterceptor()
                ),
                getDrawableDecodeInterceptorList(notEmptyRequest)
            )
        }
    }

    @Test
    fun testNewFetcher() {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, ComponentRegistry.Builder().build()).apply {
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addFetcher(HttpUriFetcher.Factory())
                    }
                })
            }
            assertNoThrow {
                newFetcherOrThrow(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addFetcher(AssetUriFetcher.Factory())
                    }
                })
            }

            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(DisplayRequest(context, "http://sample.com/sample.jpeg"))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(DisplayRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        addFetcher(AssetUriFetcher.Factory())
                    }
                })
            }
            assertNoThrow {
                newFetcherOrThrow(DisplayRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        addFetcher(HttpUriFetcher.Factory())
                    }
                })
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }.build()).apply {
            Assert.assertTrue(
                newFetcherOrThrow(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)) is AssetUriFetcher
            )
            Assert.assertTrue(
                newFetcherOrThrow(
                    DisplayRequest(context, "http://sample.com/sample.jpeg")
                ) is HttpUriFetcher
            )
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(DisplayRequest(context, "file:///sdcard/sample.jpeg"))
            }

            Assert.assertTrue(newFetcherOrThrow(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                components {
                    addFetcher(AllFetcher.Factory())
                }
            }) is AllFetcher)
            Assert.assertTrue(newFetcherOrThrow(DisplayRequest(context, "http://sample.com/sample.jpeg") {
                components {
                    addFetcher(AllFetcher.Factory())
                }
            }) is AllFetcher)
            Assert.assertTrue(newFetcherOrThrow(DisplayRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    addFetcher(AllFetcher.Factory())
                }
            }) is AllFetcher)
        }
    }

    @Test
    fun testNewBitmapDecoder() {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertThrow(IllegalStateException::class) {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                runBlocking(Dispatchers.Main) {
                    newBitmapDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertThrow(IllegalArgumentException::class) {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newBitmapDecoderOrThrow(requestContext, fetchResult)
            }
            assertThrow(IllegalArgumentException::class) {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addBitmapDecoder(DrawableBitmapDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newBitmapDecoderOrThrow(requestContext, fetchResult)
            }
            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addBitmapDecoder(DefaultBitmapDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newBitmapDecoderOrThrow(requestContext, fetchResult)
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
        }.build()).apply {
            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                Assert.assertTrue(
                    newBitmapDecoderOrThrow(requestContext, fetchResult) is DefaultBitmapDecoder
                )
            }

            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addBitmapDecoder(TestBitmapDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                Assert.assertTrue(
                    newBitmapDecoderOrThrow(requestContext, fetchResult) is TestBitmapDecoder
                )
            }
        }
    }

    @Test
    fun testNewDrawableDecoder() {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertThrow(IllegalStateException::class) {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                runBlocking(Dispatchers.Main) {
                    newDrawableDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertThrow(IllegalArgumentException::class) {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newDrawableDecoderOrThrow(requestContext, fetchResult)
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                assertThrow(IllegalArgumentException::class) {
                    val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                        components {
                            addDrawableDecoder(GifAnimatedDrawableDecoder.Factory())
                        }
                    }
                    val requestContext = request.toRequestContext()
                    val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                    newDrawableDecoderOrThrow(requestContext, fetchResult)
                }
            }
            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addDrawableDecoder(DefaultDrawableDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newDrawableDecoderOrThrow(requestContext, fetchResult)
            }
        }

        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build()).apply {
            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                Assert.assertTrue(
                    newDrawableDecoderOrThrow(
                        requestContext,
                        fetchResult
                    ) is DefaultDrawableDecoder
                )
            }
            assertNoThrow {
                val request = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
                    components {
                        addDrawableDecoder(TestDrawableDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext()
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                Assert.assertTrue(
                    newDrawableDecoderOrThrow(requestContext, fetchResult) is TestDrawableDecoder
                )
            }
        }
    }

    @Test
    fun testToString() {
        val sketch = newSketch()
        Components(sketch, ComponentRegistry.Builder().build()).apply {
            Assert.assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "bitmapDecoderFactoryList=[]," +
                        "drawableDecoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "bitmapDecodeInterceptorList=[]," +
                        "drawableDecodeInterceptorList=[]" +
                        "))",
                toString()
            )
        }
        Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addBitmapDecoder(DrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
            addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "bitmapDecoderFactoryList=[DrawableBitmapDecoder,DefaultBitmapDecoder]," +
                        "drawableDecoderFactoryList=[DefaultDrawableDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "bitmapDecodeInterceptorList=[BitmapTransformationDecodeInterceptor(sortWeight=90),EngineBitmapDecodeInterceptor(sortWeight=100)]," +
                        "drawableDecodeInterceptorList=[EngineDrawableDecodeInterceptor(sortWeight=100)]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEquals() {
        val sketch = newSketch()
        val components0 = Components(sketch, ComponentRegistry.Builder().build())
        val components1 = Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build())
        val components11 = Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build())
        val components2 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecoder(DrawableBitmapDecoder.Factory())
        }.build())
        val components3 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build())
        val components6 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build())

        Assert.assertEquals(components0, components0)
        Assert.assertEquals(components1, components11)
        Assert.assertNotEquals(components1, Any())
        Assert.assertNotEquals(components1, null)
        Assert.assertNotEquals(components0, components1)
        Assert.assertNotEquals(components0, components2)
        Assert.assertNotEquals(components0, components3)
        Assert.assertNotEquals(components0, components4)
        Assert.assertNotEquals(components0, components5)
        Assert.assertNotEquals(components0, components6)
        Assert.assertNotEquals(components1, components2)
        Assert.assertNotEquals(components1, components3)
        Assert.assertNotEquals(components1, components4)
        Assert.assertNotEquals(components1, components5)
        Assert.assertNotEquals(components1, components6)
        Assert.assertNotEquals(components2, components3)
        Assert.assertNotEquals(components2, components4)
        Assert.assertNotEquals(components2, components5)
        Assert.assertNotEquals(components2, components6)
        Assert.assertNotEquals(components3, components4)
        Assert.assertNotEquals(components3, components5)
        Assert.assertNotEquals(components3, components6)
        Assert.assertNotEquals(components4, components5)
        Assert.assertNotEquals(components4, components6)
        Assert.assertNotEquals(components5, components6)
    }

    @Test
    fun testHashCode() {
        val sketch = newSketch()
        val components0 = Components(sketch, ComponentRegistry.Builder().build())
        val components1 = Components(sketch, ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build())
        val components2 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecoder(DrawableBitmapDecoder.Factory())
        }.build())
        val components3 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, ComponentRegistry.Builder().apply {
            addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())
        }.build())
        val components6 = Components(sketch, ComponentRegistry.Builder().apply {
            addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
        }.build())

        Assert.assertNotEquals(components0.hashCode(), components1.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components3.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components3.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components4.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components4.hashCode(), components6.hashCode())
        Assert.assertNotEquals(components5.hashCode(), components6.hashCode())
    }
}