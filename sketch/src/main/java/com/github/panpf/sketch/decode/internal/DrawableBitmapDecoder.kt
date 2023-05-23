/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DrawableDataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.toNewBitmap

/**
 * Extract the icon of the installed app and convert it to Bitmap
 */
open class DrawableBitmapDecoder(
    private val sketch: Sketch,
    private val requestContext: RequestContext,
    private val drawableDataSource: DrawableDataSource,
) : BitmapDecoder {

    companion object {
        const val MODULE = "DrawableBitmapDecoder"
    }

    @WorkerThread
    override suspend fun decode(): Result<BitmapDecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val drawable = drawableDataSource.drawable
        val bitmap = drawable.toNewBitmap(
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = request.disallowReuseBitmap,
            preferredConfig = request.bitmapConfig?.getConfig(ImageFormat.PNG.mimeType)
        )
        val imageInfo = ImageInfo(
            width = bitmap.width,
            height = bitmap.height,
            mimeType = ImageFormat.PNG.mimeType,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED
        )
        sketch.logger.d(MODULE) {
            "decode. successful. ${bitmap.logString}. ${imageInfo}. '${requestContext.key}'"
        }
        BitmapDecodeResult(bitmap, imageInfo, LOCAL, null, null)
            .appliedResize(sketch, requestContext)
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (dataSource is DrawableDataSource) {
                DrawableBitmapDecoder(sketch, requestContext, dataSource)
            } else {
                null
            }
        }

        override fun toString(): String = "DrawableBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}