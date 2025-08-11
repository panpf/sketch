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

@file:Suppress("UnnecessaryVariable")

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Rect
import okio.Closeable

/**
 * Encapsulates the most basic decoding operations for use by the decoder
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.BitmapFactoryDecodeHelperTest
 * @see com.github.panpf.sketch.video.test.decode.internal.VideoFrameDecodeHelperTest
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.internal.FFmpegVideoFrameDecodeHelperTest
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.internal.SkiaDecodeHelperTest
 * @see com.github.panpf.sketch.core.android.test.decode.internal.SkiaBlurHashDecodeHelperTest
 * @see com.github.panpf.sketch.core.android.test.decode.internal.AndroidBlurHashDecodeHelperTest
 */
interface DecodeHelper : Closeable {

    /**
     * Image width, height, and format information
     */
    val imageInfo: ImageInfo

    /**
     * Whether the region decoding is supported
     */
    val supportRegion: Boolean

    /**
     * Decode the entire image
     */
    fun decode(sampleSize: Int): Image

    /**
     * Decode the specified region
     */
    fun decodeRegion(region: Rect, sampleSize: Int): Image

}