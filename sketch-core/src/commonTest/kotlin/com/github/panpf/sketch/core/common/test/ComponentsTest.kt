package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Components
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.AllFetcher
import com.github.panpf.sketch.test.utils.FakeDecoder
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor2
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestDecoder2
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.TestRequestInterceptor2
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
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
        val (context, sketch) = getTestContextAndSketch()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addRequestInterceptor(TestRequestInterceptor(95))
                addRequestInterceptor(TestRequestInterceptor2())
            }
        }

        Components(sketch, ComponentRegistry()).apply {
            assertEquals(
                listOf<RequestInterceptor>(),
                getRequestInterceptorList(emptyRequest)
            )
            assertEquals(
                listOf(TestRequestInterceptor2(), TestRequestInterceptor(95)),
                getRequestInterceptorList(notEmptyRequest)
            )
        }

        Components(sketch, ComponentRegistry {
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
        val (context, sketch) = getTestContextAndSketch()
        val emptyRequest = ImageRequest(context, "")
        val notEmptyRequest = ImageRequest(context, "") {
            components {
                addDecodeInterceptor(TestDecodeInterceptor(95))
                addDecodeInterceptor(TestDecodeInterceptor2())
            }
        }

        Components(sketch, ComponentRegistry()).apply {
            assertEquals(
                listOf<DecodeInterceptor>(),
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

        Components(sketch, ComponentRegistry {
            addDecodeInterceptor(TransformationDecodeInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
        }).apply {
            assertEquals(
                listOf(
                    TransformationDecodeInterceptor(),
                    EngineDecodeInterceptor()
                ),
                getDecodeInterceptorList(emptyRequest)
            )
            assertEquals(
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

        Components(sketch, ComponentRegistry()).apply {
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg"))
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg") {
                    components {
                        addFetcher(HttpUriFetcher.Factory())
                    }
                })
            }
            newFetcherOrThrow(ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    addFetcher(FileUriFetcher.Factory())
                }
            })

            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg"))
            }
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                    components {
                        addFetcher(FileUriFetcher.Factory())
                    }
                })
            }
            newFetcherOrThrow(ImageRequest(context, "http://sample.com/sample.jpeg") {
                components {
                    addFetcher(HttpUriFetcher.Factory())
                }
            })
        }

        Components(sketch, ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
            addFetcher(HttpUriFetcher.Factory())
        }).apply {
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg")
                ) is FileUriFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg")
                ) is HttpUriFetcher
            )
            assertFailsWith(IllegalArgumentException::class) {
                newFetcherOrThrow(ImageRequest(context, "file1:///sdcard/sample.jpeg"))
            }

            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg") {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "http://sample.com/sample.jpeg") {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }
                ) is AllFetcher
            )
            assertTrue(
                newFetcherOrThrow(
                    ImageRequest(context, "file:///sdcard/sample.jpeg") {
                        components {
                            addFetcher(AllFetcher.Factory())
                        }
                    }
                ) is AllFetcher
            )
        }
    }

    @Test
    fun testNewDecoder() = runTest {
        val context = getTestContext()
        val sketch = newSketch()

        Components(sketch, ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
        }).apply {
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = newFetcherOrThrow(request).fetch().getOrThrow()
                withContext(Dispatchers.Main) {
                    newDecoderOrThrow(requestContext, fetchResult)
                }
            }
        }

        Components(sketch, ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
        }).apply {
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = newFetcherOrThrow(request).fetch().getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            assertFails {
                val request = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                    components {
                        addDecoder(FakeDecoder.Factory())
                    }
                }
                val requestContext = request.toRequestContext(sketch)
                val fetchResult = newFetcherOrThrow(request).fetch().getOrThrow()
                newDecoderOrThrow(requestContext, fetchResult)
            }
            val request = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    addDecoder(TestDecoder.Factory())
                }
            }
            val requestContext = request.toRequestContext(sketch)
            val fetchResult = newFetcherOrThrow(request).fetch().getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is TestDecoder
            )
        }

        Components(sketch, ComponentRegistry {
            addFetcher(FileUriFetcher.Factory())
            addDecoder(TestDecoder.Factory())
        }).apply {
            val request = ImageRequest(context, "file:///sdcard/sample.jpeg")
            val requestContext = request.toRequestContext(sketch)
            val fetchResult = newFetcherOrThrow(request).fetch().getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext, fetchResult) is TestDecoder
            )

            val request2 = ImageRequest(context, "file:///sdcard/sample.jpeg") {
                components {
                    addDecoder(TestDecoder.Factory())
                }
            }
            val requestContext2 = request2.toRequestContext(sketch)
            val fetchResult2 = newFetcherOrThrow(request2).fetch().getOrThrow()
            assertTrue(
                newDecoderOrThrow(requestContext2, fetchResult2) is TestDecoder
            )
        }
    }

    @Test
    fun testToString() {
        val sketch = newSketch()
        Components(sketch, ComponentRegistry()).apply {
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
        Components(sketch, ComponentRegistry {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
            addDecoder(TestDecoder.Factory())
            addDecoder(TestDecoder2.Factory())
            addRequestInterceptor(EngineRequestInterceptor())
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
        }).apply {
            assertEquals(
                "Components(ComponentRegistry(" +
                        "fetcherFactoryList=[HttpUriFetcher,Base64UriFetcher,TestFetcher]," +
                        "decoderFactoryList=[TestDecoder,TestDecoder2]," +
                        "requestInterceptorList=[EngineRequestInterceptor(sortWeight=100)]," +
                        "decodeInterceptorList=[TransformationDecodeInterceptor(sortWeight=90),EngineDecodeInterceptor(sortWeight=100)]" +
                        "))",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val sketch = newSketch()
        val components0 = Components(sketch, ComponentRegistry())
        val components1 = Components(sketch, ComponentRegistry {
            addFetcher(HttpUriFetcher.Factory())
        })
        val components11 = Components(sketch, ComponentRegistry {
            addFetcher(HttpUriFetcher.Factory())
        })
        val components2 = Components(sketch, ComponentRegistry {
            addDecoder(TestDecoder.Factory())
        })
        val components4 = Components(sketch, ComponentRegistry {
            addRequestInterceptor(EngineRequestInterceptor())
        })
        val components5 = Components(sketch, ComponentRegistry {
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