package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.internal.MemoryCacheInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestInterceptor
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
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
        }.apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertTrue(interceptors.isEmpty())
        }.newBuilder().build().apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertTrue(interceptors.isEmpty())
        }.newBuilder {
            add(DecoderInterceptor())
        }.build().apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertEquals(listOf(DecoderInterceptor()), interceptors)
        }
    }

    @Test
    fun testNewRegistry() {
        ComponentRegistry {
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
        }.apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertTrue(interceptors.isEmpty())
        }.newRegistry().apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertTrue(interceptors.isEmpty())
        }.newRegistry {
            add(DecoderInterceptor())
        }.apply {
            assertEquals(
                listOf(TestFetcher.Factory()),
                fetchers
            )
            assertEquals(
                listOf(TestDecoder.Factory()),
                decoders
            )
            assertEquals(listOf(DecoderInterceptor()), interceptors)
        }
    }

    @Test
    fun testIsEmpty() {
        ComponentRegistry().apply {
            assertTrue(isEmpty())
            assertFalse(isNotEmpty())
        }

        ComponentRegistry {
            add(TestFetcher.Factory())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            add(TestDecoder.Factory())
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            add(DecoderInterceptor())
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
            add(FileUriFetcher.Factory())
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
            add(FileUriFetcher.Factory())
            add(TestHttpUriFetcher.Factory(context))
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
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
        val requestContext = request.toRequestContext(sketch)

        ComponentRegistry {
            add(FileUriFetcher.Factory())
        }.apply {
            val fetcher =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty))
            val fetchResult = fetcher.fetch().getOrThrow()
            assertFailsWith(IllegalArgumentException::class) {
                newDecoderOrThrow(requestContext, fetchResult)
            }
        }

        ComponentRegistry {
            add(FileUriFetcher.Factory())
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
            add(FileUriFetcher.Factory())
            add(TestDecoder.Factory())
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
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
            add(TestInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher]," +
                        "decoders=[TestDecoder]," +
                        "interceptors=[TestInterceptor(sortWeight=0)]" +
                        ")",
                toString()
            )
        }
        val componentRegistry1 = ComponentRegistry {
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
            add(DecoderInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher]," +
                        "decoders=[TestDecoder]," +
                        "interceptors=[DecoderInterceptor]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher,TestFetcher]," +
                        "decoders=[TestDecoder,TestDecoder]," +
                        "interceptors=[TestInterceptor(sortWeight=0),DecoderInterceptor]" +
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
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                toString()
            )
        }
        ComponentRegistry {
            add(Base64UriFetcher.Factory())
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
            add(TestDecoder2.Factory())
            add(DecoderInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[Base64UriFetcher,TestFetcher]," +
                        "decoders=[TestDecoder,TestDecoder2]," +
                        "interceptors=[DecoderInterceptor]" +
                        ")",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val componentRegistry0 = ComponentRegistry()
        val componentRegistry1 = ComponentRegistry {
            add(TestFetcher.Factory())
        }
        val componentRegistry11 = ComponentRegistry {
            add(TestFetcher.Factory())
        }
        val componentRegistry2 = ComponentRegistry {
            add(TestDecoder.Factory())
        }
        val componentRegistry4 = ComponentRegistry {
            add(DecoderInterceptor())
        }

        assertEquals(componentRegistry0, componentRegistry0)
        assertEquals(componentRegistry1, componentRegistry11)
        assertNotEquals(componentRegistry1, Any())
        assertNotEquals(componentRegistry1, null as Any?)
        assertNotEquals(componentRegistry0, componentRegistry1)
        assertNotEquals(componentRegistry0, componentRegistry2)
        assertNotEquals(componentRegistry0, componentRegistry4)
        assertNotEquals(componentRegistry1, componentRegistry2)
        assertNotEquals(componentRegistry1, componentRegistry4)
        assertNotEquals(componentRegistry2, componentRegistry4)

        assertNotEquals(componentRegistry0.hashCode(), componentRegistry1.hashCode())
        assertNotEquals(componentRegistry0.hashCode(), componentRegistry2.hashCode())
        assertNotEquals(componentRegistry0.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry2.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry4.hashCode())
    }

    @Test
    fun testBuilder() {
        ComponentRegistry().apply {
            assertTrue(fetchers.isEmpty())
            assertTrue(decoders.isEmpty())
            assertTrue(interceptors.isEmpty())
        }

        ComponentRegistry {
            add(Base64UriFetcher.Factory())
            add(TestDecoder.Factory())
            add(DecoderInterceptor())
            assertFailsWith(IllegalArgumentException::class) {
                add(TestInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestInterceptor(100))
            }
        }.apply {
            assertEquals(
                listOf(
                    Base64UriFetcher.Factory(),
                ),
                fetchers
            )
            assertEquals(
                listOf(
                    TestDecoder.Factory(),
                ),
                decoders
            )
            assertEquals(
                listOf(
                    DecoderInterceptor()
                ),
                interceptors
            )
        }.newRegistry {
            addComponents(ComponentRegistry {
                add(TestFetcher.Factory())
                add(TestDecoder2.Factory())
                add(MemoryCacheInterceptor())
                add(TestInterceptor(95))
            })
        }.apply {
            assertEquals(
                listOf(
                    Base64UriFetcher.Factory(),
                    TestFetcher.Factory(),
                ),
                fetchers
            )
            assertEquals(
                listOf(
                    TestDecoder.Factory(),
                    TestDecoder2.Factory(),
                ),
                decoders
            )
            assertEquals(
                listOf(
                    MemoryCacheInterceptor(),
                    TestInterceptor(95),
                    DecoderInterceptor()
                ),
                interceptors
            )
        }
    }
}