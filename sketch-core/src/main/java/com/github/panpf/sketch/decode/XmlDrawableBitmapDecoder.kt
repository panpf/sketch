package com.github.panpf.sketch.decode

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DrawableResDataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeException
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.drawableToBitmap

class XmlDrawableBitmapDecoder(
    val sketch: Sketch,
    val request: LoadRequest,
    val resources: Resources,
    val drawableResId: Int
) : BitmapDecoder {
    override suspend fun decodeBitmap(): BitmapDecodeResult {
        // Be sure to use this.resources
        val drawable = ResourcesCompat.getDrawable(this.resources, drawableResId, null)
            ?: throw BitmapDecodeException(request, "Invalid drawable resource id '$drawableResId'")
        val bitmap = drawableToBitmap(drawable, false, sketch.bitmapPoolHelper)
        val imageInfo = ImageInfo(
            "image/android-xml",
            bitmap.width,
            bitmap.height,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return BitmapDecodeResult(bitmap, imageInfo, DataFrom.LOCAL, true)
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
            return if (fetchResult.mimeType == "text/xml" && dataSource is DrawableResDataSource) {
                XmlDrawableBitmapDecoder(
                    // Be sure to use dataSource.resources
                    sketch, request, dataSource.resources, dataSource.drawableId
                )
            } else {
                null
            }
        }
    }
}