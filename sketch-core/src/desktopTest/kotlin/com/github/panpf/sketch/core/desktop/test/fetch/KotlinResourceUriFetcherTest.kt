package com.github.panpf.sketch.core.desktop.test.fetch

import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.KotlinResourceUriFetcher
import com.github.panpf.sketch.fetch.isKotlinResourceUri
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KotlinResourceUriFetcherTest {

    @Test
    fun testNewKotlinResourceUri() {
        assertEquals(
            expected = "file:///kotlin_resource/sample.jpeg",
            actual = newKotlinResourceUri("sample.jpeg")
        )
        assertEquals(
            expected = "file:///kotlin_resource/images/sample.jpeg",
            actual = newKotlinResourceUri("images/sample.jpeg")
        )
        assertEquals(
            expected = "file:///kotlin_resource/sample.jpeg?from=home",
            actual = newKotlinResourceUri("sample.jpeg?from=home")
        )
    }

    @Test
    fun testIsKotlinResourceUri() {
        assertEquals(
            expected = true,
            actual = isKotlinResourceUri("file:///kotlin_resource/sample.jpeg".toUri())
        )
        assertEquals(
            expected = true,
            actual = isKotlinResourceUri("file:///kotlin_resource/images/sample.jpeg".toUri())
        )
        assertEquals(
            expected = true,
            actual = isKotlinResourceUri("file:///kotlin_resource/sample.jpeg?from=home".toUri())
        )

        assertEquals(
            expected = false,
            actual = isKotlinResourceUri("file1:///kotlin_resource/sample.jpeg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isKotlinResourceUri("file:///kotlin_resource1/sample.jpeg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isKotlinResourceUri("file:///sample.jpeg".toUri())
        )
    }

    @Test
    fun testFactoryConstructor() {
        KotlinResourceUriFetcher("sample.jpeg").apply {
            assertEquals("sample.jpeg", resourcePath)
        }
        KotlinResourceUriFetcher("sample.png").apply {
            assertEquals("sample.png", resourcePath)
        }
    }

    @Test
    fun testCompanion() {
        assertEquals("file", KotlinResourceUriFetcher.SCHEME)
        assertEquals("kotlin_resource", KotlinResourceUriFetcher.PATH_ROOT)
    }

    @Test
    fun testFetch() = runTest {
        KotlinResourceUriFetcher("sample.jpeg").apply {
            assertEquals(
                expected = FetchResult(
                    dataSource = KotlinResourceDataSource("sample.jpeg"),
                    mimeType = "image/jpeg"
                ),
                actual = fetch().getOrThrow()
            )
        }

        KotlinResourceUriFetcher("sample.png").apply {
            assertEquals(
                expected = FetchResult(
                    dataSource = KotlinResourceDataSource("sample.png"),
                    mimeType = "image/png"
                ),
                actual = fetch().getOrThrow()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val factory1 = KotlinResourceUriFetcher("sample.jpeg")
        val factory11 = KotlinResourceUriFetcher("sample.jpeg")
        val factory2 = KotlinResourceUriFetcher("sample.png")

        assertEquals(expected = factory1, actual = factory11)
        assertNotEquals(illegal = factory1, actual = factory2)
        assertNotEquals(illegal = factory1, actual = null as Any?)
        assertNotEquals(illegal = factory1, actual = Any())

        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "KotlinResourceUriFetcher('sample.jpeg')",
            actual = KotlinResourceUriFetcher("sample.jpeg").toString()
        )
        assertEquals(
            expected = "KotlinResourceUriFetcher('sample.png')",
            actual = KotlinResourceUriFetcher("sample.png").toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val factory = KotlinResourceUriFetcher.Factory()
        val (context, sketch) = getTestContextAndSketch()

        factory.create(
            ImageRequest(context, "file:///kotlin_resource/sample.jpeg")
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals("sample.jpeg", resourcePath)
        }
        factory.create(
            ImageRequest(context, "file:///kotlin_resource/images/sample.jpeg")
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals("images/sample.jpeg", resourcePath)
        }
        factory.create(
            ImageRequest(context, "file:///kotlin_resource/sample.jpeg?from=home")
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals("sample.jpeg", resourcePath)
        }

        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(context, "file1:///kotlin_resource/sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(context, "file:///kotlin_resource1/sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(context, "file:///sample.jpeg")
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val factory1 = KotlinResourceUriFetcher.Factory()
        val factory11 = KotlinResourceUriFetcher.Factory()
        assertEquals(expected = factory1, actual = factory11)
        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "KotlinResourceUriFetcher",
            actual = KotlinResourceUriFetcher.Factory().toString()
        )
    }
}