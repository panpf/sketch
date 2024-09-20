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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.util.simpleName


/**
 * Create a DecodeConfig based on the parameters related to image quality in the request
 *
 * @see com.github.panpf.sketch.core.android.test.decode.DecodeConfigTest.testDecodeConfig
 */
fun DecodeConfig(
    request: ImageRequest,
    mimeType: String? = null,
    isOpaque: Boolean = false
): DecodeConfig =
    DecodeConfig().apply {
        if (request.preferQualityOverSpeed) {
            inPreferQualityOverSpeed = true
        }

        val userColorType = request.colorType?.getColorType(mimeType, isOpaque)?.colorType
        if (userColorType != null) {
            colorType = userColorType
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val userColorSpace = request.colorSpace?.getColorSpace(mimeType, isOpaque)?.colorSpace
            if (userColorSpace != null) {
                colorSpace = userColorSpace
            }
        }
    }

/**
 * Android Bitmap Decode configuration
 *
 * @see com.github.panpf.sketch.core.android.test.decode.DecodeConfigTest
 */
data class DecodeConfig(
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
     * If this is non-null, the decoder will try to decode into this
     * internal configuration. If it is null, or the request cannot be met,
     * the decoder will try to pick the best matching config based on the
     * system's screen depth, and characteristics of the original image such
     * as if it has per-pixel alpha (requiring a config that also does).
     *
     * Image are loaded with the [Bitmap.Config.ARGB_8888] config by
     * default.
     */
    var colorType: ColorType? = null,

    /**
     *
     * If this is non-null, the decoder will try to decode into this
     * color space. If it is null, or the request cannot be met,
     * the decoder will pick either the color space embedded in the image
     * or the color space best suited for the requested image configuration
     * (for instance [sRGB][ColorSpace.Named.SRGB] for
     * [Bitmap.Config.ARGB_8888] configuration and
     * [EXTENDED_SRGB][ColorSpace.Named.EXTENDED_SRGB] for
     * [Bitmap.Config.RGBA_F16]).
     *
     *
     * Only [ColorSpace.Model.RGB] color spaces are
     * currently supported. An `IllegalArgumentException` will
     * be thrown by the decode methods when setting a non-RGB color space
     * such as [Lab][ColorSpace.Named.CIE_LAB].
     *
     *
     * The specified color space's transfer function must be
     * an [ICC parametric curve][ColorSpace.Rgb.TransferParameters]. An
     * `IllegalArgumentException` will be thrown by the decode methods
     * if calling [ColorSpace.Rgb.getTransferParameters] on the
     * specified color space returns null.
     *
     *
     * After decode, the bitmap's color space is stored in
     * [BitmapFactory.Options.outColorSpace].
     */
    @RequiresApi(VERSION_CODES.O)
    var colorSpace: ColorSpace? = null,

    /**
     * In {@link android.os.Build.VERSION_CODES#M} and below, if
     * inPreferQualityOverSpeed is set to true, the decoder will try to
     * decode the reconstructed image to a higher quality even at the
     * expense of the decoding speed. Currently the field only affects JPEG
     * decode, in the case of which a more accurate, but slightly slower,
     * IDCT method will be used instead.
     */
    @Deprecated("As of android.os.Build.VERSION_CODES#N, this is ignored. The output will always be high quality.")
    // TODO rename to preferQualityOverSpeed
    var inPreferQualityOverSpeed: Boolean? = null,
) {
    override fun toString(): String {
        return if (VERSION.SDK_INT >= VERSION_CODES.O) {
            "DecodeConfig(sampleSize=$sampleSize, colorType=$colorType, colorSpace=${colorSpace?.simpleName}, inPreferQualityOverSpeed=$inPreferQualityOverSpeed)"
        } else {
            "DecodeConfig(sampleSize=$sampleSize, colorType=$colorType, inPreferQualityOverSpeed=$inPreferQualityOverSpeed)"
        }
    }
}

/**
 * Convert [DecodeConfig] to [BitmapFactory.Options]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.DecodeConfigTest.testToBitmapOptions
 */
fun DecodeConfig.toBitmapOptions(): BitmapFactory.Options {
    val options = BitmapFactory.Options()
    sampleSize?.let {
        options.inSampleSize = it
    }
    @Suppress("DEPRECATION")
    if (VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed == true) {
        options.inPreferQualityOverSpeed = true
    }
    colorType?.let {
        options.inPreferredConfig = it
    }
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace?.let {
            options.inPreferredColorSpace = it
        }
    }
    return options
}