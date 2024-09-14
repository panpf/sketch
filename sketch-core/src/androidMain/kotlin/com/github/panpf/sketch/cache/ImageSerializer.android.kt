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

package com.github.panpf.sketch.cache

import android.graphics.Bitmap.CompressFormat
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.toBitmapOptions
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import okio.BufferedSink

/**
 * Creating an ImageSerializer for the Android platform
 *
 * @see com.github.panpf.sketch.core.android.test.cache.ImageSerializerAndroidTest.testCreateImageSerializer
 */
actual fun createImageSerializer(): ImageSerializer = AndroidBitmapImageSerializer

/**
 * Android platform image serialization implementation
 *
 * @see com.github.panpf.sketch.core.android.test.cache.ImageSerializerAndroidTest.testAndroidBitmapImageSerializer
 */
object AndroidBitmapImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is BitmapImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        image as BitmapImage
        image.bitmap.compress(CompressFormat.PNG, 100, sink.outputStream())
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val decodeOptions = DecodeConfig(
            request = requestContext.request,
            mimeType = imageInfo.mimeType,
            isOpaque = false
        ).toBitmapOptions()
        val bitmap = dataSource.decodeBitmap(decodeOptions)
            ?: throw DecodeException("Decode bitmap return null. '${requestContext.logKey}'")
        return bitmap.asImage()
    }
}