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

package com.github.panpf.sketch.extensions.core.common.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.request.isDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.isIgnoredSaveCellularTraffic
import com.github.panpf.sketch.request.isSaveCellularTraffic
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SaveCellularTrafficExtensionsCommonTest {

    @Test
    fun testSaveCellularTraffic() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            assertFalse(isSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.apply {
            assertTrue(isSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic(false)
        }.apply {
            assertFalse(isSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.apply {
            assertTrue(isSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic(false)
        }.apply {
            assertFalse(isSaveCellularTraffic)
        }

        ImageOptions().apply {
            assertFalse(isSaveCellularTraffic)
        }

        ImageOptions {
            saveCellularTraffic()
        }.apply {
            assertTrue(isSaveCellularTraffic)
        }
        ImageOptions {
            saveCellularTraffic(false)
        }.apply {
            assertFalse(isSaveCellularTraffic)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            saveCellularTraffic()
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
            saveCellularTraffic(true)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnoreSaveCellularTraffic() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic()
        }.apply {
            assertTrue(isIgnoredSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic(false)
        }.apply {
            assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic()
        }.apply {
            assertTrue(isIgnoredSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic(false)
        }.apply {
            assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions().apply {
            assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions {
            ignoreSaveCellularTraffic()
        }.apply {
            assertTrue(isIgnoredSaveCellularTraffic)
        }
        ImageOptions {
            ignoreSaveCellularTraffic(false)
        }.apply {
            assertFalse(isIgnoredSaveCellularTraffic)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            ignoreSaveCellularTraffic()
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
            ignoreSaveCellularTraffic(true)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testSetDepthFromSaveCellularTraffic() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertTrue(isDepthFromSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertTrue(isDepthFromSaveCellularTraffic)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions().apply {
            assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertTrue(isDepthFromSaveCellularTraffic)
        }
        ImageOptions {
            depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            assertFalse(isDepthFromSaveCellularTraffic)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.key
        assertEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)

        val key11 = ImageRequest(context, ResourceImages.svg.uri).key
        val key22 = ImageRequest(context, ResourceImages.svg.uri) {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.key
        assertNotEquals(key11, key22)

        val cacheKey11 = ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey22 = ImageRequest(context, ResourceImages.svg.uri) {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey11, cacheKey22)
    }

    @Test
    fun testIsCausedBySaveCellularTraffic() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertTrue(isCausedBySaveCellularTraffic(this, DepthException("")))
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertFalse(isCausedBySaveCellularTraffic(this, Exception("")))
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            assertFalse(isCausedBySaveCellularTraffic(this, DepthException("")))
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
        }.apply {
            assertFalse(isCausedBySaveCellularTraffic(this, DepthException("")))
        }
    }
}