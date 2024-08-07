package com.github.panpf.sketch.core.android.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry.Builder
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher.Factory
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.AllFetcher
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestRequestInterceptor2
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.tools4j.test.Assertx.assertNoThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFails

@RunWith(AndroidJUnit4::class)
class ComponentsTest {

    @Test
    fun testRequestInterceptors() {
        val (context, sketch) = getTestContextAndSketch()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addRequestInterceptor(TestRequestInterceptor(95))
                addRequestInterceptor(TestRequestInterceptor2())
            }
        }

        Components(sketch, Builder().build()).apply {
            Assert.assertEquals(
                listOf<RequestInterceptor>(),
                getRequestInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(TestRequestInterceptor2(), TestRequestInterceptor(95)),
                getRequestInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, Builder().apply {
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                listOf(MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                getRequestInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    TestRequestInterceptor2(),
                    MemoryCacheRequestInterceptor(),
                    TestRequestInterceptor(95),
                    EngineRequestInterceptor()
                ),
                getRequestInterceptorList(notEmptyRequest)
            )
        }
    }

    @Test
    fun testDecodeInterceptors() {
        val (context, sketch) = getTestContextAndSketch()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addDecodeInterceptor(TestDecodeInterceptor(95))
                addDecodeInterceptor(TestDecodeInterceptor2())
            }
        }

        Components(sketch, Builder().build()).apply {
            Assert.assertEquals(
                listOf<DecodeInterceptor>(),
                getDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    TestDecodeInterceptor2(),
                    TestDecodeInterceptor(95),
                ),
                getDecodeInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, Builder().apply {
            addDecodeInterceptor(TransformationDecodeInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                listOf(
                    TransformationDecodeInterceptor(),
                    EngineDecodeInterceptor()
                ),
                getDecodeInterceptorList(emptyRequest)
            )
            Assert.assertEquals(
                listOf(
                    TestDecodeInterceptor2(),
                    TransformationDecodeInterceptor(),
                    TestDecodeInterceptor(95),
                    EngineDecodeInterceptor()
                ),
                getDecodeInterceptorList(notEmptyRequest)
            )
        }
    }

    @Test
    fun testNewFetcher() {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, Builder().build()).apply {
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, ResourceImages.jpeg.uri))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, ResourceImages.jpeg.uri) {
                    components {
                        addFetcher(Factory())
                    }
                })
            }
            assertNoThrow {
                newFetcherOrThrow(ImageRequest(context, ResourceImages.jpeg.uri) {
                    components {
                        addFetcher(AssetUriFetcher.Factory())
                    }
                })
            }

            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg"))
            }
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        addFetcher(AssetUriFetcher.Factory())
                    }
                })
            }
            assertNoThrow {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        addFetcher(Factory())
                    }
                })
            }
        }

        Components(sketch, Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(Factory())
        }.build()).apply {
            Assert.assertTrue(
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        ResourceImages.jpeg.uri
                    )
                ) is AssetUriFetcher
            )
            Assert.assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                ) is HttpUriFetcher
            )
            assertThrow(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg"))
            }

            Assert.assertTrue(
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        ResourceImages.jpeg.uri
                    ) {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }) is AllFetcher
            )
            Assert.assertTrue(
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ) {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }) is AllFetcher
            )
            Assert.assertTrue(
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ) {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }) is AllFetcher
            )
        }
    }

    @Test
    fun testNewDecoder() = runTest {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertFails {
                val request = ImageRequest(context, ResourceImages.jpeg.uri)
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                runBlocking(Dispatchers.Main) {
                    newDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        Components(sketch, Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
        }.build()).apply {
            assertFails {
                val request = ImageRequest(context, ResourceImages.jpeg.uri)
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            assertFails {
                val request = ImageRequest(context, ResourceImages.jpeg.uri) {
                    components {
                        addDecoder(DrawableDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            val request = ImageRequest(context, ResourceImages.jpeg.uri) {
                components {
                    addDecoder(BitmapFactoryDecoder.Factory())
                }
            }
            val requestContext = request.toRequestContext(sketch)
            val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
            Assert.assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is BitmapFactoryDecoder
            )
        }

        Components(sketch, Builder().apply {
            addFetcher(AssetUriFetcher.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
        }.build()).apply {
            val request = ImageRequest(context, ResourceImages.jpeg.uri)
            val requestContext = request.toRequestContext(sketch)
            val fetchResult = runBlocking { newFetcherOrThrow(request).fetch() }.getOrThrow()
            Assert.assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is BitmapFactoryDecoder
            )

            val request2 = ImageRequest(context, ResourceImages.jpeg.uri) {
                components {
                    addDecoder(TestDecoder.Factory())
                }
            }
            val requestContext2 = request2.toRequestContext(sketch)
            val fetchResult2 = runBlocking { newFetcherOrThrow(request2).fetch() }.getOrThrow()
            Assert.assertTrue(
                newDecoderOrThrow(requestContext2, fetchResult2) is TestDecoder
            )
        }
    }

    @Test
    fun testToString() {
        val sketch = newSketch()
        Components(sketch, Builder().build()).apply {
            Assert.assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        "))",
                toString()
            )
        }
        Components(sketch, Builder().apply {
            addFetcher(Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addDecoder(DrawableDecoder.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
        }.build()).apply {
            Assert.assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,ResourceUriFetcher]," +
                        "decoderFactoryList=[DrawableDecoder,BitmapFactoryDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TransformationDecodeInterceptor(sortWeight=90),EngineDecodeInterceptor(sortWeight=100)]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEquals() {
        val sketch = newSketch()
        val components0 = Components(sketch, Builder().build())
        val components1 = Components(sketch, Builder().apply {
            addFetcher(Factory())
        }.build())
        val components11 = Components(sketch, Builder().apply {
            addFetcher(Factory())
        }.build())
        val components2 = Components(sketch, Builder().apply {
            addDecoder(DrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build())

        Assert.assertEquals(components0, components0)
        Assert.assertEquals(components1, components11)
        Assert.assertNotEquals(components1, Any())
        Assert.assertNotEquals(components1, null)
        Assert.assertNotEquals(components0, components1)
        Assert.assertNotEquals(components0, components2)
        Assert.assertNotEquals(components0, components4)
        Assert.assertNotEquals(components0, components5)
        Assert.assertNotEquals(components1, components2)
        Assert.assertNotEquals(components1, components4)
        Assert.assertNotEquals(components1, components5)
        Assert.assertNotEquals(components2, components4)
        Assert.assertNotEquals(components2, components5)
        Assert.assertNotEquals(components4, components5)
    }

    @Test
    fun testHashCode() {
        val sketch = newSketch()
        val components0 = Components(sketch, Builder().build())
        val components1 = Components(sketch, Builder().apply {
            addFetcher(Factory())
        }.build())
        val components2 = Components(sketch, Builder().apply {
            addDecoder(DrawableDecoder.Factory())
        }.build())
        val components4 = Components(sketch, Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build())
        val components5 = Components(sketch, Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build())

        Assert.assertNotEquals(components0.hashCode(), components1.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components0.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components2.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components1.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components4.hashCode())
        Assert.assertNotEquals(components2.hashCode(), components5.hashCode())
        Assert.assertNotEquals(components4.hashCode(), components5.hashCode())
    }
}