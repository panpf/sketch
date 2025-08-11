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

import android.graphics.Canvas
import android.graphics.ColorSpace
import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.isNotEmpty
import com.github.panpf.sketch.util.safeToSoftware
import com.github.panpf.sketch.util.times
import okio.buffer
import kotlin.math.roundToInt

/**
 * Decode the SVG image info
 *
 * @see com.github.panpf.sketch.svg.android.test.decode.internal.SvgsAndroidTest.testReadSvgImageInfo
 */
internal actual fun DataSource.readSvgImageInfo(
    useViewBoundsAsIntrinsicSize: Boolean,
): ImageInfo {
    val svg = openSource().buffer().inputStream().use { SVG.getFromInputStream(it) }
    val viewBox: RectF? = svg.documentViewBox
    val imageSize: Size = if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        Size(viewBox.width().roundToInt(), viewBox.height().roundToInt())
    } else {
        Size(svg.documentWidth.roundToInt(), svg.documentHeight.roundToInt())
    }
    return ImageInfo(size = imageSize, mimeType = SvgDecoder.MIME_TYPE)
        .apply { checkImageInfo(this) }
}

/**
 * Decode the SVG image
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorType
 * * colorSpace
 * * svgBackgroundColor
 * * svgCss
 *
 * @see com.github.panpf.sketch.svg.android.test.decode.internal.SvgsAndroidTest.testDecodeSvg
 */
internal actual fun DataSource.decodeSvg(
    requestContext: RequestContext,
    useViewBoundsAsIntrinsicSize: Boolean,
): DecodeResult {
    val svg = openSource().buffer().inputStream().use { SVG.getFromInputStream(it) }

    val viewBox: RectF? = svg.documentViewBox
    val imageSize: Size = if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        Size(viewBox.width().roundToInt(), viewBox.height().roundToInt())
    } else {
        Size(svg.documentWidth.roundToInt(), svg.documentHeight.roundToInt())
    }
    checkImageSize(imageSize)

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && imageSize.isNotEmpty) {
        svg.setDocumentViewBox(0f, 0f, imageSize.width.toFloat(), imageSize.height.toFloat())
    }
    svg.setDocumentWidth("100%")
    svg.setDocumentHeight("100%")

    val imageInfo = ImageInfo(size = imageSize, mimeType = SvgDecoder.MIME_TYPE)
    val resize = requestContext.computeResize(imageInfo.size)
    val targetScale = calculateScaleMultiplierWithOneSide(
        sourceSize = imageSize,
        targetSize = resize.size
    )
    val bitmapSize = imageSize.times(targetScale)
    val decodeConfig = DecodeConfig(requestContext.request, PNG.mimeType, isOpaque = false)
    val bitmapConfig = decodeConfig.colorType.safeToSoftware()
    val bitmap = if (VERSION.SDK_INT >= VERSION_CODES.O) {
        createBitmap(
            width = bitmapSize.width,
            height = bitmapSize.height,
            config = bitmapConfig,
            hasAlpha = true,
            colorSpace = decodeConfig.colorSpace ?: ColorSpace.get(ColorSpace.Named.SRGB),
        )
    } else {
        createBitmap(
            width = bitmapSize.width,
            height = bitmapSize.height,
            config = bitmapConfig,
        )
    }
    val canvas = Canvas(bitmap)
    val backgroundColor = requestContext.request.svgBackgroundColor
    if (backgroundColor != null) {
        canvas.drawColor(backgroundColor)
    }
    val renderOptions = RenderOptions()
    val css = requestContext.request.svgCss
    if (css != null) {
        renderOptions.css(css)
    }
    svg.renderToCanvas(canvas, renderOptions)

    val transformeds: List<String>? = if (targetScale != 1f)
        listOf(createScaledTransformed(targetScale)) else null
    val decodeResult = DecodeResult(
        image = bitmap.asImage(),
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        resize = resize,
        transformeds = transformeds,
        extras = null
    )

    @Suppress("UnnecessaryVariable", "RedundantSuppression")
    val resizeResult = decodeResult.resize(resize)
    return resizeResult
}