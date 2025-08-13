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

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.request.ImageRequest
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType

/**
 * Create a DecodeConfig based on the parameters related to image quality in the request
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.DecodeConfigTest.testDecodeConfig
 */
actual fun DecodeConfig(
    request: ImageRequest,
    mimeType: String?,
    isOpaque: Boolean,
): DecodeConfig = DecodeConfig().apply {
    val userColorType = request.colorType?.getColorType(mimeType, isOpaque)?.colorType
    if (userColorType != null) {
        colorType = userColorType
    }

    val userColorSpace = request.colorSpace?.getColorSpace(mimeType, isOpaque)?.colorSpace
    if (userColorSpace != null) {
        colorSpace = userColorSpace
    }
}

/**
 * Decode configuration
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.DecodeConfigTest
 */
actual data class DecodeConfig(
    /**
     * If set to a value > 1, requests the decoder to subsample the original
     * image, returning a smaller image to save memory. The sample size is
     * the number of pixels in either dimension that correspond to a single
     * pixel in the decoded bitmap. For example, inSampleSize == 4 returns
     * an image that is 1/4 the width/height of the original, and 1/16 the
     * number of pixels. Any value <= 1 is treated the same as 1. Note: the
     * decoder uses a final value based on powers of 2, any other value will
     * be rounded down to the nearest power of 2.
     */
    var sampleSize: Int? = null,

    /**
     * Color configuration
     */
    var colorType: ColorType? = null,

    /**
     * Color Space
     */
    var colorSpace: ColorSpace? = null,
) {
    override fun toString(): String {
        return "DecodeConfig(sampleSize=$sampleSize, colorType=$colorType, colorSpace=${colorSpace?.name()})"
    }
}