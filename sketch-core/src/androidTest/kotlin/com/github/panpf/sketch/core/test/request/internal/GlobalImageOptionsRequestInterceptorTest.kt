@file:Suppress("DEPRECATION")

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
package com.github.panpf.sketch.core.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.internal.GlobalImageOptionsRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.resources.AssetImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GlobalImageOptionsRequestInterceptorTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndNewSketch {
            globalImageOptions(ImageOptions {
                depth(MEMORY)
                downloadCachePolicy(WRITE_ONLY)
            })
        }

        val request = LoadRequest(context, AssetImages.jpeg.uri).apply {
            Assert.assertEquals(Depth.NETWORK, depth)
            Assert.assertEquals(CachePolicy.ENABLED, downloadCachePolicy)
        }
        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(sketch, request, false).let { it as LoadResult }.apply {
                Assert.assertEquals(MEMORY, this.request.depth)
                Assert.assertEquals(WRITE_ONLY, this.request.downloadCachePolicy)
            }
        }
    }

    @Test
    fun testSortWeight() {
        GlobalImageOptionsRequestInterceptor().apply {
            Assert.assertEquals(80, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = GlobalImageOptionsRequestInterceptor()
        val element11 = GlobalImageOptionsRequestInterceptor()
        val element12 = GlobalImageOptionsRequestInterceptor()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element12)
        Assert.assertNotSame(element12, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element12)
        Assert.assertEquals(element12, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element12.hashCode())
        Assert.assertEquals(element12.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        GlobalImageOptionsRequestInterceptor().apply {
            Assert.assertEquals(
                "GlobalImageOptionsRequestInterceptor(sortWeight=80)",
                toString()
            )
        }
    }
}