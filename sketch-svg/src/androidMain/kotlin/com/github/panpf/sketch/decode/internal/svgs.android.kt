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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.times
import okio.buffer
import kotlin.math.roundToInt

/**
 * Decode the SVG image
 *
 * @see com.github.panpf.sketch.svg.android.test.decode.internal.SvgsAndroidTest.testDecodeSvg
 */
internal actual suspend fun decodeSvg(
    requestContext: RequestContext,
    dataSource: DataSource,
    useViewBoundsAsIntrinsicSize: Boolean,
    backgroundColor: Int?,
    css: String?,
): DecodeResult {
    val svg = dataSource.openSource().buffer().inputStream().use { SVG.getFromInputStream(it) }

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
    if (svgWidth <= 0f || svgHeight <= 0f) {
        throw ImageInvalidException("Invalid svg image size, size=${svgWidth}x${svgHeight}")
    }

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && svgWidth > 0f && svgHeight > 0f) {
        svg.setDocumentViewBox(0f, 0f, svgWidth, svgHeight)
    }
    svg.setDocumentWidth("100%")
    svg.setDocumentHeight("100%")

    val svgSize = SketchSize(width = svgWidth.roundToInt(), height = svgHeight.roundToInt())
    val targetSize = requestContext.size!!
    val targetScale =
        computeScaleMultiplierWithOneSide(sourceSize = svgSize, targetSize = targetSize)
    val bitmapSize = svgSize.times(targetScale)
    val request = requestContext.request
    val bitmapConfig = request.bitmapConfig?.getConfig(ImageFormat.PNG.mimeType).toSoftware()
    val bitmap = Bitmap.createBitmap(
        /* width = */ bitmapSize.width,
        /* height = */ bitmapSize.height,
        /* config = */ bitmapConfig,
    )
    val canvas = Canvas(bitmap)
    backgroundColor?.let { canvas.drawColor(it) }
    val renderOptions = css?.let { RenderOptions().css(it) }
    svg.renderToCanvas(canvas, renderOptions)

    val imageInfo = ImageInfo(
        width = svgWidth.roundToInt(),
        height = svgHeight.roundToInt(),
        mimeType = SvgDecoder.MIME_TYPE,
    )
    val transformeds: List<String>? = if (targetScale != 1f)
        listOf(createScaledTransformed(targetScale)) else null
    val resize = requestContext.computeResize(imageInfo.size)
    val decodeResult = DecodeResult(
        image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
        imageInfo = imageInfo,
        dataFrom = dataSource.dataFrom,
        resize = resize,
        transformeds = transformeds,
        extras = null
    )

    @Suppress("UnnecessaryVariable", "RedundantSuppression")
    val resizedResult = decodeResult.appliedResize(requestContext)
    return resizedResult
}

/**
 * Convert null and [Bitmap.Config.HARDWARE] configs to [Bitmap.Config.ARGB_8888].
 */
private fun Bitmap.Config?.toSoftware(): Bitmap.Config {
    return if (this == null || Build.VERSION.SDK_INT >= 26 && this == Bitmap.Config.HARDWARE)
        Bitmap.Config.ARGB_8888 else this
}