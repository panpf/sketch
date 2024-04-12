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
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.internal.getOrCreate
import com.github.panpf.sketch.decode.internal.isSmallerSizeMode
import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Adds SVG support
 */
fun ComponentRegistry.Builder.supportSvg(): ComponentRegistry.Builder = apply {
    addBitmapDecoder(SvgBitmapDecoder.Factory())
}

/**
 * Decode svg file and convert to Bitmap
 */
class SvgBitmapDecoder constructor(
    private val sketch: Sketch,
    private val requestContext: RequestContext,
    private val dataSource: BasedStreamDataSource,
    private val useViewBoundsAsIntrinsicSize: Boolean = true,
    private val backgroundColor: Int?,
    private val css: String?,
) : BitmapDecoder {

    companion object {
        const val MODULE = "SvgBitmapDecoder"
        const val MIME_TYPE = "image/svg+xml"
    }

    @WorkerThread
    override suspend fun decode(): Result<BitmapDecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val svg = dataSource.newInputStream().buffered().use { SVG.getFromInputStream(it) }

        val imageWidth: Float
        val imageHeight: Float
        val viewBox: RectF? = svg.documentViewBox
        if (useViewBoundsAsIntrinsicSize && viewBox != null) {
            imageWidth = viewBox.width()
            imageHeight = viewBox.height()
        } else {
            imageWidth = svg.documentWidth
            imageHeight = svg.documentHeight
        }
        if (imageWidth <= 0f || imageHeight <= 0f) {
            throw ImageInvalidException(
                "Invalid svg image, width or height is less than or equal to 0"
            )
        }
        val imageInfo = ImageInfo(
            width = imageWidth.roundToInt(),
            height = imageHeight.roundToInt(),
            mimeType = MIME_TYPE,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED
        )

        val targetSize = requestContext.resizeSize
        val dstWidth: Int
        val dstHeight: Int
        var transformedList: List<String>? = null
        if (request.resizeSizeResolver is DisplaySizeResolver) {
            val imageSize = Size(imageInfo.width, imageInfo.height)
            val precision = request.resizePrecisionDecider.get(
                imageWidth = imageSize.width,
                imageHeight = imageSize.height,
                resizeWidth = targetSize.width,
                resizeHeight = targetSize.height
            )
            val inSampleSize = calculateSampleSize(
                imageSize = imageSize,
                targetSize = targetSize,
                smallerSizeMode = precision.isSmallerSizeMode(),
                mimeType = null
            )
            dstWidth = (imageWidth / inSampleSize).roundToInt()
            dstHeight = (imageHeight / inSampleSize).roundToInt()
            if (inSampleSize > 1) {
                transformedList = listOf(createInSampledTransformed(inSampleSize))
            }
        } else {
            val scale: Float =  when {
                targetSize.isNotEmpty -> min(
                    targetSize.width / imageWidth,
                    targetSize.height / imageHeight
                )
                targetSize.width > 0 -> targetSize.width / imageWidth
                targetSize.height > 0 -> targetSize.height / imageHeight
                else -> 1f
            }
            dstWidth = (imageWidth * scale).roundToInt()
            dstHeight = (imageHeight * scale).roundToInt()
            if (scale != 1f) {
                transformedList = listOf(createScaledTransformed(scale))
            }
        }

        // Set the SVG's view box to enable scaling if it is not set.
        if (viewBox == null && imageWidth > 0f && imageHeight > 0f) {
            svg.setDocumentViewBox(0f, 0f, imageWidth, imageHeight)
        }
        svg.setDocumentWidth("100%")
        svg.setDocumentHeight("100%")

        val bitmap = sketch.bitmapPool.getOrCreate(
            width = dstWidth,
            height = dstHeight,
            config = request.bitmapConfig?.getConfig(ImageFormat.PNG.mimeType).toSoftware(),
            disallowReuseBitmap = requestContext.request.disallowReuseBitmap,
            caller = "SvgBitmapDecoder"
        )
        val canvas = Canvas(bitmap)
        backgroundColor?.let { canvas.drawColor(it) }
        val renderOptions = css?.let { RenderOptions().css(it) }
        svg.renderToCanvas(canvas, renderOptions)
        sketch.logger.d(MODULE) {
            "decode. successful. ${bitmap.logString}. ${imageInfo}. '${requestContext.key}'"
        }

        BitmapDecodeResult(
            bitmap = bitmap,
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList,
            extras = null
        ).appliedResize(sketch, requestContext)
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
        ): SvgBitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (
                (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                        || fetchResult.headerBytes.isSvg())
                && dataSource is BasedStreamDataSource
            ) {
                SvgBitmapDecoder(
                    sketch = sketch,
                    requestContext = requestContext,
                    dataSource = dataSource,
                    useViewBoundsAsIntrinsicSize = useViewBoundsAsIntrinsicSize,
                    backgroundColor = requestContext.request.svgBackgroundColor,
                    css = requestContext.request.svgCss
                )
            } else {
                null
            }
        }

        override fun toString(): String = "SvgBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Factory
            if (useViewBoundsAsIntrinsicSize != other.useViewBoundsAsIntrinsicSize) return false
            return true
        }

        override fun hashCode(): Int {
            return useViewBoundsAsIntrinsicSize.hashCode()
        }
    }
}