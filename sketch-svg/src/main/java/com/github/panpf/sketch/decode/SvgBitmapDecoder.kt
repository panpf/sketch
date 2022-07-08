package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build.VERSION
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.decode.internal.realDecode
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isSvg
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import kotlin.math.roundToInt

/**
 * Decode svg file and convert to Bitmap
 */
class SvgBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
    private val useViewBoundsAsIntrinsicSize: Boolean = true,
    private val backgroundColor: Int?,
) : BitmapDecoder {

    companion object {
        const val MIME_TYPE = "image/svg+xml"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val svg = dataSource.newInputStream().buffered().use { SVG.getFromInputStream(it) }
        val imageInfo = readImageInfo(svg)
        return realDecode(
            sketch = sketch,
            request = request,
            dataFrom = dataSource.dataFrom,
            imageInfo = imageInfo,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            decodeFull = { decodeConfig: DecodeConfig ->
                realDecodeFull(imageInfo, decodeConfig, svg)
            },
            decodeRegion = null
        ).applyResize(sketch, request.resize)
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
        return ImageInfo(width, height, MIME_TYPE)
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

    /**
     * Convert null and [Bitmap.Config.HARDWARE] configs to [Bitmap.Config.ARGB_8888].
     */
    private fun Bitmap.Config?.toSoftware(): Bitmap.Config {
        return if (this == null || VERSION.SDK_INT >= 26 && this == HARDWARE) Bitmap.Config.ARGB_8888 else this
    }

    class Factory(val useViewBoundsAsIntrinsicSize: Boolean = true) : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): SvgBitmapDecoder? =
            if (
                MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                || fetchResult.headerBytes.isSvg()
            ) {
                SvgBitmapDecoder(
                    sketch = sketch,
                    request = request,
                    dataSource = fetchResult.dataSource,
                    useViewBoundsAsIntrinsicSize = useViewBoundsAsIntrinsicSize,
                    backgroundColor = request.svgBackgroundColor
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