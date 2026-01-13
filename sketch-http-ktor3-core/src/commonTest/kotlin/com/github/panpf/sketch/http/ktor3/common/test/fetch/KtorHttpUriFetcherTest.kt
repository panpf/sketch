package com.github.panpf.sketch.http.ktor3.common.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class KtorHttpUriFetcherTest {

    @Test
    fun testSupportKtorHttpUri() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportKtorHttpUri()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[KtorHttpUriFetcher]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportKtorHttpUri()
            supportKtorHttpUri()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[KtorHttpUriFetcher,KtorHttpUriFetcher]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = KtorStack()
        KtorHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
    }

    @Test
    fun testEqualsAndHashCode() {
        val (context, sketch) = getTestContextAndSketch()
        val sketch2 = Sketch(context)
        val httpStack = KtorStack()
        val httpStack2 = KtorStack()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val request2 = request.newRequest { memoryCachePolicy(DISABLED) }
        val element1 = KtorHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element11 = KtorHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey")
        val element2 = KtorHttpUriFetcher(sketch2, httpStack, request, "downloadCacheKey")
        val element3 = KtorHttpUriFetcher(sketch, httpStack2, request, "downloadCacheKey")
        val element4 = KtorHttpUriFetcher(sketch, httpStack, request2, "downloadCacheKey")
        val element5 = KtorHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey2")

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "http://sample.com/sample.jpg")
        val httpStack = KtorStack()
        val httpUriFetcher = KtorHttpUriFetcher(sketch, httpStack, request, "downloadCacheKey2")
        assertEquals(
            expected = "KtorHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request, downloadCacheKey='downloadCacheKey2')",
            actual = httpUriFetcher.toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val httpUri = "http://sample.com/sample.jpg"
        val httpsUri = "https://sample.com/sample.jpg"
        val ftpUri = "ftp://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val factory = KtorHttpUriFetcher.Factory()
        assertNotNull(
            factory.create(
                ImageRequest(context, httpsUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNotNull(
            factory.create(
                ImageRequest(context, httpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            factory.create(
                ImageRequest(context, ftpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            factory.create(
                ImageRequest(context, contentUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val httpStack = KtorStack()
        val httpStack2 = KtorStack()
        val element1 = KtorHttpUriFetcher.Factory(httpStack)
        val element11 = KtorHttpUriFetcher.Factory(httpStack)
        val element2 = KtorHttpUriFetcher.Factory(httpStack2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "KtorHttpUriFetcher",
            actual = KtorHttpUriFetcher.Factory(KtorStack()).toString()
        )
    }
}