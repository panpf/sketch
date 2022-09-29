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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build.VERSION
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.getOrCreate
import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import kotlin.math.roundToInt

/**
 * Decode svg file and convert to Bitmap
 */
class SvgBitmapDecoder constructor(
    private val sketch: Sketch,
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
    private val useViewBoundsAsIntrinsicSize: Boolean = true,
    private val backgroundColor: Int?,
    private val css: String?,
) : BitmapDecoder {

    companion object {
        const val MODULE = "SvgBitmapDecoder"
        const val MIME_TYPE = "image/svg+xml"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        // Currently running on a limited number of IO contexts, so this warning can be ignored
        @Suppress("BlockingMethodInNonBlockingContext")
        val svg = dataSource.newInputStream().buffered().use { SVG.getFromInputStream(it) }
        val imageInfo = readImageInfo(svg)
        return realDecode(
            requestContext = requestContext,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            decodeFull = { decodeConfig: DecodeConfig ->
                realDecodeFull(imageInfo, decodeConfig, svg)
            },
            decodeRegion = null
        ).appliedResize(sketch, requestContext, requestContext.resize)
    }

    private fun readImageInfo(svg: SVG): ImageInfo {
        val width: Int
        val height: Int
        val viewBox: RectF? = svg.documentViewBox
        if (useViewBoundsAsIntrinsicSize && viewBox != null) {
            width = viewBox.width().toInt()
            height = viewBox.height().toInt()
        } else {
            width = svg.documentWidth.toInt()
            height = svg.documentHeight.toInt()
        }
        return ImageInfo(width, height, MIME_TYPE, ExifInterface.ORIENTATION_UNDEFINED)
    }

    private fun realDecodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig, svg: SVG): Bitmap {
        val svgWidth: Float
        val svgHeight: Float
        val viewBox: RectF? = svg.documentViewBox
        if (useViewBoundsAsIntrinsicSize && viewBox != null) {
            svgWidth = viewBox.width()
            svgHeight = viewBox.height()
        } else {
            svgWidth = svg.documentWidth
            svgHeight = svg.documentHeight
        }

        val inSampleSize = decodeConfig.inSampleSize?.toFloat()
        val dstWidth = if (inSampleSize != null) {
            (imageInfo.width / inSampleSize).roundToInt()
        } else {
            imageInfo.width
        }
        val dstHeight = if (inSampleSize != null) {
            (imageInfo.height / inSampleSize).roundToInt()
        } else {
            imageInfo.height
        }

        // Set the SVG's view box to enable scaling if it is not set.
        if (viewBox == null && svgWidth > 0 && svgHeight > 0) {
            svg.setDocumentViewBox(0f, 0f, svgWidth, svgHeight)
        }

        svg.setDocumentWidth("100%")
        svg.setDocumentHeight("100%")

        val bitmap = sketch.bitmapPool.getOrCreate(
            width = dstWidth,
            height = dstHeight,
            config = decodeConfig.inPreferredConfig.toSoftware(),
            disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
            caller = "SvgBitmapDecoder"
        )
        val canvas = Canvas(bitmap).apply {
            backgroundColor?.let {
                drawColor(it)
            }
        }
        val renderOptions = css?.let { RenderOptions().css(it) }
        svg.renderToCanvas(canvas, renderOptions)
        sketch.logger.d(MODULE) {
            "realDecodeFull. successful. ${bitmap.logString}. ${imageInfo}. '${requestContext.key}'"
        }
        return bitmap
    }

    /**
     * Convert null and [Bitmap.Config.HARDWARE] configs to [Bitmap.Config.ARGB_8888].
     */
    private fun Bitmap.Config?.toSoftware(): Bitmap.Config {
        return if (this == null || VERSION.SDK_INT >= 26 && this == HARDWARE) Bitmap.Config.ARGB_8888 else this
    }

    class Factory(val useViewBoundsAsIntrinsicSize: Boolean = true) : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): SvgBitmapDecoder? =
            if (
                MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                || fetchResult.headerBytes.isSvg()
            ) {
                SvgBitmapDecoder(
                    sketch = sketch,
                    requestContext = requestContext,
                    dataSource = fetchResult.dataSource,
                    useViewBoundsAsIntrinsicSize = useViewBoundsAsIntrinsicSize,
                    backgroundColor = requestContext.request.svgBackgroundColor,
                    css = requestContext.request.svgCss
                )
            } else {
                null
            }

        override fun toString(): String = "SvgBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Factory) return false
            if (useViewBoundsAsIntrinsicSize != other.useViewBoundsAsIntrinsicSize) return false
            return true
        }

        override fun hashCode(): Int {
            return useViewBoundsAsIntrinsicSize.hashCode()
        }
    }
}