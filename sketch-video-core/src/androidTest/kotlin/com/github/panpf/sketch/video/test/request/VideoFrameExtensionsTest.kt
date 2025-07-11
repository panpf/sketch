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

package com.github.panpf.sketch.video.test.request

import android.media.MediaMetadataRetriever
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameMillis
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class VideoFrameExtensionsTest {

    @Test
    fun testVideoFrameMicros() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                this.videoFrameMillis(-1)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFrameMillis(-1)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFrameMillis(-1)
            }
        }

        ImageOptions().apply {
            assertNull(videoFrameMicros)
        }
        ImageOptions {
            videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageOptions {
            videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageOptions {
                videoFrameMillis(-1)
            }
        }

        val key1 = ImageRequest(context, ResourceImages.mp4.uri).key
        val key2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMillis(500)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, ResourceImages.mp4.uri)
            .toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameMillis(500)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoPercent() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                this.videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                this.videoFramePercent(-1.1f)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFramePercent(-1.1f)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFramePercent(-1.1f)
            }
        }

        ImageOptions().apply {
            assertNull(videoFramePercent)
        }
        ImageOptions {
            videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageOptions {
                videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageOptions {
                videoFramePercent(-1.1f)
            }
        }

        val key1 = ImageRequest(context, ResourceImages.mp4.uri).key
        val key2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFramePercent(0.45f)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.mp4.uri).toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFramePercent(0.45f)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testVideoOption() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            this.videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                this.videoFrameOption(-1)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                videoFrameOption(-1)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri).apply {
            assertNull(videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_NEXT_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC, videoFrameOption)
        }
        ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }.apply {
            assertEquals(MediaMetadataRetriever.OPTION_CLOSEST_SYNC, videoFrameOption)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ResourceImages.mp4.uri) {
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

        val key1 = ImageRequest(context, ResourceImages.mp4.uri).key
        val key2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.mp4.uri).toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.mp4.uri) {
            videoFrameOption(MediaMetadataRetriever.OPTION_NEXT_SYNC)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }
}