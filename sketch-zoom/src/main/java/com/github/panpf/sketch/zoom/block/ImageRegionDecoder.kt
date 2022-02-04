/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom.block

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrNull
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import java.io.IOException

/**
 * 图片碎片解码器，支持纠正图片方向
 */
class ImageRegionDecoder constructor(
    val imageUri: String,
    val imageSize: Size,
    val imageFormat: ImageFormat?,
    val exifOrientationHelper: ExifOrientationHelper,
    regionDecoder: BitmapRegionDecoder
) {

    private var regionDecoder: BitmapRegionDecoder? = regionDecoder
    val isReady: Boolean
        get() = regionDecoder?.isRecycled == false

    fun recycle() {
        if (isReady) {
            regionDecoder?.recycle()
            regionDecoder = null
        }
    }

    fun decodeRegion(srcRect: Rect?, options: BitmapFactory.Options?): Bitmap? {
        return if (isReady) regionDecoder?.decodeRegion(srcRect, options) else null
    }

    companion object {

        @Throws(IOException::class)
        fun build(
            imageUri: String,
            exifOrientation: Int,
            sketch: Sketch
        ): ImageRegionDecoder {
            val request = LoadRequest(imageUri) // todo 请求要从 view 中去
            val fetch = sketch.componentRegistry.newFetcher(sketch, request)
            val fetchResult = runBlocking {
                fetch.fetch()
            }
            val imageInfo = fetchResult.dataSource.readImageInfoWithBitmapFactoryOrNull()
                ?: throw Exception("Unsupported image format.  $imageUri")

            val exifOrientationHelper = ExifOrientationHelper(exifOrientation)
            val imageSize =
                exifOrientationHelper.applyToSize(Size(imageInfo.width, imageInfo.height))
            val regionDecoder: BitmapRegionDecoder = fetchResult.dataSource.newInputStream().use {
                if (VERSION.SDK_INT >= VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(it)
                } else {
                    @Suppress("DEPRECATION")
                    BitmapRegionDecoder.newInstance(it, false)
                }
            } ?: throw Exception("Unsupported image format.  $imageUri")
            val imageType = ImageFormat.valueOfMimeType(imageInfo.mimeType)
            return ImageRegionDecoder(
                imageUri,
                imageSize,
                imageType,
                exifOrientationHelper,
                regionDecoder
            )
        }
    }
}