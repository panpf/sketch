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
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.isContentUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import androidx.core.net.toUri as toAndroidUri

@RunWith(AndroidJUnit4::class)
class ContentUriFetcherTest {

    @Test
    fun testIsContentUri() {
        assertFalse(actual = isContentUri(uri = "content1://sample_app/sample.jpg".toUri()))
        assertTrue(actual = isContentUri(uri = "content://sample_app/sample.jpg".toUri()))
    }

    @Test
    fun testCompanion() {
        assertEquals("content", ContentUriFetcher.SCHEME)
    }

    @Test
    fun testFetch() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"

        val fetcher = fetcherFactory.create(
            ImageRequest(context, contentUri)
                .toRequestContext(sketch, Size.Empty)
        )!!
        val source = fetcher.fetch().getOrThrow().dataSource
        assertTrue(source is ContentDataSource)
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ContentUriFetcher(context, "content://sample_app/sample.jpg".toAndroidUri())
        val element11 = ContentUriFetcher(context, "content://sample_app/sample.jpg".toAndroidUri())
        val element2 = ContentUriFetcher(context, "content://sample_app/sample.png".toAndroidUri())

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
            expected = "ContentUriFetcher('content://sample_app/sample.jpg')",
            actual = ContentUriFetcher(
                context,
                "content://sample_app/sample.jpg".toAndroidUri()
            ).toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"
        val httpUri = "http://sample.com/sample.jpg"

        fetcherFactory.create(
            ImageRequest(context, contentUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(
            ImageRequest(context, contentUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(
            ImageRequest(context, contentUri)
                .toRequestContext(sketch, Size.Empty)
        )!!.apply {
            assertEquals(contentUri, this.contentUri.toString())
        }
        assertNull(
            fetcherFactory.create(
                ImageRequest(context, httpUri)
                    .toRequestContext(sketch, Size.Empty)
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ContentUriFetcher.Factory()
        val element11 = ContentUriFetcher.Factory()

        assertEquals(element1, element11)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "ContentUriFetcher",
            actual = ContentUriFetcher.Factory().toString()
        )
    }
}