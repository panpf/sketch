package com.github.panpf.sketch.http.okhttp.common.test.fetch

import com.github.panpf.sketch.fetch.OkHttpHttpUriFetcher
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OkHttpHttpUriFetcherTest {

    @Test
    fun testOkHttpHttpUri() {
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

        val factory = OkHttpHttpUriFetcher.Factory()
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
        val httpStack: OkHttpStack = OkHttpStack.Builder().build()
        val httpStack2: OkHttpStack = OkHttpStack.Builder().build()
        val element1 = OkHttpHttpUriFetcher.Factory(httpStack)
        val element11 = OkHttpHttpUriFetcher.Factory(httpStack)
        val element2 = OkHttpHttpUriFetcher.Factory(httpStack2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testFactoryToString() {
        val httpStack: OkHttpStack = OkHttpStack.Builder().build()
        assertEquals(
            expected = "OkHttpHttpUriFetcher(httpStack=$httpStack)",
            actual = OkHttpHttpUriFetcher.Factory(httpStack).toString()
        )
    }
}