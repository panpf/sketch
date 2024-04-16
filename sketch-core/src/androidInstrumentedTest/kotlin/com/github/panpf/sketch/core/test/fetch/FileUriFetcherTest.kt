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
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "file:///sdcard/sample.jpg",
            newFileUri("/sdcard/sample.jpg")
        )
        Assert.assertEquals(
            "file:///sdcard1/sample1.jpg",
            newFileUri("/sdcard1/sample1.jpg")
        )

        Assert.assertEquals(
            "file:///sdcard/sample.jpg",
            newFileUri(File("/sdcard/sample.jpg"))
        )
        Assert.assertEquals(
            "file:///sdcard1/sample1.jpg",
            newFileUri(File("/sdcard1/sample1.jpg"))
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val filePath = "/sdcard/sample.jpg"
        val filePathUri = "file:///sdcard/sample.jpg"
        val filePath2 = "/sdcard/sample .jpg"
        val filePath2Uri = "file:///sdcard/sample%20.jpg"
        val filePath3 = "/sdcard/sample.png"
        val filePath3Uri = "file:///sdcard/sample.png?from=bing"
        val filePath4 = "/sdcard/sample.gif"
        val filePath4Uri = "file:///sdcard/sample.gif#/main/"
        val ftpUri = "ftp:///sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val fetcherFactory = FileUriFetcher.Factory()

        fetcherFactory.create(sketch, ImageRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            Assert.assertEquals(filePathUri, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            Assert.assertEquals(filePath2, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            Assert.assertEquals(filePath2Uri, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            Assert.assertEquals(filePath3Uri, this.request.uriString)
            Assert.assertEquals(filePath3, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            Assert.assertEquals(filePath4Uri, this.request.uriString)
            Assert.assertEquals(filePath4, this.file.path)
        }

        fetcherFactory.create(sketch, ImageRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            Assert.assertEquals(filePathUri, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            Assert.assertEquals(filePath2, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            Assert.assertEquals(filePath2Uri, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            Assert.assertEquals(filePath3Uri, this.request.uriString)
            Assert.assertEquals(filePath3, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            Assert.assertEquals(filePath4Uri, this.request.uriString)
            Assert.assertEquals(filePath4, this.file.path)
        }

        fetcherFactory.create(sketch, ImageRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            Assert.assertEquals(filePathUri, this.request.uriString)
            Assert.assertEquals(filePath, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            Assert.assertEquals(filePath2, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            Assert.assertEquals(filePath2Uri, this.request.uriString)
            Assert.assertEquals(filePath2, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            Assert.assertEquals(filePath3Uri, this.request.uriString)
            Assert.assertEquals(filePath3, this.file.path)
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            Assert.assertEquals(filePath4Uri, this.request.uriString)
            Assert.assertEquals(filePath4, this.file.path)
        }

        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, ftpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, ImageRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = FileUriFetcher.Factory()
        val element11 = FileUriFetcher.Factory()

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
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///sdcard/sample.jpg"

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, fileUri))!!
        val source = runBlocking {
            fetcher.fetch()
        }.getOrThrow().dataSource
        Assert.assertTrue(source is FileDataSource)
    }
}