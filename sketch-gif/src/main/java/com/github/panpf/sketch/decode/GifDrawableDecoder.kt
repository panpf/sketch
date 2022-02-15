@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.decode

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Movie
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
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
import com.github.panpf.sketch.request.internal.RequestExtras
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
@RequiresApi(Build.VERSION_CODES.KITKAT)
class GifDrawableDecoder constructor(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    override suspend fun decode(): DrawableDecodeResult {
        val movie: Movie? = dataSource.newInputStream().use { Movie.decodeStream(it) }

        val width = movie?.width() ?: 0
        val height = movie?.height() ?: 0
        check(movie != null && width > 0 && height > 0) { "Failed to decode GIF." }

        val movieDrawable = MovieDrawable(
            movie = movie,
            config = when {
                movie.isOpaque && request.bitmapConfig?.isLowQuality == true -> RGB_565
                else -> ARGB_8888
            },
            sketch.bitmapPool,
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

        val imageInfo = ImageInfo(width, height, MIME_TYPE)

        return DrawableDecodeResult(
            drawable = SketchGifDrawable(
                requestKey = request.key,
                requestUri = request.uriString,
                imageInfo = imageInfo,
                imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
                dataFrom = dataSource.dataFrom,
                movieDrawable = movieDrawable
            ),
            imageInfo = imageInfo,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom
        )
    }

    override fun close() {

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): GifDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
                    return GifDrawableDecoder(sketch, request, fetchResult.dataSource)
                } else if (fetchResult.headerBytes.isGif()) {
                    // Some sites disguise the suffix of a GIF file as a JPEG, which must be identified by the file header
                    return GifDrawableDecoder(sketch, request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifDrawableDecoder"
    }

    companion object {
        const val MIME_TYPE = "image/gif"
        const val REPEAT_COUNT_KEY = "sketch#repeat_count"
        const val ANIMATED_TRANSFORMATION_KEY = "sketch#animated_transformation"
        const val ANIMATION_START_CALLBACK_KEY = "sketch#animation_start_callback"
        const val ANIMATION_END_CALLBACK_KEY = "sketch#animation_end_callback"
    }
}
