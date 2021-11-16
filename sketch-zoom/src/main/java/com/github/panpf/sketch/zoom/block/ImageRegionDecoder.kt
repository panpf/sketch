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

import android.content.Context
import android.graphics.*
import com.github.panpf.sketch.Sketch.Companion.with
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageDecodeUtils.Companion.decodeBitmap
import com.github.panpf.sketch.decode.ImageType
import com.github.panpf.sketch.decode.ImageType.Companion.valueOfMimeType
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.uri.UriModel.Companion.match
import com.github.panpf.sketch.util.ExifInterface
import com.github.panpf.sketch.util.SketchUtils.Companion.close
import java.io.IOException
import java.io.InputStream

/**
 * 图片碎片解码器，支持纠正图片方向
 */
class ImageRegionDecoder(
    val imageUri: String, 
    val imageSize: Point, 
    val imageType: ImageType?,
    val exifOrientation: Int, 
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
            context: Context, imageUri: String,
            correctImageOrientationDisabled: Boolean
        ): ImageRegionDecoder {
            val uriModel = match(context, imageUri)
                ?: throw IllegalArgumentException("Unknown scheme uri. $imageUri")
            val dataSource: DataSource = try {
                uriModel.getDataSource(context, imageUri, null)
            } catch (e: GetDataSourceException) {
                throw IllegalArgumentException("Can not be generated DataSource.  $imageUri", e)
            }

            // 读取图片尺寸和类型
            val boundOptions = BitmapFactory.Options()
            boundOptions.inJustDecodeBounds = true
            decodeBitmap(dataSource, boundOptions)
            val imageSize = Point(boundOptions.outWidth, boundOptions.outHeight)

            // 读取图片方向并根据方向改变尺寸
            val configuration = with(context).configuration
            val orientationCorrector = configuration.orientationCorrector
            var exifOrientation = ExifInterface.ORIENTATION_UNDEFINED
            if (!correctImageOrientationDisabled) {
                exifOrientation =
                    orientationCorrector.readExifOrientation(boundOptions.outMimeType, dataSource)
            }
            orientationCorrector.rotateSize(imageSize, exifOrientation)
            var inputStream: InputStream? = null
            val regionDecoder: BitmapRegionDecoder
            try {
                inputStream = dataSource.newInputStream()
                regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false)
            } finally {
                close(inputStream)
            }
            val imageType = valueOfMimeType(boundOptions.outMimeType)
            return ImageRegionDecoder(
                imageUri,
                imageSize,
                imageType,
                exifOrientation,
                regionDecoder
            )
        }
    }
}