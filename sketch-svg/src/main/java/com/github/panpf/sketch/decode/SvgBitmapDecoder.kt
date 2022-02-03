package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.AbsBitmapDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isSvg
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.util.toSoftware
import kotlin.math.roundToInt

class SvgBitmapDecoder(
    sketch: Sketch,
    request: LoadRequest,
    dataSource: DataSource,
    val useViewBoundsAsIntrinsicSize: Boolean = true,
    val backgroundColor: Int?,
) : AbsBitmapDecoder(sketch, request, dataSource) {

    companion object {
        const val MIME_TYPE = "image/svg+xml"
    }

    private val svg by lazy {
        dataSource.newInputStream().use { SVG.getFromInputStream(it) }
    }

    override fun readImageInfo(): ImageInfo {
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
        return ImageInfo(width, height, MIME_TYPE)
    }

    override fun readExifOrientation(imageInfo: ImageInfo): Int =
        ExifInterface.ORIENTATION_UNDEFINED

    override fun decodeFull(imageInfo: ImageInfo, decodeConfig: DecodeConfig): Bitmap {
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

        val bitmap = sketch.bitmapPool
            .getOrCreate(dstWidth, dstHeight, decodeConfig.inPreferredConfig.toSoftware())
        val canvas = Canvas(bitmap).apply {
            backgroundColor?.let {
                drawColor(it)
            }
        }
        svg.renderToCanvas(canvas)
        return bitmap
    }

    override fun canDecodeRegion(mimeType: String): Boolean = false

    override fun decodeRegion(
        imageInfo: ImageInfo, srcRect: Rect, decodeConfig: DecodeConfig
    ): Bitmap = throw UnsupportedOperationException("SVGBitmapDecoder not support decode region")

    override fun close() {

    }

    class Factory(
        val useViewBoundsAsIntrinsicSize: Boolean = true
    ) : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            fetchResult: FetchResult
        ): SvgBitmapDecoder? =
            if (
                MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                || fetchResult.headerBytes.isSvg()
            ) {
                SvgBitmapDecoder(
                    sketch,
                    request,
                    fetchResult.dataSource,
                    useViewBoundsAsIntrinsicSize,
                    request.svgBackgroundColor
                )
            } else {
                null
            }

        override fun toString(): String = "SvgBitmapDecoder"
    }
}