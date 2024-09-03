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

package com.github.panpf.sketch.core.android.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.test.runTest
import okio.buffer
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AssetUriFetcherTest {

    @Test
    fun testNewAssetUri() {
        assertEquals(
            "file:///android_asset/sample.jpeg",
            ResourceImages.jpeg.uri
        )
        assertEquals(
            "file:///android_asset/images/sample.png",
            newAssetUri("images/sample.png")
        )
    }

    @Test
    fun testIsAssetUri() {
        // TODO test
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = ResourceImages.jpeg.uri
        val assetUri2 = "${ResourceImages.png.uri}?from=bing"
        val assetUri3 = "${ResourceImages.animGif.uri}#/main/"
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            assertEquals(assetUri, request.uri.toString())
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            assertEquals(assetUri2, request.uri.toString())
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            assertEquals(assetUri3, request.uri.toString())
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            assertEquals(assetUri, request.uri.toString())
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            assertEquals(assetUri2, request.uri.toString())
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            assertEquals(assetUri3, request.uri.toString())
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            assertEquals(assetUri, request.uri.toString())
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            assertEquals(assetUri2, request.uri.toString())
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            assertEquals(assetUri3, request.uri.toString())
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        assertNull(fetcherFactory.create(sketch, ImageRequest(context, httpUri)))
        assertNull(fetcherFactory.create(sketch, ImageRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AssetUriFetcher.Factory()
        val element11 = AssetUriFetcher.Factory()

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = ResourceImages.jpeg.uri

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is AssetDataSource)

        source.openSource().buffer().use { it.readByteArray() }
    }

    // TODO test
}