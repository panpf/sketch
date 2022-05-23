package com.github.panpf.sketch.decode

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.BaseAnimatedImageDrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isAnimatedHeif
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Only the following attributes are supported:
 *
 * resize.size
 *
 * resize.precision: It is always LESS_PIXELS
 *
 * colorSpace
 *
 * repeatCount
 *
 * animatedTransformation
 *
 * onAnimationStart
 *
 * onAnimationEnd
 */
@RequiresApi(Build.VERSION_CODES.P)
class GifAnimatedDrawableDecoder(
    request: ImageRequest,
    dataSource: DataSource,
) : BaseAnimatedImageDrawableDecoder(request, dataSource) {

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): GifAnimatedDrawableDecoder? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.valueOfMimeType(fetchResult.mimeType)
                // Some sites disguise the suffix of a GIF file as a JPEG, which must be identified by the file header
                if (imageFormat == ImageFormat.GIF || fetchResult.headerBytes.isAnimatedHeif()) {
                    return GifAnimatedDrawableDecoder(request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifAnimatedDrawableDecoder"

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