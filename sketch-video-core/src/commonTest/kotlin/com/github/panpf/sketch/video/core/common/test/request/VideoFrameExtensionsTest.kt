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

package com.github.panpf.sketch.video.core.common.test.request

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PREFER_VIDEO_COVER_KEY
import com.github.panpf.sketch.request.VIDEO_FRAME_MICROS_KEY
import com.github.panpf.sketch.request.VIDEO_FRAME_PERCENT_KEY
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.request.videoFrameMicros
import com.github.panpf.sketch.request.videoFrameMillis
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VideoFrameExtensionsTest {

    @Test
    fun testVideoFrameMicros() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
            extras!!.entry(VIDEO_FRAME_MICROS_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                this.videoFrameMillis(-1)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFrameMillis(-1)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFrameMicros)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMicros(1000000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMillis(1000)
        }.apply {
            assertEquals(1000000L, videoFrameMicros)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
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

        val key1 = ImageRequest(context, ComposeResImageFiles.mp4.uri).key
        val key2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMillis(500)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, ComposeResImageFiles.mp4.uri)
            .toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFrameMillis(500)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }

    @Suppress("Range")
    @Test
    fun testVideoPercent() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            this.videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
            extras!!.entry(VIDEO_FRAME_PERCENT_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                this.videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                this.videoFramePercent(-1.1f)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFramePercent(-1.1f)
            }
        }

        ImageRequest(context, ComposeResImageFiles.mp4.uri).apply {
            assertNull(videoFramePercent)
        }
        ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFramePercent(0.45f)
        }.apply {
            assertEquals(0.45f, videoFramePercent)
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
                videoFramePercent(-0.1f)
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            ImageRequest(context, ComposeResImageFiles.mp4.uri) {
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

        val key1 = ImageRequest(context, ComposeResImageFiles.mp4.uri).key
        val key2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFramePercent(0.45f)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(
                context,
                ComposeResImageFiles.mp4.uri
            ).toRequestContext(sketch).memoryCacheKey
        val cacheKey2 = ImageRequest(context, ComposeResImageFiles.mp4.uri) {
            videoFramePercent(0.45f)
        }.toRequestContext(sketch).memoryCacheKey
        assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testPreferVideoCover() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertNull(preferVideoCover)
        }.newRequest {
            preferVideoCover()
        }.apply {
            assertTrue(preferVideoCover!!)
            extras!!.entry(PREFER_VIDEO_COVER_KEY)!!.apply {
                assertNotNull(this.requestKey)
                assertNotNull(this.cacheKey)
            }
        }.newRequest {
            preferVideoCover(null)
        }.apply {
            assertNull(preferVideoCover)
        }.newRequest {
            preferVideoCover(true)
        }.apply {
            assertTrue(preferVideoCover!!)
        }.newRequest {
            preferVideoCover(false)
        }.apply {
            assertFalse(preferVideoCover!!)
        }

        ImageOptions().apply {
            assertNull(preferVideoCover)
        }.newOptions {
            preferVideoCover()
        }.apply {
            assertTrue(preferVideoCover!!)
        }.newOptions {
            preferVideoCover(null)
        }.apply {
            assertNull(preferVideoCover)
        }.newOptions {
            preferVideoCover(true)
        }.apply {
            assertTrue(preferVideoCover!!)
        }.newOptions {
            preferVideoCover(false)
        }.apply {
            assertFalse(preferVideoCover!!)
        }
    }
}