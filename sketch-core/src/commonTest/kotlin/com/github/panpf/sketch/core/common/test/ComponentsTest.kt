package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.cache.internal.MemoryCacheInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.AllFetcher
import com.github.panpf.sketch.test.utils.FakeDecoder
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestInterceptor
import com.github.panpf.sketch.test.utils.TestInterceptor2
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ComponentsTest {

    @Test
    fun testInterceptors() {
        val context = getTestContext()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                add(TestInterceptor(95))
                add(TestInterceptor2())
            }
        }

        Components(ComponentRegistry()).apply {
            assertEquals(
                listOf(),
                getInterceptors(emptyRequest)
            )
            assertEquals(
                listOf(TestInterceptor2(), TestInterceptor(95)),
                getInterceptors(notEmptyRequest)
            )
        }

        Components(ComponentRegistry {
            add(MemoryCacheInterceptor())
            add(DecoderInterceptor())
        }).apply {
            assertEquals(
                listOf(MemoryCacheInterceptor(), DecoderInterceptor()),
                getInterceptors(emptyRequest)
            )
            assertEquals(
                listOf(
                    TestInterceptor2(),
                    MemoryCacheInterceptor(),
                    TestInterceptor(95),
                    DecoderInterceptor()
                ),
                getInterceptors(notEmptyRequest)
            )
        }
    }

    @Test
    fun testNewFetcher() {
        val (context, sketch) = getTestContextAndSketch()

        Components(ComponentRegistry()).apply {
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "file:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg") {
                    components {
                        add(TestHttpUriFetcher.Factory(context))
                    }
                }.toRequestContext(sketch, Size.Empty))
            }
            newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    add(FileUriFetcher.Factory())
                }
            }.toRequestContext(sketch, Size.Empty))

            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                        .toRequestContext(sketch, Size.Empty)
                )
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        add(FileUriFetcher.Factory())
                    }
                }.toRequestContext(sketch, Size.Empty))
            }
            newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                components {
                    add(TestHttpUriFetcher.Factory(context))
                }
            }.toRequestContext(sketch, Size.Empty))
        }

        Components(ComponentRegistry {
            add(FileUriFetcher.Factory())
            add(TestHttpUriFetcher.Factory(context))
        }).apply {
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg").toRequestContext(
                        sketch,
                        Size.Empty
                    )
                ) is FileUriFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg").toRequestContext(
                        sketch,
                        Size.Empty
                    )
                ) is TestHttpUriFetcher
            )
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(
                    ImageRequest(
                        context,
                        "file1:///sdcard/sample.jpeg"
                    ).toRequestContext(sketch, Size.Empty)
                )
            }

            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg") {
                        components {
                            add(AllFetcher.Factory())
                        }
                    }.toRequestContext(sketch, Size.Empty)
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg") {
                        components {
                            add(AllFetcher.Factory())
                        }
                    }.toRequestContext(sketch, Size.Empty)
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg") {
                        components {
                            add(AllFetcher.Factory())
                        }
                    }.toRequestContext(sketch, Size.Empty)
                ) is AllFetcher
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

        Components(ComponentRegistry {
            add(FileUriFetcher.Factory())
        }).apply {
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
                val requestContext = request.toRequestContext(sketch)
                val fetchResult =
                    newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty)).fetch()
                        .getOrThrow()
                withContext(Dispatchers.Main) {
                    newDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        Components(ComponentRegistry {
            add(FileUriFetcher.Factory())
        }).apply {
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
                val requestContext = request.toRequestContext(sketch)
                val fetchResult =
                    newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty)).fetch()
                        .getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                    components {
                        add(FakeDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext(sketch)
                val fetchResult =
                    newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty)).fetch()
                        .getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            val request = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    add(TestDecoder.Factory())
                }
            }
            val requestContext = request.toRequestContext(sketch)
            val fetchResult =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty)).fetch().getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is TestDecoder
            )
        }

        Components(ComponentRegistry {
            add(FileUriFetcher.Factory())
            add(TestDecoder.Factory())
        }).apply {
            val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
            val requestContext = request.toRequestContext(sketch)
            val fetchResult =
                newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty)).fetch().getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is TestDecoder
            )

            val request2 = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    add(TestDecoder.Factory())
                }
            }
            val requestContext2 = request2.toRequestContext(sketch)
            val fetchResult2 =
                newFetcherOrThrow(request2.toRequestContext(sketch, Size.Empty)).fetch()
                    .getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext2, fetchResult2) is TestDecoder
            )
        }
    }

    @Test
    fun testToString() {
        Components(ComponentRegistry()).apply {
            assertEquals(
                "Components(ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        "))",
                toString()
            )
        }
        Components(ComponentRegistry {
            add(Base64UriFetcher.Factory())
            add(TestFetcher.Factory())
            add(TestDecoder.Factory())
            add(TestDecoder2.Factory())
            add(DecoderInterceptor())
        }).apply {
            assertEquals(
                "Components(ComponentRegistry(" +
                        "fetchers=[Base64UriFetcher,TestFetcher]," +
                        "decoders=[TestDecoder,TestDecoder2]," +
                        "interceptors=[DecoderInterceptor]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val components0 = Components(ComponentRegistry())
        val components1 = Components(ComponentRegistry {
            add(TestFetcher.Factory())
        })
        val components11 = Components(ComponentRegistry {
            add(TestFetcher.Factory())
        })
        val components2 = Components(ComponentRegistry {
            add(TestDecoder.Factory())
        })
        val components4 = Components(ComponentRegistry {
            add(DecoderInterceptor())
        })

        assertEquals(components0, components0)
        assertEquals(components1, components11)
        assertNotEquals(components1, Any())
        assertNotEquals(components1, null as Any?)
        assertNotEquals(components0, components1)
        assertNotEquals(components0, components2)
        assertNotEquals(components0, components4)
        assertNotEquals(components1, components2)
        assertNotEquals(components1, components4)
        assertNotEquals(components2, components4)

        assertNotEquals(components0.hashCode(), components1.hashCode())
        assertNotEquals(components0.hashCode(), components2.hashCode())
        assertNotEquals(components0.hashCode(), components4.hashCode())
        assertNotEquals(components1.hashCode(), components2.hashCode())
        assertNotEquals(components1.hashCode(), components4.hashCode())
        assertNotEquals(components2.hashCode(), components4.hashCode())
    }
}