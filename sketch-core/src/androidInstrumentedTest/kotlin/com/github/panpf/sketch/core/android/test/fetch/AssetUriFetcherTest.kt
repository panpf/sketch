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
import com.github.panpf.sketch.fetch.isAssetUri
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.buffer
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertFalse(actual = isAssetUri("file1:///android_asset/sample.jpeg".toUri()))
        assertFalse(actual = isAssetUri("file://sample.com/android_asset/sample.jpeg".toUri()))
        assertFalse(actual = isAssetUri("file:///android_asset1/sample.jpeg".toUri()))
        assertTrue(actual = isAssetUri("file:///android_asset/sample.jpeg".toUri()))
    }

    @Test
    fun testCompanion() {
        assertEquals("file", AssetUriFetcher.SCHEME)
        assertEquals("android_asset", AssetUriFetcher.PATH_ROOT)
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = ResourceImages.jpeg.uri

        val fetcher = fetcherFactory.create(
            ImageRequest(context, assetUri)
                .toRequestContext(sketch, Size.Empty)
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is AssetDataSource)

        source.openSource().buffer().use { it.readByteArray() }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = AssetUriFetcher(context, "file.jpeg")
        val element11 = AssetUriFetcher(context, "file.jpeg")
        val element2 = AssetUriFetcher(context, "file.png")

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        assertEquals(
            expected = "AssetUriFetcher('file.jpeg')",
            actual = AssetUriFetcher(context, "file.jpeg").toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = ResourceImages.jpeg.uri
        val assetUri2 = "${ResourceImages.png.uri}?from=bing"
        val assetUri3 = "${ResourceImages.animGif.uri}#/main/"
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(
            ImageRequest(context, assetUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri3)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(
            ImageRequest(context, assetUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri3)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(
            ImageRequest(context, assetUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(
            ImageRequest(context, assetUri3)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        assertNull(
            fetcherFactory.create(
                ImageRequest(context, httpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, contentUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
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
    fun testFactoryToString() {
        assertEquals(
            expected = "AssetUriFetcher",
            actual = AssetUriFetcher.Factory().toString()
        )
    }
}