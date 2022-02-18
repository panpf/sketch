package com.github.panpf.sketch.decode

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.BaseAnimatedImageDrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isAnimatedHeif
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestExtras

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
class HeifAnimatedDrawableDecoder(
    sketch: Sketch,
    request: DisplayRequest,
    dataSource: DataSource,
) : BaseAnimatedImageDrawableDecoder(sketch, request, dataSource) {

    @RequiresApi(Build.VERSION_CODES.P)
    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): HeifAnimatedDrawableDecoder? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !request.disabledAnimationDrawable) {
                val imageFormat = ImageFormat.valueOfMimeType(fetchResult.mimeType)
                if ((imageFormat == ImageFormat.HEIC || imageFormat == ImageFormat.HEIF)
                    && fetchResult.headerBytes.isAnimatedHeif()
                ) {
                    return HeifAnimatedDrawableDecoder(sketch, request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "HeifAnimatedDrawableDecoder"
    }
}