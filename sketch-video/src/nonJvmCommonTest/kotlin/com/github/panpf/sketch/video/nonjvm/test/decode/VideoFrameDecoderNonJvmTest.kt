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

package com.github.panpf.sketch.video.nonjvm.test.decode

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VideoFrameDecoderNonJvmTest {

    @Test
    fun testSupportVideoFrame() {
        val factory = VideoFrameDecoder.Factory()
        assertEquals("VideoFrameDecoder", factory.key)
        assertTrue(factory.toString().contains("VideoFrameDecoder"))
    }

    @Test
    fun testDecode() = runTest {
        runInNewSketchWithUse(
            builder = { components { supportVideoFrame() } }
        ) { context, sketch ->
            val request = ImageRequest(context, ResourceImages.mp4.uri)
            val result = withContext(Dispatchers.Default) {
                request.decode(sketch, VideoFrameDecoder.Factory())
            }
            assertEquals(ImageInfo(500, 250, "video/mp4"), result.imageInfo)
            assertEquals(500, result.image.width)
            assertEquals(250, result.image.height)
            assertEquals(LOCAL, result.dataFrom)
            assertNull(result.transformeds)
        }
    }
}
