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
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FileUriFetcherTest {

    @Test
    fun testNewFileUri() {
        if (Path.DIRECTORY_SEPARATOR == "/") {
            assertEquals(
                expected = "file:///storage/sample.jpg",
                actual = newFileUri("/storage/sample.jpg")
            )
            assertEquals(
                expected = "file:///storage1/sample1.jpg",
                actual = newFileUri("file:///storage1/sample1.jpg")
            )
        } else {
            assertEquals(
                expected = "D:\\test\\relative\\image.jpg",
                actual = newFileUri("D:\\test\\relative\\image.jpg")
            )
            assertEquals(
                expected = "\\\\qnap\\photos\\dog.jpg",
                actual = newFileUri("\\\\qnap\\photos\\dog.jpg")
            )
        }
    }

    @Test
    fun testNewFileUri2() {
        if (Path.DIRECTORY_SEPARATOR == "/") {
            assertEquals(
                expected = "file:///storage/sample.jpg",
                actual = newFileUri("/storage/sample.jpg".toPath())
            )
            assertEquals(
                expected = "file:/storage1/sample1.jpg",
                actual = newFileUri("file:///storage1/sample1.jpg".toPath())
            )
        } else {
            assertEquals(
                expected = "D:\\test\\relative\\image.jpg",
                actual = newFileUri("D:\\test\\relative\\image.jpg".toPath())
            )
            assertEquals(
                expected = "\\\\qnap\\photos\\dog.jpg",
                actual = newFileUri("\\\\qnap\\photos\\dog.jpg".toPath())
            )
        }
    }

    @Test
    fun testIsFileUri() {
        assertTrue(isFileUri("/storage/sample.jpg".toUri()))
        assertTrue(isFileUri("file:///storage/sample.jpg".toUri()))
        assertTrue(isFileUri("D:\\test\\relative\\image.jpg".toUri("\\")))
        assertTrue(isFileUri("\\\\qnap\\photos\\dog.jpg".toUri("\\")))
        assertFalse(isFileUri("http://sample.com/sample.jpg".toUri()))
    }

    @Test
    fun testConstructor() {
        FileUriFetcher(path = "/storage/sample.jpg".toPath(), fileSystem = defaultFileSystem())
    }

    @Test
    fun testCompanion() {
        assertEquals("file", FileUriFetcher.SCHEME)
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///storage/sample.jpg"

        val fetcher = fetcherFactory.create(
            ImageRequest(context, fileUri)
                .toRequestContext(sketch, Size.Empty)
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is FileDataSource)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = FileUriFetcher("/storage/sample.jpg".toPath())
        val element11 = FileUriFetcher("/storage/sample.jpg".toPath())
        val element2 =
            FileUriFetcher("/storage/sample.png".toPath())

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "FileUriFetcher('/storage/sample.jpg')",
            actual = FileUriFetcher("/storage/sample.jpg".toPath()).toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val filePath = "/storage/sample.jpg"
        val filePathUri = "file:///storage/sample.jpg"
        val filePath2 = "/storage/sample .jpg"
        val filePath2Uri = "file:///storage/sample%20.jpg"
        val filePath3 = "/storage/sample.png"
        val filePath3Uri = "file:///storage/sample.png?from=bing"
        val filePath4 = "/storage/sample.gif"
        val filePath4Uri = "file:///storage/sample.gif#/main/"
        val ftpUri = "ftp:///sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val factory = FileUriFetcher.Factory()

        factory.create(
            ImageRequest(context, filePath)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePathUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath3Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath3, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath4Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath4, this.path.toString())
        }

        factory.create(
            ImageRequest(context, filePath)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePathUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath3Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath3, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath4Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath4, this.path.toString())
        }

        factory.create(
            ImageRequest(context, filePath)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePathUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath2Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath2, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath3Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath3, this.path.toString())
        }
        factory.create(
            ImageRequest(context, filePath4Uri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(filePath4, this.path.toString())
        }

        assertNull(
            factory.create(
                ImageRequest(context, ftpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
        assertNull(
            factory.create(
                ImageRequest(context, contentUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = FileUriFetcher.Factory()
        val element11 = FileUriFetcher.Factory()

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(expected = "FileUriFetcher", actual = FileUriFetcher.Factory().toString())
    }
}