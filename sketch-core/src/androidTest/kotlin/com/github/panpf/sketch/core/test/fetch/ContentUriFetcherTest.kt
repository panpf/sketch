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
package com.github.panpf.sketch.core.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentUriFetcherTest {

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"
        val httpUri = "http://sample.com/sample.jpg"

        fetcherFactory.create(sketch, LoadRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DownloadRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, httpUri)))
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
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, contentUri))!!
        val source = runBlocking {
            fetcher.fetch()
        }.getOrThrow().dataSource
        Assert.assertTrue(source is ContentDataSource)
    }
}