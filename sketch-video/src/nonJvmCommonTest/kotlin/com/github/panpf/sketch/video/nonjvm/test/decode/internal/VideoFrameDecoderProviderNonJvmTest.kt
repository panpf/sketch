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

package com.github.panpf.sketch.video.nonjvm.test.decode.internal

import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.internal.VideoFrameDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class VideoFrameDecoderProviderNonJvmTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = VideoFrameDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(decoderFactory is VideoFrameDecoder.Factory, decoderFactory.toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = VideoFrameDecoderProvider()
        val element11 = VideoFrameDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = VideoFrameDecoderProvider()
        assertTrue(decoderProvider.toString().contains("VideoFrameDecoderProvider"), decoderProvider.toString())
        assertTrue(decoderProvider.toString().contains("@"), decoderProvider.toString())
    }
}
