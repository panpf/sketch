package com.github.panpf.sketch.decode.internal

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.drawableToBitmap

class XmlDrawableBitmapDecoder(
    sketch: Sketch,
    request: LoadRequest,
    val resources: Resources,
    val drawableResId: Int
) : AbsBitmapDecoder(sketch, request) {

    override suspend fun executeDecode(): BitmapDecodeResult {
        // Be sure to use this.resources
        val drawable = ResourcesCompat.getDrawable(this.resources, drawableResId, null)
            ?: throw BitmapDecodeException(request, "Invalid drawable resource id '$drawableResId'")
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            throw BitmapDecodeException(
                request,
                "Invalid drawable resource, intrinsicWidth or intrinsicHeight is less than or equal to 0"
            )
        }
        val bitmap = drawableToBitmap(drawable, false, sketch.bitmapPool)
        val imageInfo = ImageInfo(
            bitmap.width,
            bitmap.height,
            "image/android-xml",
        )
        return BitmapDecodeResult(bitmap, imageInfo, ExifInterface.ORIENTATION_UNDEFINED, LOCAL)
    }

    override fun close() {

    }

    class Factory : BitmapDecoder.Factory {
        override fun create(
            sketch: Sketch,
            request: LoadRequest,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (fetchResult.mimeType == "text/xml" && dataSource is ResourceDataSource) {
                XmlDrawableBitmapDecoder(
                    // Be sure to use dataSource.resources
                    sketch, request, dataSource.resources, dataSource.drawableId
                )
            } else {
                null
            }
        }

        override fun toString(): String = "XmlDrawableBitmapDecoder"
    }
}