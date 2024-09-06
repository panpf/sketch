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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import okio.BufferedSink
import okio.buffer
import okio.use
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.impl.use

/**
 * Creating an ImageSerializer for the Skia platform
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.ImageSerializerNonAndroidTest.testCreateImageSerializer
 */
actual fun createImageSerializer(): ImageSerializer? = SkiaBitmapImageSerializer()

/**
 * Skia platform image serialization implementation
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.ImageSerializerNonAndroidTest
 */
class SkiaBitmapImageSerializer : ImageSerializer {

    override fun supportImage(image: Image): Boolean {
        return image is SkiaBitmapImage
    }

    override fun compress(image: Image, sink: BufferedSink) {
        require(image is SkiaBitmapImage) { "Unsupported image type: ${image::class}" }
        val encodedData = SkiaImage.makeFromBitmap(image.bitmap).use {
            it.encodeToData(format = EncodedImageFormat.PNG, quality = 100)
        }
        encodedData?.use {
            sink.write(it.bytes)
        }
    }

    override fun decode(
        requestContext: RequestContext,
        imageInfo: ImageInfo,
        dataSource: DataSource
    ): Image {
        val bytes = dataSource.openSource().buffer().use { it.readByteArray() }
        val skiaBitmap = SkiaImage.makeFromEncoded(bytes).use {
            SkiaBitmap.makeFromImage(it)
        }
        return skiaBitmap.asSketchImage()
    }
}