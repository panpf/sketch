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

package com.github.panpf.sketch.video.core.android.test.request

import android.media.MediaMetadataRetriever
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.VIDEO_FRAME_OPTION_KEY
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class VideoFrameExtensionsAndroidTest {

    @Test
    fun testVideoOption() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
            extras!!.entry(VIDEO_FRAME_OPTION_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                this.videoFrameOption(-1)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFrameOption(-1)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFrameOption(-1)
            }
        }

        ImageOptions().apply {
            assertNull(videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
            extras!!.entry(VIDEO_FRAME_OPTION_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageOptions {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageOptions {
                videoFrameOption(-1)
            }
        }

        val key1 = ImageRequest(context, ComposeResImageFiles.mp4.uri).key
        val key2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(
                context,
                ComposeResImageFiles.mp4.uri
            ).toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }
}