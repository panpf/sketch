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
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetUriFetcherTest {

    @Test
    fun testNewAssetUri() {
        Assert.assertEquals(
            "file:///android_asset/sample.jpeg",
            ResourceImages.jpeg.uri
        )
        Assert.assertEquals(
            "file:///android_asset/images/sample.png",
            newAssetUri("images/sample.png")
        )
    }

    // TODO isAssetUri

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
            Assert.assertEquals(assetUri, request.uri.toString())
            Assert.assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri.toString())
            Assert.assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri.toString())
            Assert.assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            Assert.assertEquals(assetUri, request.uri.toString())
            Assert.assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri.toString())
            Assert.assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri.toString())
            Assert.assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            Assert.assertEquals(assetUri, request.uri.toString())
            Assert.assertEquals(ResourceImages.jpeg.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri.toString())
            Assert.assertEquals(ResourceImages.png.resourceName, fileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri.toString())
            Assert.assertEquals(ResourceImages.animGif.resourceName, fileName)
        }

        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AssetUriFetcher.Factory()
        val element11 = AssetUriFetcher.Factory()

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFetch() = runTest{
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = ResourceImages.jpeg.uri

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!
        val source = fetcher.fetch().getOrThrow().dataSource
        Assert.assertTrue(source is AssetDataSource)

        source.openSource().buffer().use { it.readByteArray() }
    }

    // TODO test
}