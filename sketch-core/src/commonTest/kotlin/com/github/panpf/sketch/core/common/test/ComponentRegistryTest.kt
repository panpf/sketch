package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ComponentRegistryTest {

    @Test
    fun testFun() {
        // TODO test
    }

    @Test
    fun testNewBuilder() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertTrue(requestInterceptorList.isEmpty())
            assertTrue(decodeInterceptorList.isEmpty())
        }.newBuilder().build().apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertTrue(requestInterceptorList.isEmpty())
            assertTrue(decodeInterceptorList.isEmpty())
        }.newBuilder {
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build().apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            assertEquals(
                listOf(EngineDecodeInterceptor()),
                decodeInterceptorList
            )
        }
    }

    @Test
    fun testNewRegistry() {
        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertTrue(requestInterceptorList.isEmpty())
            assertTrue(decodeInterceptorList.isEmpty())
        }.newRegistry().apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertTrue(requestInterceptorList.isEmpty())
            assertTrue(decodeInterceptorList.isEmpty())
        }.newRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.apply {
            assertEquals(
                listOf(HttpUriFetcher.Factory()),
                fetcherFactoryList
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoderFactoryList
            )
            assertEquals(listOf(EngineRequestInterceptor()), requestInterceptorList)
            assertEquals(
                listOf(EngineDecodeInterceptor()),
                decodeInterceptorList
            )
        }
    }

    @Test
    fun testIsEmpty() {
        ComponentRegistry.Builder().build().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build().apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testNewFetcher() {
        val context = getTestContext()
        val sketch = newSketch()

        ComponentRegistry.Builder().build().apply {
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            }

            assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            )
            assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(FileUriFetcher.Factory())
        }.build().apply {
            newFetcherOrThrow(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            }

            assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            )
            assertNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(FileUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }.build().apply {
            newFetcherOrThrow(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            newFetcherOrThrow(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))

            assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, "file:///sdcard/sample.jpeg"))
            )
            assertNotNull(
                newFetcherOrNull(sketch, ImageRequest(context, "http://sample.com/sample.jpeg"))
            )
        }
    }

    @Test
    fun testNewDecoder() = runTest {
        val context = getTestContext()
        val sketch = newSketch()
        val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
        val requestContext = request.toRequestContext(sketch)

        ComponentRegistry.Builder().apply {
            addFetcher(FileUriFetcher.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, request)
            val fetchResult = fetcher.fetch().getOrThrow()
            assertFailsWith(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
        }

        ComponentRegistry.Builder().apply {
            addFetcher(FileUriFetcher.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, request)
            val fetchResult = fetcher.fetch().getOrThrow()
            assertFailsWith(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
            assertNull(
                newDecoderOrNull(requestContext, fetchResult)
            )
        }

        ComponentRegistry.Builder().apply {
            addFetcher(FileUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            val fetcher =
                newFetcherOrThrow(sketch, request)
            val fetchResult = fetcher.fetch().getOrThrow()
            newDecoderOrThrow(requestContext, fetchResult)
            assertNotNull(
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
            assertEquals(
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
            addDecoder(TestDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor2())
        }.build().apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher]," +
                        "decoderFactoryList=[TestDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[Test2DecodeInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher,HttpUriFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor(sortWeight=0),EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0),Test2DecodeInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry2)
        assertNotEquals(componentRegistry1, componentRegistry2)

        assertSame(componentRegistry, componentRegistry.merged(null))
        assertSame(componentRegistry, null.merged(componentRegistry))
    }

    @Test
    fun testToString() {
        ComponentRegistry.Builder().build().apply {
            assertEquals(
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
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addDecoder(TestDecoder2.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
        }.build().apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,TestFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder2]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TransformationDecodeInterceptor(sortWeight=90),EngineDecodeInterceptor(sortWeight=100)]" +
                        ")",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val componentRegistry0 = ComponentRegistry.Builder().build()
        val componentRegistry1 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry11 = ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
        }.build()
        val componentRegistry2 = ComponentRegistry.Builder().apply {
            addDecoder(TestDecoder.Factory())
        }.build()
        val componentRegistry4 = ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
        }.build()
        val componentRegistry5 = ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.build()

        assertEquals(componentRegistry0, componentRegistry0)
        assertEquals(componentRegistry1, componentRegistry11)
        assertNotEquals(componentRegistry1, Any())
        assertNotEquals(componentRegistry1, null as Any?)
        assertNotEquals(componentRegistry0, componentRegistry1)
        assertNotEquals(componentRegistry0, componentRegistry2)
        assertNotEquals(componentRegistry0, componentRegistry4)
        assertNotEquals(componentRegistry0, componentRegistry5)
        assertNotEquals(componentRegistry1, componentRegistry2)
        assertNotEquals(componentRegistry1, componentRegistry4)
        assertNotEquals(componentRegistry1, componentRegistry5)
        assertNotEquals(componentRegistry2, componentRegistry4)
        assertNotEquals(componentRegistry2, componentRegistry5)
        assertNotEquals(componentRegistry4, componentRegistry5)

        assertNotEquals(componentRegistry0.hashCode(), componentRegistry1.hashCode())
        assertNotEquals(componentRegistry0.hashCode(), componentRegistry2.hashCode())
        assertNotEquals(componentRegistry0.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry0.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry2.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry4.hashCode(), componentRegistry5.hashCode())
    }

    @Test
    fun testBuilderAddFetcher() {
        ComponentRegistry.Builder().build().apply {
            assertTrue(fetcherFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
        }.build().apply {
            assertEquals(
                listOf(
                    HttpUriFetcher.Factory(),
                    Base64UriFetcher.Factory(),
                    TestFetcher.Factory(),
                ),
                fetcherFactoryList
            )
        }
    }

    @Test
    fun testBuilderAddDecoder() {
        ComponentRegistry.Builder().build().apply {
            assertTrue(decoderFactoryList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecoder(TestDecoder.Factory())
            addDecoder(TestDecoder2.Factory())
        }.build().apply {
            assertEquals(
                listOf(
                    TestDecoder.Factory(),
                    TestDecoder2.Factory(),
                ),
                decoderFactoryList
            )
        }
    }

    @Test
    fun testBuilderAddRequestInterceptor() {
        ComponentRegistry.Builder().build().apply {
            assertTrue(requestInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(TestRequestInterceptor(95))

            assertFailsWith(IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(100))
            }
        }.build().apply {
            assertEquals(
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
    fun testBuilderAddDecodeInterceptor() {
        ComponentRegistry.Builder().build().apply {
            assertTrue(decodeInterceptorList.isEmpty())
        }

        ComponentRegistry.Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor(95))
            assertFailsWith(IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(100))
            }
        }.build().apply {
            assertEquals(
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