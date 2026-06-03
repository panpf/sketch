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

package com.github.panpf.sketch.decode.internal

import android.os.Build
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Rect
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode

class AwxkeeAvifDecodeHelper(
    val request: ImageRequest,
    val dataSource: DataSource,
    val mimeType: String
) : DecodeHelper {

    private val heifCoder = HeifCoder()
    val data by lazy { dataSource.toByteArray() }
    private val _imageInfo by lazy {
        val size = requireNotNull(heifCoder.getSize(data)) {
            "Invalid avif/heif image"
        }
        ImageInfo(size.width, size.height, mimeType)
    }

    override suspend fun getImageInfo(): ImageInfo = _imageInfo

    override suspend fun isSupportRegion(): Boolean = false

    override suspend fun decode(sampleSize: Int): Image {
        val sampledBitmapSize = calculateSampledBitmapSize(
            imageSize = _imageInfo.size,
            sampleSize = sampleSize,
            mimeType = _imageInfo.mimeType
        )
        val decodeConfig = DecodeConfig(request, _imageInfo.mimeType, isOpaque = false)
        val preferredColorConfig = decodeConfig.colorType.preferredColorConfig()
        val bitmap = if (sampleSize > 1) {
            heifCoder.decodeSampled(
                byteArray = data,
                scaledWidth = sampledBitmapSize.width,
                scaledHeight = sampledBitmapSize.height,
                preferredColorConfig = preferredColorConfig,
                scaleMode = ScaleMode.FIT
            )
        } else {
            heifCoder.decode(
                byteArray = data,
                preferredColorConfig = preferredColorConfig
            )
        }
        return bitmap.asImage()
    }

    override suspend fun decodeRegion(region: Rect, sampleSize: Int): Image =
        throw UnsupportedOperationException("AwxkeeAvifDecoder does not support region decoding")

    private fun ColorType?.preferredColorConfig(): PreferredColorConfig = when {
        this == ColorType.ARGB_8888 -> PreferredColorConfig.RGBA_8888
        this == ColorType.RGB_565 -> PreferredColorConfig.RGB_565
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == ColorType.RGBA_F16 -> PreferredColorConfig.RGBA_F16
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this == ColorType.HARDWARE -> PreferredColorConfig.HARDWARE
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && this == ColorType.RGBA_1010102 -> PreferredColorConfig.RGBA_1010102
        else -> PreferredColorConfig.RGBA_8888
    }

    override fun close() {

    }
}