package com.github.panpf.sketch.core.android.test

import com.github.panpf.sketch.ComponentRegistry.Builder
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher.Factory
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor

@RunWith(AndroidJUnit4::class)
class ComponentRegistryBuilderTest {

    @Test
    fun testAddFetcher() {
        Builder().build().apply {
            Assert.assertTrue(fetcherFactoryList.isEmpty())
        }

        Builder().apply {
            addFetcher(Factory())
            addFetcher(Base64UriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addFetcher(TestFetcher.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    Factory(),
                    Base64UriFetcher.Factory(),
                    ResourceUriFetcher.Factory(),
                    TestFetcher.Factory(),
                ),
                fetcherFactoryList
            )
        }
    }

    @Test
    fun testAddDecoder() {
        Builder().build().apply {
            Assert.assertTrue(decoderFactoryList.isEmpty())
        }

        Builder().apply {
            addDecoder(DrawableDecoder.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
            addDecoder(TestDecoder.Factory())
        }.build().apply {
            Assert.assertEquals(
                listOf(
                    DrawableDecoder.Factory(),
                    BitmapFactoryDecoder.Factory(),
                    TestDecoder.Factory(),
                ),
                decoderFactoryList
            )
        }
    }

    @Test
    fun testAddRequestInterceptor() {
        Builder().build().apply {
            Assert.assertTrue(requestInterceptorList.isEmpty())
        }

        Builder().apply {
            addRequestInterceptor(EngineRequestInterceptor())
            addRequestInterceptor(MemoryCacheRequestInterceptor())
            addRequestInterceptor(TestRequestInterceptor(95))

            assertThrow(java.lang.IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addRequestInterceptor(TestRequestInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
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
    fun testAddDecodeInterceptor() {
        Builder().build().apply {
            Assert.assertTrue(decodeInterceptorList.isEmpty())
        }

        Builder().apply {
            addDecodeInterceptor(EngineDecodeInterceptor())
            addDecodeInterceptor(TransformationDecodeInterceptor())
            addDecodeInterceptor(TestDecodeInterceptor(95))
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(-1))
            }
            assertThrow(java.lang.IllegalArgumentException::class) {
                addDecodeInterceptor(TestDecodeInterceptor(100))
            }
        }.build().apply {
            Assert.assertEquals(
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