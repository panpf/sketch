package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.appliedResize
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.internal.isSmallerSizeMode
import com.github.panpf.sketch.decode.internal.toLogString
import com.github.panpf.sketch.decode.internal.toSoftware
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.util.Size
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
        throw ImageInvalidException("Invalid svg image, width or height is less than or equal to 0")
    }
    val imageInfo = ImageInfo(
        width = svgWidth.roundToInt(),
        height = svgHeight.roundToInt(),
        mimeType = SvgDecoder.MIME_TYPE,
        exifOrientation = ExifOrientation.UNDEFINED
    )

    val bitmapWidth: Int
    val bitmapHeight: Int
    val size = requestContext.size!!
    val request = requestContext.request
    var transformedList: List<String>? = null
    if (request.sizeResolver is DisplaySizeResolver) {
        val imageSize = Size(imageInfo.width, imageInfo.height)
        val precision = request.precisionDecider.get(
            imageSize = imageSize,
            targetSize = size,
        )
        val inSampleSize = calculateSampleSize(
            imageSize = imageSize,
            targetSize = size,
            smallerSizeMode = precision.isSmallerSizeMode(),
            mimeType = null
        )
        bitmapWidth = (svgWidth / inSampleSize).roundToInt()
        bitmapHeight = (svgHeight / inSampleSize).roundToInt()
        if (inSampleSize > 1) {
            transformedList = listOf(createInSampledTransformed(inSampleSize))
        }
    } else {
        val scale: Float = if (size.isNotEmpty) {
            min(size.width / svgWidth, size.height / svgHeight)
        } else {
            1f
        }
        bitmapWidth = (svgWidth * scale).roundToInt()
        bitmapHeight = (svgHeight * scale).roundToInt()
        if (scale != 1f) {
            transformedList = listOf(createScaledTransformed(scale))
        }
    }

    // Set the SVG's view box to enable scaling if it is not set.
    if (viewBox == null && svgWidth > 0f && svgHeight > 0f) {
        svg.setDocumentViewBox(0f, 0f, svgWidth, svgHeight)
    }
    svg.setDocumentWidth("100%")
    svg.setDocumentHeight("100%")

    val bitmap = Bitmap.createBitmap(
        /* width = */ bitmapWidth,
        /* height = */ bitmapHeight,
        /* config = */ request.bitmapConfig?.getConfig(ImageFormat.PNG.mimeType).toSoftware(),
    )
    val canvas = Canvas(bitmap)
    backgroundColor?.let { canvas.drawColor(it) }
    val renderOptions = css?.let { RenderOptions().css(it) }
    svg.renderToCanvas(canvas, renderOptions)
    requestContext.sketch.logger.d(SvgDecoder.MODULE) {
        "decode. successful. ${bitmap.toLogString()}. ${imageInfo}. '${requestContext.logKey}'"
    }

    return DecodeResult(
        image = bitmap.asSketchImage(resources = requestContext.request.context.resources),
        imageInfo = imageInfo,
        dataFrom = dataSource.dataFrom,
        transformedList = transformedList,
        extras = null
    ).appliedResize(requestContext)
}