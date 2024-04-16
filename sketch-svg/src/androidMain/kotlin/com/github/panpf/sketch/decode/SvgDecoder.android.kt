package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.internal.toSoftware
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.isNotEmpty
import okio.buffer
import kotlin.math.min
import kotlin.math.roundToInt

actual suspend fun decodeSvg(
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

    val targetSize = requestContext.size!!
    val targetScale: Float = when {
        targetSize.isNotEmpty -> min(targetSize.width / svgWidth, targetSize.height / svgHeight)
        targetSize.width > 0 -> targetSize.width / svgHeight
        targetSize.height > 0 -> targetSize.height / svgHeight
        else -> 1f
    }
    val bitmapWidth: Int = (svgWidth * targetScale).roundToInt()
    val bitmapHeight: Int = (svgHeight * targetScale).roundToInt()
    val request = requestContext.request
    val bitmapConfig = request.bitmapConfig?.getConfig(ImageFormat.PNG.mimeType).toSoftware()
    val bitmap = Bitmap.createBitmap(
        /* width = */ bitmapWidth,
        /* height = */ bitmapHeight,
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
    val transformedList: List<String>? = if (targetScale != 1f)
        listOf(createScaledTransformed(targetScale)) else null
    val decodeResult = DecodeResult(
        image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
        imageInfo = imageInfo,
        dataFrom = dataSource.dataFrom,
        transformedList = transformedList,
        extras = null
    )

    @Suppress("UnnecessaryVariable", "RedundantSuppression")
    val resizedResult = decodeResult.appliedResize(requestContext)
    return resizedResult
}