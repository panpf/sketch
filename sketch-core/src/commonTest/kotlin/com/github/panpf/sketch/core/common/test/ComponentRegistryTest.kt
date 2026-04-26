package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.DecoderInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.isNotEmpty
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ThumbnailInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeDecoder
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestFetcher2
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestInterceptor
import com.github.panpf.sketch.test.utils.TestInterceptor2
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

        ComponentRegistry {
            disabledFetcher(TestFetcher.Factory::class)
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            disabledDecoder(TestDecoder.Factory::class)
        }.apply {
            assertFalse(isEmpty())
            assertTrue(isNotEmpty())
        }

        ComponentRegistry {
            disabledInterceptor(TestInterceptor::class)
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
                    ImageRequest(context, "file:///sdcard/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            }

            assertNull(
                newFetcherOrNull(
                    ImageRequest(context, "file:///sdcard/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            )
            assertNull(
                newFetcherOrNull(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            )
        }

        ComponentRegistry {
            add(FileUriFetcher.Factory())
        }.apply {
            newFetcherOrThrow(
                ImageRequest(context, "file:///sdcard/sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            }

            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(context, "file:///sdcard/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            )
            assertNull(
                newFetcherOrNull(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            )
        }

        ComponentRegistry {
            add(FileUriFetcher.Factory())
            add(TestHttpUriFetcher.Factory(context))
        }.apply {
            newFetcherOrThrow(
                ImageRequest(context, "file:///sdcard/sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )
            newFetcherOrThrow(
                ImageRequest(context, "http://sample.com/sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )

            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(context, "file:///sdcard/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            )
            assertNotNull(
                newFetcherOrNull(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
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
            disabledFetcher(TestFetcher2.Factory::class)
            disabledDecoder(TestDecoder2.Factory::class)
            disabledInterceptor(TestInterceptor2::class)
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher]," +
                        "decoders=[TestDecoder]," +
                        "interceptors=[TestInterceptor]," +
                        "disabledFetchers=[${TestFetcher2.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder2.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor2::class}]" +
                        ")",
                toString()
            )
        }
        val componentRegistry1 = ComponentRegistry {
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
            add(TestInterceptor())
            add(DecoderInterceptor())
            disabledFetcher(TestFetcher2.Factory::class)
            disabledDecoder(TestDecoder2.Factory::class)
            disabledInterceptor(TestInterceptor2::class)
            disabledInterceptor(ThumbnailInterceptor::class)
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher]," +
                        "decoders=[TestDecoder]," +
                        "interceptors=[TestInterceptor,DecoderInterceptor]," +
                        "disabledFetchers=[${TestFetcher2.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder2.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor2::class},${ThumbnailInterceptor::class}]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry1)

        val componentRegistry2 = componentRegistry.merged(componentRegistry1).apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher]," +
                        "decoders=[TestDecoder]," +
                        "interceptors=[TestInterceptor,DecoderInterceptor]," +
                        "disabledFetchers=[${TestFetcher2.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder2.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor2::class},${ThumbnailInterceptor::class}]" +
                        ")",
                toString()
            )
        }
        assertNotEquals(componentRegistry, componentRegistry2)
        assertEquals(componentRegistry1, componentRegistry2)

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
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
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
            add(TestInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher,Base64UriFetcher]," +
                        "decoders=[TestDecoder,TestDecoder2]," +
                        "interceptors=[TestInterceptor,DecoderInterceptor]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
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
            add(TestInterceptor())
            disabledFetcher(TestFetcher.Factory::class)
            disabledDecoder(TestDecoder.Factory::class)
            disabledInterceptor(TestInterceptor::class)
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[Base64UriFetcher]," +
                        "decoders=[TestDecoder2]," +
                        "interceptors=[DecoderInterceptor]," +
                        "disabledFetchers=[${TestFetcher.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor::class}]" +
                        ")",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val componentRegistry1 = ComponentRegistry()
        val componentRegistry11 = ComponentRegistry()
        val componentRegistry2 = ComponentRegistry {
            add(TestFetcher.Factory())
        }
        val componentRegistry3 = ComponentRegistry {
            add(TestDecoder.Factory())
        }
        val componentRegistry4 = ComponentRegistry {
            add(DecoderInterceptor())
        }
        val componentRegistry5 = ComponentRegistry {
            disabledFetcher(TestFetcher.Factory::class)
        }
        val componentRegistry6 = ComponentRegistry {
            disabledDecoder(TestDecoder.Factory::class)
        }
        val componentRegistry7 = ComponentRegistry {
            disabledInterceptor(DecoderInterceptor::class)
        }

        assertEquals(componentRegistry1, componentRegistry11)
        assertNotEquals(componentRegistry1, componentRegistry2)
        assertNotEquals(componentRegistry1, componentRegistry3)
        assertNotEquals(componentRegistry1, componentRegistry4)
        assertNotEquals(componentRegistry1, componentRegistry5)
        assertNotEquals(componentRegistry1, componentRegistry6)
        assertNotEquals(componentRegistry1, componentRegistry7)
        assertNotEquals(componentRegistry2, componentRegistry3)
        assertNotEquals(componentRegistry2, componentRegistry4)
        assertNotEquals(componentRegistry2, componentRegistry5)
        assertNotEquals(componentRegistry2, componentRegistry6)
        assertNotEquals(componentRegistry2, componentRegistry7)
        assertNotEquals(componentRegistry3, componentRegistry4)
        assertNotEquals(componentRegistry3, componentRegistry5)
        assertNotEquals(componentRegistry3, componentRegistry6)
        assertNotEquals(componentRegistry3, componentRegistry7)
        assertNotEquals(componentRegistry4, componentRegistry5)
        assertNotEquals(componentRegistry5, componentRegistry6)
        assertNotEquals(componentRegistry6, componentRegistry7)
        assertNotEquals(componentRegistry5, componentRegistry6)
        assertNotEquals(componentRegistry5, componentRegistry7)
        assertNotEquals(componentRegistry6, componentRegistry7)
        assertNotEquals(componentRegistry1, Any())
        assertNotEquals(componentRegistry1, null as Any?)

        assertEquals(componentRegistry1.hashCode(), componentRegistry11.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry2.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry3.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry6.hashCode())
        assertNotEquals(componentRegistry1.hashCode(), componentRegistry7.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry3.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry6.hashCode())
        assertNotEquals(componentRegistry2.hashCode(), componentRegistry7.hashCode())
        assertNotEquals(componentRegistry3.hashCode(), componentRegistry4.hashCode())
        assertNotEquals(componentRegistry3.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry3.hashCode(), componentRegistry6.hashCode())
        assertNotEquals(componentRegistry3.hashCode(), componentRegistry7.hashCode())
        assertNotEquals(componentRegistry4.hashCode(), componentRegistry5.hashCode())
        assertNotEquals(componentRegistry5.hashCode(), componentRegistry6.hashCode())
        assertNotEquals(componentRegistry6.hashCode(), componentRegistry7.hashCode())
        assertNotEquals(componentRegistry5.hashCode(), componentRegistry6.hashCode())
        assertNotEquals(componentRegistry5.hashCode(), componentRegistry7.hashCode())
        assertNotEquals(componentRegistry6.hashCode(), componentRegistry7.hashCode())
    }

    @Test
    fun testBuilder() {
        ComponentRegistry().apply {
            assertTrue(fetchers.isEmpty())
            assertTrue(decoders.isEmpty())
            assertTrue(interceptors.isEmpty())
            assertTrue(disabledFetchers.isEmpty())
            assertTrue(disabledDecoders.isEmpty())
            assertTrue(disabledInterceptors.isEmpty())
        }

        ComponentRegistry {
            assertFailsWith(IllegalArgumentException::class) {
                add(TestInterceptor(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestInterceptor(101))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestDecoder.Factory(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestDecoder.Factory(101))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestFetcher.Factory(-1))
            }
            assertFailsWith(IllegalArgumentException::class) {
                add(TestFetcher.Factory(101))
            }
        }

        ComponentRegistry {
            add(Base64UriFetcher.Factory())
            add(TestFetcher.Factory())
            add(TestDecoder.Factory(100))
            add(TestDecoder2.Factory(0))
            add(DecoderInterceptor())
            add(TestInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher,Base64UriFetcher]," +
                        "decoders=[TestDecoder2,TestDecoder]," +
                        "interceptors=[TestInterceptor,DecoderInterceptor]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                toString()
            )
        }

        ComponentRegistry {
            add(Base64UriFetcher.Factory(), TestFetcher.Factory())
            add(TestDecoder.Factory(100), TestDecoder2.Factory(0))
            add(DecoderInterceptor(), TestInterceptor())
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[TestFetcher,Base64UriFetcher]," +
                        "decoders=[TestDecoder2,TestDecoder]," +
                        "interceptors=[TestInterceptor,DecoderInterceptor]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                toString()
            )
        }

        ComponentRegistry {
            add(Base64UriFetcher.Factory())
            add(TestFetcher.Factory())
            add(TestDecoder.Factory(100))
            add(TestDecoder2.Factory(0))
            add(DecoderInterceptor())
            add(TestInterceptor())
            disabledFetcher(TestFetcher.Factory::class)
            disabledFetcher(TestFetcher2.Factory::class)
            disabledDecoder(TestDecoder.Factory::class)
            disabledDecoder(FakeDecoder.Factory::class)
            disabledInterceptor(TestInterceptor::class)
            disabledInterceptor(ThumbnailInterceptor::class)
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[Base64UriFetcher]," +
                        "decoders=[TestDecoder2]," +
                        "interceptors=[DecoderInterceptor]," +
                        "disabledFetchers=[${TestFetcher.Factory::class},${TestFetcher2.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder.Factory::class},${FakeDecoder.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor::class},${ThumbnailInterceptor::class}]" +
                        ")",
                toString()
            )
        }

        ComponentRegistry {
            add(Base64UriFetcher.Factory(), TestFetcher.Factory())
            add(TestDecoder.Factory(100), TestDecoder2.Factory(0))
            add(DecoderInterceptor(), TestInterceptor())
            disabledFetcher(TestFetcher.Factory::class, TestFetcher2.Factory::class)
            disabledDecoder(TestDecoder.Factory::class, FakeDecoder.Factory::class)
            disabledInterceptor(TestInterceptor::class, ThumbnailInterceptor::class)
        }.apply {
            assertEquals(
                "ComponentRegistry(" +
                        "fetchers=[Base64UriFetcher]," +
                        "decoders=[TestDecoder2]," +
                        "interceptors=[DecoderInterceptor]," +
                        "disabledFetchers=[${TestFetcher.Factory::class},${TestFetcher2.Factory::class}]," +
                        "disabledDecoders=[${TestDecoder.Factory::class},${FakeDecoder.Factory::class}]," +
                        "disabledInterceptors=[${TestInterceptor::class},${ThumbnailInterceptor::class}]" +
                        ")",
                toString()
            )
        }
    }
}