package com.github.panpf.sketch.compose.resources.test.fetch

import com.github.panpf.sketch.fetch.ComposeResourceUriFetcher
import com.github.panpf.sketch.fetch.isComposeResourceUri
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.toUri
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ComposeResourceUriFetcherTest {

    @Test
    fun testNewComposeResourceUri() {
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("jar:file:/data/app/com.github.panpf.sketch4.sample-1==/base.apk!/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("file:/Users/panpf/Workspace/sketch/sample/build/processedResources/desktop/main/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("http://localhost:8080/./composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
            actual = newComposeResourceUri("file:///Users/panpf/Library/Developer/ CoreSimulator/Devices/F828C881-A750-432B-8210-93A84C45E/data/Containers/Bundle/Application/CBD75605-D35E-47A7-B56B-6C5690B062CC/SketchSample.app/compose-resources/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg")
        )
        assertEquals(
            expected = "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home",
            actual = newComposeResourceUri("composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home")
        )

        assertFailsWith(IllegalArgumentException::class) {
            newComposeResourceUri("sample.jpeg")
        }
    }

    @Test
    fun testIsComposeResourceUri() {
        assertEquals(
            expected = true,
            actual = isComposeResourceUri("file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg".toUri())
        )
        assertEquals(
            expected = true,
            actual = isComposeResourceUri("file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpgg?from=home".toUri())
        )

        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file1://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file://compose_resource2/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file:///composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
        assertEquals(
            expected = false,
            actual = isComposeResourceUri("file://composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg".toUri())
        )
    }

    @Test
    fun testFactoryCreate() {
        val factory = ComposeResourceUriFetcher.Factory()
        val (context, sketch) = getTestContextAndSketch()

        factory.create(
            sketch,
            ImageRequest(
                context,
                "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
            )
        )!!
            .apply {
                assertEquals(
                    "composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg",
                    resourcePath
                )
            }
        factory.create(
            sketch,
            ImageRequest(
                context,
                "file://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg?from=home"
            )
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
                sketch,
                ImageRequest(
                    context,
                    "file1://compose_resource/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                )
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                sketch,
                ImageRequest(
                    context,
                    "file://compose_resource1/composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                )
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                sketch,
                ImageRequest(
                    context,
                    "file:///composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                )
            )
        )
        assertEquals(
            expected = null,
            actual = factory.create(
                sketch,
                ImageRequest(
                    context,
                    "file://composeResources/com.github.panpf.sketch.sample.resources/files/huge_china.jpg"
                )
            )
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val factory1 = ComposeResourceUriFetcher.Factory()
        val factory2 = ComposeResourceUriFetcher.Factory()
        assertEquals(expected = factory1, actual = factory2)
        assertEquals(expected = factory1.hashCode(), actual = factory2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ComposeResourceUriFetcher",
            actual = ComposeResourceUriFetcher.Factory().toString()
        )
    }
}