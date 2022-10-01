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
package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.extensions.test.toRequestContext
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.SAVE_CELLULAR_TRAFFIC_KEY
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.request.isDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.isIgnoredSaveCellularTraffic
import com.github.panpf.sketch.request.isSaveCellularTraffic
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.util.UnknownException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).saveCellularTraffic()
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        ImageOptions {
            saveCellularTraffic()
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        ImageOptions {
            saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            saveCellularTraffic()
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            saveCellularTraffic(true)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnoreSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).ignoreSaveCellularTraffic()
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic()
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions {
            ignoreSaveCellularTraffic()
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        ImageOptions {
            ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignoreSaveCellularTraffic()
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignoreSaveCellularTraffic(true)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testSetDepthFromSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        ImageOptions {
            depth(NETWORK, "$SAVE_CELLULAR_TRAFFIC_KEY:error")
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).toRequestContext().cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIsCausedBySaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertTrue(isCausedBySaveCellularTraffic(this, DepthException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic(this, UnknownException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK, SAVE_CELLULAR_TRAFFIC_KEY)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic(this, DepthException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic(this, DepthException("")))
        }
    }
}