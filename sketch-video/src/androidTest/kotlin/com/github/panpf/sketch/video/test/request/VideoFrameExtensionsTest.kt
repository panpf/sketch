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
package com.github.panpf.sketch.video.test.request

import android.media.MediaMetadataRetriever
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameMillis
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.video.test.decode.toRequestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoFrameExtensionsTest {

    @Test
    fun testVideoFrameMicros() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (LoadRequest(context, newAssetUri("sample.mp4")) as ImageRequest).apply {
            Assert.assertNull(videoFrameMicros)
        }
        (LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameMicros(1000000)
        } as ImageRequest).apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        (LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameMillis(1000)
        } as ImageRequest).apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                (this as ImageRequest.Builder).videoFrameMillis(-1)
            }
        }

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameMicros)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                videoFrameMillis(-1)
            }
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameMicros)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample.mp4")) {
                videoFrameMillis(-1)
            }
        }

        ImageOptions().apply {
            Assert.assertNull(videoFrameMicros)
        }
        ImageOptions {
            videoFrameMicros(1000000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        ImageOptions {
            videoFrameMillis(1000)
        }.apply {
            Assert.assertEquals(1000000L, videoFrameMicros)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageOptions {
                videoFrameMillis(-1)
            }
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(500)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameMillis(500)
        }.toRequestContext().cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoPercentDuration() {
        val context = InstrumentationRegistry.getInstrumentation().context

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFramePercent)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFramePercent(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercent)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                (this as ImageRequest.Builder).videoFramePercent(-0.1f)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                (this as ImageRequest.Builder).videoFramePercent(-1.1f)
            }
        }

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFramePercent)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercent(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercent)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                videoFramePercent(-0.1f)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                videoFramePercent(-1.1f)
            }
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFramePercent)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercent(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercent)
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample.mp4")) {
                videoFramePercent(-0.1f)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample.mp4")) {
                videoFramePercent(-1.1f)
            }
        }

        ImageOptions().apply {
            Assert.assertNull(videoFramePercent)
        }
        ImageOptions {
            videoFramePercent(0.45f)
        }.apply {
            Assert.assertEquals(0.45f, videoFramePercent)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageOptions {
                videoFramePercent(-0.1f)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            ImageOptions {
                videoFramePercent(-1.1f)
            }
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercent(0.45f)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFramePercent(0.45f)
        }.toRequestContext().cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoOption() {
        val context = InstrumentationRegistry.getInstrumentation().context

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            (this as ImageRequest.Builder).videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                (this as ImageRequest.Builder).videoFrameOption(-1)
            }
        }

        LoadRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertThrow(IllegalArgumentException::class) {
            LoadRequest(context, newAssetUri("sample.mp4")) {
                videoFrameOption(-1)
            }
        }

        DisplayRequest(context, newAssetUri("sample.mp4")).apply {
            Assert.assertNull(videoFrameOption)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        DisplayRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample.mp4")) {
                videoFrameOption(-1)
            }
        }

        ImageOptions().apply {
            Assert.assertNull(videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            Assert.assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageOptions {
                videoFrameOption(-1)
            }
        }

        val key1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.mp4")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.mp4")) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.toRequestContext().cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }
}