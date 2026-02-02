package com.github.panpf.sketch.compose.resources.common.test.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.isComposeResourceUri
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.supportComposeResources
import com.github.panpf.sketch.images.Res
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.buffer
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalResourceApi::class)
class ComposeResourceUriFetcherTest {

    @Test
    fun testSupportComposeResources() {
        ComponentRegistry.Builder().apply {
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }

            supportComposeResources()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[ComposeResourceUriFetcher]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }

            supportComposeResources()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetchers=[ComposeResourceUriFetcher,ComposeResourceUriFetcher]," +
                            "decoders=[]," +
                            "interceptors=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testNewComposeResourceUri() {
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("jar:file:/data/app/com.github.panpf.sketch4.sample-1==/base.apk!/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("file:/Users/panpf/Workspace/sketch/sample/build/processedResources/desktop/main/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("http://localhost:8080/./composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("file:///Users/panpf/Library/Developer/ CoreSimulator/Devices/F828C881-A750-432B-8210-93A84C45E/data/Containers/Bundle/Application/CBD75605-D35E-47A7-B56B-6C5690B062CC/SketchSample.app/compose-resources/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home",
            actual = newComposeResourceUri("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home")
        )

        assertFailsWith(IllegalArgumentException::class) {
            newComposeResourceUri("moon.jpeg")
        }
    }

    @Test
    fun testIsComposeResourceUri() {
        assertEquals(
            expected = true,
            actual = isComposeResourceUri("file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg".toUri())
        )
        assertEquals(
            expected = true,
            actual = isComposeResourceUri("file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg?from=home".toUri())
        )

        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg?from=home".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file1:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file:///compose_resource2/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file:///composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file:///composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
    }

    @Test
    fun testCompanion() {
        assertEquals("file", ComposeResourceUriFetcher.SCHEME)
        assertEquals("compose_resource", ComposeResourceUriFetcher.PATH_ROOT)
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val fetcherFactory = ComposeResourceUriFetcher.Factory()
        val request =
            ImageRequest(context, newComposeResourceUri(Res.getUri("drawable/moon.jpeg")))
        val requestContext = request.toRequestContext(sketch, Size.Empty)
        val fetcher = fetcherFactory.create(requestContext)!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is ByteArrayDataSource)

        source.openSource().buffer().use { it.readByteArray() }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 =
            ComposeResourceUriFetcher("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        val element11 =
            ComposeResourceUriFetcher("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        val element2 =
            ComposeResourceUriFetcher("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.png")

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ComposeResourceUriFetcher('composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg')",
            actual = ComposeResourceUriFetcher("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg").toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val factory = ComposeResourceUriFetcher.Factory()
        val (context, sketch) = getTestContextAndSketch()

        factory.create(
            ImageRequest(
                context,
                "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
            ).toRequestContext(sketch, Size.Empty)
        )!!
            .apply {
                assertEquals(
                    "composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
                    resourcePath
                )
            }
        factory.create(
            ImageRequest(
                context,
                "file:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home"
            ).toRequestContext(sketch, Size.Empty)
        )!!
            .apply {
                assertEquals(
                    "composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
                    resourcePath
                )
            }

        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(
                    context,
                    "file1:///compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                ).toRequestContext(sketch, Size.Empty)
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(
                    context,
                    "file:///compose_resource1/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                ).toRequestContext(sketch, Size.Empty)
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                ImageRequest(
                    context,
                    "file:///composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                ).toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val factory1 = ComposeResourceUriFetcher.Factory()
        val factory2 = ComposeResourceUriFetcher.Factory()
        assertEquals(expected = factory1, actual = factory2)
        assertEquals(expected = factory1.hashCode(), actual = factory2.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "ComposeResourceUriFetcher",
            actual = ComposeResourceUriFetcher.Factory().toString()
        )
    }
}