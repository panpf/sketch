/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.core.android.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.isBase64Uri
import com.github.panpf.sketch.fetch.newBase64Uri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.io.encoding.ExperimentalEncodingApi

@RunWith(AndroidJUnit4::class)
class Base64UriFetcherTest {

    @Test
    fun testNewBase64Uri() {
        Assert.assertEquals(
            "data:image/png;base64,4y2u1412421089084901240129",
            newBase64Uri("image/png", "4y2u1412421089084901240129")
        )
        Assert.assertEquals(
            "data:image/jpeg;base64,4y2u1412421089084901240128",
            newBase64Uri("image/jpeg", "4y2u1412421089084901240128")
        )
        // TODO newBase64Uri(ByteArray)
    }

    // TODO base64Specification

    @Test
    fun testIsBase64Uri() {
        Assert.assertEquals(
            true,
            isBase64Uri("data:image/png;base64,4y2u1412421089084901240129".toUri())
        )
        Assert.assertEquals(
            true,
            isBase64Uri("data:img/png;base64,4y2u1412421089084901240129".toUri())
        )

        Assert.assertEquals(
            false,
            isBase64Uri("data:application/zip;base64,4y2u1412421089084901240129".toUri())
        )
        Assert.assertEquals(
            false,
            isBase64Uri("data:image/png;string,4y2u1412421089084901240129".toUri())
        )
        Assert.assertEquals(
            false,
            isBase64Uri("data:img/pngbase64,4y2u1412421089084901240129".toUri())
        )
        Assert.assertEquals(
            false,
            isBase64Uri("data:img/png;base644y2u1412421089084901240129".toUri())
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = Base64UriFetcher.Factory()

        val base64Uri1 = "data:image/png;base64,4y2u1412421089084901240129"
        val base64Uri2 = "data:img/png;base64,4y2u1412421089084901240129"
        Assert.assertNotEquals(base64Uri1, base64Uri2)
        fetcherFactory.create(sketch, ImageRequest(context, base64Uri1))!!.apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals("4y2u1412421089084901240129", imageDataBase64String)
        }
        fetcherFactory.create(sketch, ImageRequest(context, base64Uri2))!!.apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals("4y2u1412421089084901240129", imageDataBase64String)
        }
        val base64ErrorUri1 = "content://sample_app/sample"
        val base64ErrorUri2 = "data:image/pngbase64,4y2u1412421089084901240129"
        val base64ErrorUri3 = "data:image/png;base54,4y2u1412421089084901240129"
        val base64ErrorUri4 = "data:image/png;base644y2u1412421089084901240129"

        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, base64ErrorUri1)))
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, base64ErrorUri2)))
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, base64ErrorUri3)))
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, base64ErrorUri4)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = Base64UriFetcher.Factory()
        val element11 = Base64UriFetcher.Factory()

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun testFetch() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = Base64UriFetcher.Factory()
        val imageData = "4y2u1412421089084901240129".toByteArray()
        val base64Uri =
            "data:image/png;base64,${kotlin.io.encoding.Base64.Default.encode(imageData)}"

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, base64Uri))!!
        val source = runBlocking {
            fetcher.fetch()
        }.getOrThrow().dataSource
        Assert.assertTrue(source is ByteArrayDataSource)
    }

    // TODO test
}