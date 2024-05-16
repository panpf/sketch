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
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "asset://sample.jpeg",
            MyImages.jpeg.uri
        )
        Assert.assertEquals(
            "asset://fd5717876ab046b8aa889c9aaac4b56c.png",
            newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.png")
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = MyImages.jpeg.uri
        val assetUri2 = "${MyImages.png.uri}?from=bing"
        val assetUri3 = "${MyImages.animGif.uri}#/main/"
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            Assert.assertEquals(assetUri, request.uri)
            Assert.assertEquals(MyImages.jpeg.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri)
            Assert.assertEquals(MyImages.png.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri)
            Assert.assertEquals(MyImages.animGif.fileName, assetFileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            Assert.assertEquals(assetUri, request.uri)
            Assert.assertEquals(MyImages.jpeg.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri)
            Assert.assertEquals(MyImages.png.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri)
            Assert.assertEquals(MyImages.animGif.fileName, assetFileName)
        }

        fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!.apply {
            Assert.assertEquals(assetUri, request.uri)
            Assert.assertEquals(MyImages.jpeg.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri2))!!.apply {
            Assert.assertEquals(assetUri2, request.uri)
            Assert.assertEquals(MyImages.png.fileName, assetFileName)
        }
        fetcherFactory.create(sketch, ImageRequest(context, assetUri3))!!.apply {
            Assert.assertEquals(assetUri3, request.uri)
            Assert.assertEquals(MyImages.animGif.fileName, assetFileName)
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
    fun testFetch() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = MyImages.jpeg.uri

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, assetUri))!!
        val source = runBlocking {
            fetcher.fetch()
        }.getOrThrow().dataSource
        Assert.assertTrue(source is AssetDataSource)
    }
}