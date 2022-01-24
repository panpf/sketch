@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.decode

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Movie
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.util.animatable2CompatCallbackOf
import java.util.Base64.Decoder

/**
 * A [Decoder] that uses [Movie] to decode GIFs.
 *
 * NOTE: Prefer using [ImageDecoderDecoder] on API 28 and above.
 *
 * @param enforceMinimumFrameDelay If true, rewrite a GIF's frame delay to a default value if
 *  it is below a threshold. See https://github.com/coil-kt/coil/issues/540 for more info.
 */
class GifDrawableDecoder constructor(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource,
    private val imageInfo: ImageInfo,
) : DrawableDecoder {

    override suspend fun decodeDrawable(): DrawableDecodeResult {
        val movie: Movie? = dataSource.newInputStream().use { Movie.decodeStream(it) }

        check(movie != null && movie.width() > 0 && movie.height() > 0) { "Failed to decode GIF." }

        val movieDrawable = MovieDrawable(
            movie = movie,
            config = when {
                movie.isOpaque && request.bitmapConfig?.isLowQuality == true -> RGB_565
                else -> ARGB_8888
            },
            sketch.bitmapPoolHelper,
        )

        movieDrawable.setRepeatCount(request.repeatCount() ?: MovieDrawable.REPEAT_INFINITE)

        // Set the start and end animation callbacks if any one is supplied through the request.
        val onStart = request.animationStartCallback()
        val onEnd = request.animationEndCallback()
        if (onStart != null || onEnd != null) {
            movieDrawable.registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
        }

        // Set the animated transformation to be applied on each frame.
        movieDrawable.setAnimatedTransformation(request.animatedTransformation())

        return DrawableDecodeResult(
            drawable = SketchGifDrawable(
                request.key,
                request.uriString,
                imageInfo,
                dataSource.from,
                movieDrawable
            ),
            info = imageInfo, from = dataSource.from
        )
    }

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch, request: DisplayRequest, fetchResult: FetchResult
        ): GifDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                val imageInfo = fetchResult.imageInfo
                val mimeType = fetchResult.imageInfo?.mimeType
                if (imageInfo != null && MIME_TYPE.equals(mimeType, ignoreCase = true)) {
                    return GifDrawableDecoder(sketch, request, fetchResult.dataSource, imageInfo)
                } else if (imageInfo != null && fetchResult.headerBytes.isGif()) {
                    // This will not happen unless there is a bug in the BitmapFactory
                    val newImageInfo = ImageInfo(
                        MIME_TYPE,
                        imageInfo.width,
                        imageInfo.height,
                        imageInfo.exifOrientation
                    )
                    return GifDrawableDecoder(sketch, request, fetchResult.dataSource, newImageInfo)
                }
            }
            return null
        }
    }

    companion object {
        const val MIME_TYPE = "image/gif"
        const val REPEAT_COUNT_KEY = "coil#repeat_count"
        const val ANIMATED_TRANSFORMATION_KEY = "coil#animated_transformation"
        const val ANIMATION_START_CALLBACK_KEY = "coil#animation_start_callback"
        const val ANIMATION_END_CALLBACK_KEY = "coil#animation_end_callback"
    }
}
