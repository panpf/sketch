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
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentUriFetcherTest {

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"
        val httpUri = "http://sample.com/sample.jpg"

        fetcherFactory.create(sketch, ImageRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, httpUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ContentUriFetcher.Factory()
        val element11 = ContentUriFetcher.Factory()

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
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, contentUri))!!
        val source = runBlocking {
            fetcher.fetch()
        }.getOrThrow().dataSource
        Assert.assertTrue(source is ContentDataSource)
    }
}