package com.github.panpf.sketch.decode

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.BaseAnimatedImageDrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
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
class WebpAnimatedDrawableDecoder(
    request: ImageRequest,
    dataSource: DataSource,
) : BaseAnimatedImageDrawableDecoder(request, dataSource) {

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): WebpAnimatedDrawableDecoder? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.valueOfMimeType(fetchResult.mimeType)
                if (imageFormat == ImageFormat.WEBP && fetchResult.headerBytes.isAnimatedWebP()) {
                    return WebpAnimatedDrawableDecoder(request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "WebpAnimatedDrawableDecoder"

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