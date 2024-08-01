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

import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResourceUriFetcherTest {

    @Test
    fun testNewResourceUri() {
        assertEquals(
            expected = "android.resource:///drawable/ic_launcher",
            actual = newResourceUri("drawable", "ic_launcher")
        )
        assertEquals(
            expected = "android.resource:///55345",
            actual = newResourceUri(55345)
        )

        assertEquals(
            expected = "android.resource://com.github.panpf.sketch.sample/drawable/ic_launcher",
            actual = newResourceUri("com.github.panpf.sketch.sample", "drawable", "ic_launcher")
        )

        assertEquals(
            expected = "android.resource://com.github.panpf.sketch.sample/55345",
            actual = newResourceUri("com.github.panpf.sketch.sample", 55345)
        )
    }

    // TODO isResourceUri

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val testAppPackage = context.packageName
        val fetcherFactory = ResourceUriFetcher.Factory()
        val resourceUriByName = newResourceUri(
            resType = "drawable",
            resName = "ic_launcher"
        )
        val resourceUriById = newResourceUri(
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        )
        val resourceUriByName2 = newResourceUri(
            packageName = testAppPackage,
            resType = "drawable",
            resName = "ic_launcher"
        )
        val resourceUriById2 = newResourceUri(
            packageName = testAppPackage,
            resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
        )
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        assertNotNull(fetcherFactory.create(sketch, ImageRequest(context, resourceUriByName)))
        assertNotNull(fetcherFactory.create(sketch, ImageRequest(context, resourceUriById)))
        assertNotNull(fetcherFactory.create(sketch, ImageRequest(context, resourceUriByName2)))
        assertNotNull(fetcherFactory.create(sketch, ImageRequest(context, resourceUriById2)))
        assertNull(fetcherFactory.create(sketch, ImageRequest(context, httpUri)))
        assertNull(fetcherFactory.create(sketch, ImageRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ResourceUriFetcher.Factory()
        val element11 = ResourceUriFetcher.Factory()

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as ResourceUriFetcher.Factory?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFetch() {
        val (context, sketch) = getTestContextAndSketch()
        val resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher

        assertTrue(
            newResourceUri("drawable", "ic_launcher")
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow().dataSource is DrawableDataSource
        )
        assertTrue(
            newResourceUri(resId)
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow().dataSource is DrawableDataSource
        )

        assertTrue(
            newResourceUri(context.packageName, "drawable", "ic_launcher")
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow().dataSource is DrawableDataSource
        )
        assertTrue(
            newResourceUri(context.packageName, resId)
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow().dataSource is DrawableDataSource
        )

        assertTrue(
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.raw.sample)
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow().dataSource is ResourceDataSource
        )

        assertFailsWith(Resources.NotFoundException::class) {
            "${ResourceUriFetcher.SCHEME}://"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }
        assertFailsWith(Resources.NotFoundException::class) {
            "${ResourceUriFetcher.SCHEME}://fakePackageName"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }

        assertFailsWith(NumberFormatException::class) {
            "${ResourceUriFetcher.SCHEME}:///errorResId"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }
        assertFailsWith(NumberFormatException::class) {
            "${ResourceUriFetcher.SCHEME}://${context.packageName}/errorResId"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }

        assertFailsWith(Resources.NotFoundException::class) {
            newResourceUri("drawable1", "ic_launcher")
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }
        assertFailsWith(Resources.NotFoundException::class) {
            newResourceUri("drawable", "ic_launcher1")
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }

        assertFailsWith(Resources.NotFoundException::class) {
            "${ResourceUriFetcher.SCHEME}:///drawable/ic_launcher/error"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }
        assertFailsWith(Resources.NotFoundException::class) {
            "${ResourceUriFetcher.SCHEME}://${context.packageName}/drawable/ic_launcher/error"
                .let { ResourceUriFetcher(sketch, ImageRequest(context, it), it.toUri()) }
                .let { runBlocking { it.fetch() } }
                .getOrThrow()
        }
    }

    // TODO test
}