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

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImageInfo
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.SvgDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.times
import okio.buffer
import okio.use
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.svg.SVGDOM
import org.jetbrains.skia.svg.SVGLength
import org.jetbrains.skia.svg.SVGLengthUnit
import kotlin.math.roundToInt

/**
 * Decode the SVG image info
 *
 * @see com.github.panpf.sketch.svg.nonandroid.test.decode.internal.SvgsNonAndroidTest.testReadSvgImageInfo
 */
internal actual fun DataSource.readSvgImageInfo(
    useViewBoundsAsIntrinsicSize: Boolean,
): ImageInfo {
    val bytes = openSource().buffer().use { it.readByteArray() }
    val svg = SVGDOM(Data.makeFromBytes(bytes))

    val svgWidth: Float
    val svgHeight: Float
    val viewBox: Rect? = svg.root?.viewBox
    if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        svgWidth = viewBox.width
        svgHeight = viewBox.height
    } else {
        svgWidth = svg.root?.width?.value ?: 0f
        svgHeight = svg.root?.height?.value ?: 0f
    }
    return ImageInfo(
        width = svgWidth.roundToInt(),
        height = svgHeight.roundToInt(),
        mimeType = MIME_TYPE,
    )
}

/**
 * Decode the SVG image
 *
 * @see com.github.panpf.sketch.svg.nonandroid.test.decode.internal.SvgsNonAndroidTest.testDecodeSvg
 */
internal actual fun DataSource.decodeSvg(
    requestContext: RequestContext,
    useViewBoundsAsIntrinsicSize: Boolean,
    backgroundColor: Int?,
    css: String?,
): DecodeResult {
    val bytes = openSource().buffer().use { it.readByteArray() }
    val svg = SVGDOM(Data.makeFromBytes(bytes))

    val svgWidth: Float
    val svgHeight: Float
    val viewBox: Rect? = svg.root?.viewBox
    if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        svgWidth = viewBox.width
        svgHeight = viewBox.height
    } else {
        svgWidth = svg.root?.width?.value ?: 0f
        svgHeight = svg.root?.height?.value ?: 0f
    }
    if (svgWidth <= 0f || svgHeight <= 0f) {
        throw ImageInvalidException("Invalid svg image size, size=${svgWidth}x${svgHeight}")
    }

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && svgWidth > 0f && svgHeight > 0f) {
        svg.root?.viewBox = Rect.makeWH(svgWidth, svgHeight)
    }
    svg.root?.width = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )
    svg.root?.height = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )

    val svgSize = SketchSize(width = svgWidth.roundToInt(), height = svgHeight.roundToInt())
    val targetSize = requestContext.size
    val targetScale =
        computeScaleMultiplierWithOneSide(sourceSize = svgSize, targetSize = targetSize)
    val bitmapSize = svgSize.times(targetScale)
    svg.setContainerSize(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())

    val decodeConfig = DecodeConfig(
        request = requestContext.request,
        mimeType = PNG.mimeType,
        isOpaque = false
    )
    val newColorType = decodeConfig.colorType ?: ColorType.RGBA_8888
    val newColorSpace = decodeConfig.colorSpace
    val bitmap = SkiaBitmap(
        SkiaImageInfo(
            width = bitmapSize.width,
            height = bitmapSize.height,
            colorType = newColorType,
            alphaType = ColorAlphaType.PREMUL,
            colorSpace = newColorSpace
        )
    )
    val canvas = Canvas(bitmap)
    if (backgroundColor != null) {
        val rect = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val paint = Paint().apply { color = backgroundColor }
        canvas.drawRect(rect, paint)
    }
    // TODO SVGDOM not support css. https://github.com/JetBrains/compose-multiplatform/issues/1217
    svg.render(canvas)

    val imageInfo = ImageInfo(
        width = svgWidth.roundToInt(),
        height = svgHeight.roundToInt(),
        mimeType = MIME_TYPE,
    )
    val transformeds: List<String>? = if (targetScale != 1f)
        listOf(createScaledTransformed(targetScale)) else null
    val resize = requestContext.computeResize(imageInfo.size)
    val decodeResult = DecodeResult(
        image = bitmap.asSketchImage(),
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        resize = resize,
        transformeds = transformeds,
        extras = null
    )

    @Suppress("UnnecessaryVariable", "RedundantSuppression")
    val resizedResult = decodeResult.appliedResize(resize)
    return resizedResult
}