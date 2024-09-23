/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.core.common.test.fetch

import com.github.panpf.sketch.fetch.Base64Spec
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.isBase64Uri
import com.github.panpf.sketch.fetch.newBase64Uri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Base64UriFetcherTest {

    @Test
    fun testNewBase64Uri() {
        assertEquals(
            "data:image/png;base64,4y2u1412421089084901240129",
            newBase64Uri("image/png", "4y2u1412421089084901240129")
        )
        assertEquals(
            "data:image/jpeg;base64,4y2u1412421089084901240128",
            newBase64Uri("image/jpeg", "4y2u1412421089084901240128")
        )
    }

    @Test
    fun testNewBase64Uri2() {
        // TODO newBase64Uri(ByteArray)
    }

    @Test
    fun testBase64UriSpec() {
        @Suppress("EnumValuesSoftDeprecate")
        assertEquals(
            expected = "Default, Mime, UrlSafe",
            actual = Base64Spec.values().joinToString()
        )
    }

    @Test
    fun testIsBase64Uri() {
        assertEquals(
            true,
            isBase64Uri("data:image/png;base64,4y2u1412421089084901240129".toUri())
        )
        assertEquals(
            true,
            isBase64Uri("data:img/png;base64,4y2u1412421089084901240129".toUri())
        )

        assertEquals(
            false,
            isBase64Uri("data:application/zip;base64,4y2u1412421089084901240129".toUri())
        )
        assertEquals(
            false,
            isBase64Uri("data:image/png;string,4y2u1412421089084901240129".toUri())
        )
        assertEquals(
            false,
            isBase64Uri("data:img/pngbase64,4y2u1412421089084901240129".toUri())
        )
        assertEquals(
            false,
            isBase64Uri("data:img/png;base644y2u1412421089084901240129".toUri())
        )
    }

    @Test
    fun testConstructor() {
        // TODO test
    }

    @Test
    fun testCompanion() {
        // TODO test
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = Base64UriFetcher.Factory()
        val imageData = "4y2u1412421089084901240129".encodeToByteArray()
        val base64Uri =
            "data:image/png;base64,${kotlin.io.encoding.Base64.Default.encode(imageData)}"

        val fetcher = fetcherFactory.create(
            ImageRequest(context, base64Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is ByteArrayDataSource)
    }

    @Test
    fun testEqualsAndHashCode() {
        // TODO test
    }

    @Test
    fun testToString() {
        // TODO test
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = Base64UriFetcher.Factory()

        val base64Uri1 = "data:image/png;base64,4y2u1412421089084901240129"
        val base64Uri2 = "data:img/png;base64,4y2u1412421089084901240129"
        assertNotEquals(base64Uri1, base64Uri2)
        fetcherFactory.create(
            ImageRequest(context, base64Uri1)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals("image/png", mimeType)
            assertEquals("4y2u1412421089084901240129", dataEncodedString)
        }
        fetcherFactory.create(
            ImageRequest(context, base64Uri2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals("image/png", mimeType)
            assertEquals("4y2u1412421089084901240129", dataEncodedString)
        }
        val base64ErrorUri1 = "content://sample_app/sample"
        val base64ErrorUri2 = "data:image/pngbase64,4y2u1412421089084901240129"
        val base64ErrorUri3 = "data:image/png;base54,4y2u1412421089084901240129"
        val base64ErrorUri4 = "data:image/png;base644y2u1412421089084901240129"

        assertNull(
            fetcherFactory.create(
                ImageRequest(context, base64ErrorUri1)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, base64ErrorUri2)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, base64ErrorUri3)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, base64ErrorUri4)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = Base64UriFetcher.Factory()
        val element11 = Base64UriFetcher.Factory()

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        // TODO test
    }
}