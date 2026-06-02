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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.correctExifOrientation
import com.github.panpf.sketch.util.sketchSize
import com.github.panpf.sketch.util.toBitmap
import com.github.panpf.sketch.util.toNSData
import platform.UIKit.UIImage

/**
 * Decode images using UIImage
 *
 * @see com.github.panpf.sketch.core.ios.test.decode.internal.UIImageDecodeHelperTest
 */
class UIImageDecodeHelper(val dataSource: DataSource, mimeType: String) : DecodeHelper {

    private val uiImage: UIImage by lazy {
        val data = dataSource.toByteArray()
        val uiImage = UIImage.imageWithData(data.toNSData())
            ?.correctExifOrientation()
        requireNotNull(uiImage) { "Failed to decode image" }
    }
    private val _imageInfo by lazy {
        val size = uiImage.sketchSize()
        ImageInfo(size, mimeType = mimeType)
    }

    override suspend fun getImageInfo(): ImageInfo {
        return _imageInfo
    }

    override suspend fun isSupportRegion(): Boolean {
        return true
    }

    override suspend fun decode(sampleSize: Int): Image {
        val bitmap = uiImage.toBitmap(sampleSize)
        return bitmap.asImage()
    }

    override suspend fun decodeRegion(region: Rect, sampleSize: Int): Image {
        val bitmap = uiImage.toBitmap(sampleSize = sampleSize, cropRect = region)
        return bitmap.asImage()
    }

    override fun close() {

    }
}