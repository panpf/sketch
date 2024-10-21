package com.github.panpf.sketch.http.ktor2.common.test.fetch

import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
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
    fun testKtorHttpUri() {
        // TODO test
    }

    // TODO test

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
        val httpStack = KtorStack()
        assertEquals(
            expected = "KtorHttpUriFetcher(httpStack=$httpStack)",
            actual = KtorHttpUriFetcher.Factory(httpStack).toString()
        )
    }
}