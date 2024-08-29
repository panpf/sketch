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

import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FileUriFetcherTest {

    @Test
    fun test() {
        assertEquals(
            expected = "file:///sdcard/sample%20s.jpeg",
            actual = "file:///sdcard/sample%20s.jpeg".toUri().toString()
        )
        assertEquals(
            expected = "/sdcard/sample s.jpeg",
            actual = "file:///sdcard/sample%20s.jpeg".toUri().path
        )
    }

    @Test
    fun testNewFileUri() {
        assertEquals(
            expected = "file:///sdcard/sample.jpg",
            actual = newFileUri("/sdcard/sample.jpg")
        )
        assertEquals(
            expected = "file:///sdcard1/sample1.jpg",
            actual = newFileUri("/sdcard1/sample1.jpg".toPath())
        )
    }

    @Test
    fun testIsFileUri() {
        // TODO isFileUri
    }

    @Test
    fun testConstructor() {
        // TODO test
    }

    @Test
    fun testCompanion() {
        // TODO test
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///sdcard/sample.jpg"

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, fileUri))!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is FileDataSource)
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
            assertEquals(filePath, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            assertEquals(filePathUri, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            assertEquals(filePath2, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            assertEquals(filePath2Uri, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            assertEquals(filePath3Uri, this.request.uri.toString())
            assertEquals(filePath3, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            assertEquals(filePath4Uri, this.request.uri.toString())
            assertEquals(filePath4, this.path.toString())
        }

        fetcherFactory.create(sketch, ImageRequest(context, filePath))!!.apply {
            assertEquals(filePath, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            assertEquals(filePathUri, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            assertEquals(filePath2, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            assertEquals(filePath2Uri, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            assertEquals(filePath3Uri, this.request.uri.toString())
            assertEquals(filePath3, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            assertEquals(filePath4Uri, this.request.uri.toString())
            assertEquals(filePath4, this.path.toString())
        }

        fetcherFactory.create(sketch, ImageRequest(context, filePath))!!.apply {
            assertEquals(filePath, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePathUri))!!.apply {
            assertEquals(filePathUri, this.request.uri.toString())
            assertEquals(filePath, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2))!!.apply {
            assertEquals(filePath2, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath2Uri))!!.apply {
            assertEquals(filePath2Uri, this.request.uri.toString())
            assertEquals(filePath2, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath3Uri))!!.apply {
            assertEquals(filePath3Uri, this.request.uri.toString())
            assertEquals(filePath3, this.path.toString())
        }
        fetcherFactory.create(sketch, ImageRequest(context, filePath4Uri))!!.apply {
            assertEquals(filePath4Uri, this.request.uri.toString())
            assertEquals(filePath4, this.path.toString())
        }

        assertNull(fetcherFactory.create(sketch, ImageRequest(context, ftpUri)))
        assertNull(fetcherFactory.create(sketch, ImageRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = FileUriFetcher.Factory()
        val element11 = FileUriFetcher.Factory()

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {

    }
}