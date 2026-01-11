package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.AllFetcher
import com.github.panpf.sketch.test.utils.FakeDecoder
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestRequestInterceptor2
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
    fun testRequestInterceptors() {
        val context = getTestContext()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addRequestInterceptor(TestRequestInterceptor(95))
                addRequestInterceptor(TestRequestInterceptor2())
            }
        }

        Components(ComponentRegistry()).apply {
            assertEquals(
                listOf(),
                getRequestInterceptorList(emptyRequest)
            )
            assertEquals(
                listOf(TestRequestInterceptor2(), TestRequestInterceptor(95)),
                getRequestInterceptorList(notEmptyRequest)
            )
        }

        Components(ComponentRegistry {
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(EngineRequestInterceptor())
        }).apply {
            assertEquals(
                listOf(MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                getRequestInterceptorList(emptyRequest)
            )
            assertEquals(
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
        val context = getTestContext()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addDecodeInterceptor(TestDecodeInterceptor(95))
                addDecodeInterceptor(TestDecodeInterceptor2())
            }
        }

        Components(ComponentRegistry()).apply {
            assertEquals(
                listOf(),
                getDecodeInterceptorList(emptyRequest)
            )
            assertEquals(
                listOf(
                    TestDecodeInterceptor2(),
                    TestDecodeInterceptor(95),
                ),
                getDecodeInterceptorList(notEmptyRequest)
            )
        }

        Components(ComponentRegistry {
            addDecodeInterceptor(TestDecodeInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }).apply {
            assertEquals(
                listOf(
                    TestDecodeInterceptor(),
                    EngineDecodeInterceptor()
                ),
                getDecodeInterceptorList(emptyRequest)
            )
            assertEquals(
                listOf(
                    TestDecodeInterceptor2(),
                    TestDecodeInterceptor(),
                    TestDecodeInterceptor(95),
                    EngineDecodeInterceptor()
                ),
                getDecodeInterceptorList(notEmptyRequest)
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
                        addFetcher(TestHttpUriFetcher.Factory(context))
                    }
                }.toRequestContext(sketch, Size.Empty))
            }
            newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    addFetcher(FileUriFetcher.Factory())
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
                        addFetcher(FileUriFetcher.Factory())
                    }
                }.toRequestContext(sketch, Size.Empty))
            }
            newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                components {
                    addFetcher(TestHttpUriFetcher.Factory(context))
                }
            }.toRequestContext(sketch, Size.Empty))
        }

        Components(ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
            addFetcher(TestHttpUriFetcher.Factory(context))
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
                            addFetcher(AllFetcher.Factory())
                        }
                    }.toRequestContext(sketch, Size.Empty)
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg") {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }.toRequestContext(sketch, Size.Empty)
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg") {
                        components {
                            addFetcher(AllFetcher.Factory())
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
            addFetcher(FileUriFetcher.Factory())
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
            addFetcher(FileUriFetcher.Factory())
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
                        addDecoder(FakeDecoder.Factory())
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
                    addDecoder(TestDecoder.Factory())
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
            addFetcher(FileUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
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
                    addDecoder(TestDecoder.Factory())
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
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        "))",
                toString()
            )
        }
        Components(ComponentRegistry {
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addDecoder(TestDecoder2.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor())
        }).apply {
            assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[Base64UriFetcher,TestFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder2]," +
                        "requestInterceptorList=[EngineRequestInterceptor]," +
                        "decodeInterceptorList=[TestDecodeInterceptor(sortWeight=0),EngineDecodeInterceptor]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val components0 = Components(ComponentRegistry())
        val components1 = Components(ComponentRegistry {
            addFetcher(TestFetcher.Factory())
        })
        val components11 = Components(ComponentRegistry {
            addFetcher(TestFetcher.Factory())
        })
        val components2 = Components(ComponentRegistry {
            addDecoder(TestDecoder.Factory())
        })
        val components4 = Components(ComponentRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
        })
        val components5 = Components(ComponentRegistry {
            addDecodeInterceptor(EngineDecodeInterceptor())
        })

        assertEquals(components0, components0)
        assertEquals(components1, components11)
        assertNotEquals(components1, Any())
        assertNotEquals(components1, null as Any?)
        assertNotEquals(components0, components1)
        assertNotEquals(components0, components2)
        assertNotEquals(components0, components4)
        assertNotEquals(components0, components5)
        assertNotEquals(components1, components2)
        assertNotEquals(components1, components4)
        assertNotEquals(components1, components5)
        assertNotEquals(components2, components4)
        assertNotEquals(components2, components5)
        assertNotEquals(components4, components5)

        assertNotEquals(components0.hashCode(), components1.hashCode())
        assertNotEquals(components0.hashCode(), components2.hashCode())
        assertNotEquals(components0.hashCode(), components4.hashCode())
        assertNotEquals(components0.hashCode(), components5.hashCode())
        assertNotEquals(components1.hashCode(), components2.hashCode())
        assertNotEquals(components1.hashCode(), components4.hashCode())
        assertNotEquals(components1.hashCode(), components5.hashCode())
        assertNotEquals(components2.hashCode(), components4.hashCode())
        assertNotEquals(components2.hashCode(), components5.hashCode())
        assertNotEquals(components4.hashCode(), components5.hashCode())
    }
}