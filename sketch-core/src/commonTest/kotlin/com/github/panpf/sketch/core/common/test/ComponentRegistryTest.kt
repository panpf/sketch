package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
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
    fun testNewBuilder() {
        ComponentRegistry {
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
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
                listOf(TestFetcher.Factory()),
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
                listOf(TestFetcher.Factory()),
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
        ComponentRegistry {
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
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
                listOf(TestFetcher.Factory()),
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
                listOf(TestFetcher.Factory()),
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
        ComponentRegistry().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        ComponentRegistry {
            addFetcher(TestFetcher.Factory())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            addDecoder(TestDecoder.Factory())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }
    }

    @Test
    fun testNewFetcher() {
        val (context, sketch) = getTestContextAndSketch()

        ComponentRegistry().apply {
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            }

            assertNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
            assertNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
        }

        ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
        }.apply {
            newFetcherOrThrow(
                ImageRequest(context, "file:///sdcard/sample.jpeg").toRequestContext(
                    sketch,
                    Size.Empty
                )
            )
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            }

            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
            assertNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
        }

        ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
            addFetcher(TestHttpUriFetcher.Factory(context))
        }.apply {
            newFetcherOrThrow(
                ImageRequest(context, "file:///sdcard/sample.jpeg").toRequestContext(
                    sketch,
                    Size.Empty
                )
            )
            newFetcherOrThrow(
                ImageRequest(
                    context,
                    "http://sample.com/sample.jpeg"
                ).toRequestContext(sketch, Size.Empty)
            )

            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(
                        context,
                        "http://sample.com/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            )
        }
    }

    @Test
    fun testNewDecoder() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
        val requestContext = request.toRequestContext(sketch)

        ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
        }.apply {
            val fetcher =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty))
            val fetchResult = fetcher.fetch().getOrThrow()
            assertFailsWith(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
        }

        ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
        }.apply {
            val fetcher =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty))
            val fetchResult = fetcher.fetch().getOrThrow()
            assertFailsWith(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
            assertNull(
                newDecoderOrNull(requestContext, fetchResult)
            )
        }

        ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }.apply {
            val fetcher =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty))
            val fetchResult = fetcher.fetch().getOrThrow()
            newDecoderOrThrow(requestContext, fetchResult)
            assertNotNull(
                newDecoderOrNull(requestContext, fetchResult)
            )
        }
    }

    @Test
    fun testMerged() {
        val componentRegistry = ComponentRegistry {
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addRequestInterceptor(TestRequestInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor())
        }.apply {
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
        val componentRegistry1 = ComponentRegistry {
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor2())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher]," +
                        "decoderFactoryList=[TestDecoder]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor2(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[TestFetcher,TestFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder]," +
                        "requestInterceptorList=[TestRequestInterceptor(sortWeight=0),EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0),TestDecodeInterceptor2(sortWeight=0)]" +
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
        ComponentRegistry().apply {
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
        ComponentRegistry {
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addDecoder(TestDecoder2.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetcherFactoryList=[Base64UriFetcher,TestFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder2]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0),EngineDecodeInterceptor(sortWeight=100)]" +
                        ")",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val componentRegistry0 = ComponentRegistry()
        val componentRegistry1 = ComponentRegistry {
            addFetcher(TestFetcher.Factory())
        }
        val componentRegistry11 = ComponentRegistry {
            addFetcher(TestFetcher.Factory())
        }
        val componentRegistry2 = ComponentRegistry {
            addDecoder(TestDecoder.Factory())
        }
        val componentRegistry4 = ComponentRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
        }
        val componentRegistry5 = ComponentRegistry {
            addDecodeInterceptor(EngineDecodeInterceptor())
        }

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
    fun testBuilder() {
        ComponentRegistry().apply {
            assertTrue(fetcherFactoryList.isEmpty())
            assertTrue(decoderFactoryList.isEmpty())
            assertTrue(requestInterceptorList.isEmpty())
            assertTrue(decodeInterceptorList.isEmpty())
        }

        ComponentRegistry {
            addFetcher(Base64UriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            assertFailsWith(IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(100))
            }
            addDecodeInterceptor(EngineDecodeInterceptor())
            assertFailsWith(IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(100))
            }
        }.apply {
            assertEquals(
                listOf(
                    Base64UriFetcher.Factory(),
                ),
                fetcherFactoryList
            )
            assertEquals(
                listOf(
                    TestDecoder.Factory(),
                ),
                decoderFactoryList
            )
            assertEquals(
                listOf(
                    EngineRequestInterceptor()
                ),
                requestInterceptorList
            )
            assertEquals(
                listOf(
                    EngineDecodeInterceptor(),
                ),
                decodeInterceptorList
            )
        }.newRegistry {
            addComponents(ComponentRegistry {
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder2.Factory())
                addRequestInterceptor(MemoryCacheRequestInterceptor())
                addRequestInterceptor(TestRequestInterceptor(95))
                addDecodeInterceptor(TestDecodeInterceptor2())
                addDecodeInterceptor(TestDecodeInterceptor(95))
            })
        }.apply {
            assertEquals(
                listOf(
                    Base64UriFetcher.Factory(),
                    TestFetcher.Factory(),
                ),
                fetcherFactoryList
            )
            assertEquals(
                listOf(
                    TestDecoder.Factory(),
                    TestDecoder2.Factory(),
                ),
                decoderFactoryList
            )
            assertEquals(
                listOf(
                    MemoryCacheRequestInterceptor(),
                    TestRequestInterceptor(95),
                    EngineRequestInterceptor()
                ),
                requestInterceptorList
            )
            assertEquals(
                listOf(
                    TestDecodeInterceptor2(),
                    TestDecodeInterceptor(95),
                    EngineDecodeInterceptor(),
                ),
                decodeInterceptorList
            )
        }
    }
}