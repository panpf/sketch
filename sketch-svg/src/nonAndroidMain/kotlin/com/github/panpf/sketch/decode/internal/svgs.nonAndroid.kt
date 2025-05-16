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

import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.isNotEmpty
import com.github.panpf.sketch.util.times
import okio.buffer
import okio.use
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
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

    val viewBox: Rect? = svg.root?.viewBox
    val imageSize: Size = if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        Size(viewBox.width.roundToInt(), viewBox.height.roundToInt())
    } else {
        svg.root
            ?.let { Size(it.width.value.roundToInt(), it.height.value.roundToInt()) }
            ?: Size.Empty
    }
    return ImageInfo(size = imageSize, mimeType = MIME_TYPE)
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
 *
 * @see com.github.panpf.sketch.svg.nonandroid.test.decode.internal.SvgsNonAndroidTest.testDecodeSvg
 */
internal actual fun DataSource.decodeSvg(
    requestContext: RequestContext,
    useViewBoundsAsIntrinsicSize: Boolean,
): DecodeResult {
    val bytes = openSource().buffer().use { it.readByteArray() }
    val svg = SVGDOM(Data.makeFromBytes(bytes))

    val viewBox: Rect? = svg.root?.viewBox
    val imageSize: Size = if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        Size(viewBox.width.roundToInt(), viewBox.height.roundToInt())
    } else {
        svg.root
            ?.let { Size(it.width.value.roundToInt(), it.height.value.roundToInt()) }
            ?: Size.Empty
    }
    checkImageSize(imageSize)

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && imageSize.isNotEmpty) {
        svg.root?.viewBox = Rect.makeWH(imageSize.width.toFloat(), imageSize.height.toFloat())
    }
    svg.root?.width = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )
    svg.root?.height = SVGLength(
        value = 100f,
        unit = SVGLengthUnit.PERCENTAGE,
    )

    val imageInfo = ImageInfo(size = imageSize, mimeType = MIME_TYPE)
    val resize = requestContext.computeResize(imageInfo.size)
    val targetScale = calculateScaleMultiplierWithOneSide(
        sourceSize = imageSize,
        targetSize = resize.size
    )
    val bitmapSize = imageSize.times(targetScale)
    svg.setContainerSize(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())

    val request = requestContext.request
    val decodeConfig = DecodeConfig(
        request = request,
        mimeType = PNG.mimeType,
        isOpaque = false
    )
    val newColorType = decodeConfig.colorType ?: ColorType.RGBA_8888
    val newColorSpace = decodeConfig.colorSpace ?: ColorSpace.sRGB
    val bitmap = createBitmap(
        org.jetbrains.skia.ImageInfo(
            width = bitmapSize.width,
            height = bitmapSize.height,
            colorType = newColorType,
            alphaType = ColorAlphaType.PREMUL,
            colorSpace = newColorSpace
        )
    )
    val canvas = Canvas(bitmap)
    val backgroundColor = requestContext.request.svgBackgroundColor
    if (backgroundColor != null) {
        val rect = Rect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val paint = Paint().apply { color = backgroundColor }
        canvas.drawRect(rect, paint)
    }
    // TODO SVGDOM not support css. https://github.com/JetBrains/compose-multiplatform/issues/1217
    svg.render(canvas)

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