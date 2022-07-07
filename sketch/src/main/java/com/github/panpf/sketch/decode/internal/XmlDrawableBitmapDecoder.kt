package com.github.panpf.sketch.decode.internal

import android.content.res.Resources
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getXmlDrawableCompat
import com.github.panpf.sketch.util.toNewBitmap

/**
 * Decode Android xml drawable and convert to Bitmap
 */
class XmlDrawableBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val packageName: String,
    private val resources: Resources,
    private val drawableResId: Int
) : BitmapDecoder {

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val context = request.context
        val drawable = if (packageName == context.packageName) {
            // getDrawableCompat can only load vector resources that are in the current package.
            context.getDrawableCompat(drawableResId)
        } else {
            // getXmlDrawableCompat can load vector resources that are in the other package.
            context.getXmlDrawableCompat(resources, drawableResId)
        }
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            throw BitmapDecodeException(
                "Invalid drawable resource, intrinsicWidth or intrinsicHeight is less than or equal to 0"
            )
        }
        val bitmap = drawable.toNewBitmap(sketch.bitmapPool)
        val imageInfo = ImageInfo(
            bitmap.width,
            bitmap.height,
            "image/android-xml",
        )
        return BitmapDecodeResult(bitmap, imageInfo, ExifInterface.ORIENTATION_UNDEFINED, LOCAL)
            .applyResize(sketch, request.resize)
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (fetchResult.mimeType == "text/xml" && dataSource is ResourceDataSource) {
                // Be sure to use dataSource.resources
                XmlDrawableBitmapDecoder(
                    sketch = sketch,
                    request = request,
                    packageName = dataSource.packageName,
                    resources = dataSource.resources,
                    drawableResId = dataSource.drawableId
                )
            } else {
                null
            }
        }

        override fun toString(): String = "XmlDrawableBitmapDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}