package com.github.panpf.sketch.decode

import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.SvgDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.isNotEmpty
import okio.buffer
import okio.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Data
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.svg.SVGDOM
import org.jetbrains.skia.svg.SVGLength
import org.jetbrains.skia.svg.SVGLengthUnit
import kotlin.math.min
import kotlin.math.roundToInt


actual suspend fun decodeSvg(
    requestContext: RequestContext,
    dataSource: DataSource,
    useViewBoundsAsIntrinsicSize: Boolean,
    backgroundColor: Int?,
    css: String?,
): DecodeResult {
    val bytes = dataSource.openSource().buffer().use { it.readByteArray() }
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

    val targetSize = requestContext.size!!
    val targetScale: Float = when {
        targetSize.isNotEmpty -> min(targetSize.width / svgWidth, targetSize.height / svgHeight)
        targetSize.width > 0 -> targetSize.width / svgHeight
        targetSize.height > 0 -> targetSize.height / svgHeight
        else -> 1f
    }
    val bitmapWidth: Int = (svgWidth * targetScale).roundToInt()
    val bitmapHeight: Int = (svgHeight * targetScale).roundToInt()
    svg.setContainerSize(bitmapWidth.toFloat(), bitmapHeight.toFloat())

    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapWidth, bitmapHeight)
    }
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
    val transformedList: List<String>? = if (targetScale != 1f)
        listOf(createScaledTransformed(targetScale)) else null
    val decodeResult = DecodeResult(
        image = bitmap.asSketchImage(),
        imageInfo = imageInfo,
        dataFrom = dataSource.dataFrom,
        transformedList = transformedList,
        extras = null
    )

    @Suppress("UnnecessaryVariable", "RedundantSuppression")
    val resizedResult = decodeResult.appliedResize(requestContext)
    return resizedResult
}