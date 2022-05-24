@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Movie
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.mimeTypeToImageFormat
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount

/**
 * A [DrawableDecoder] that uses [Movie] to decode GIFs.
 *
 * Only the following attributes are supported:
 *
 * repeatCount
 *
 * animatedTransformation
 *
 * onAnimationStart
 *
 * onAnimationEnd
 *
 * disallowReuseBitmap
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class GifMovieDrawableDecoder constructor(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    @WorkerThread
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
            if (!request.disallowReuseBitmap) {
                object : MovieDrawable.BitmapCreator {
                    override fun createBitmap(width: Int, height: Int, config: Config): Bitmap =
                        sketch.bitmapPool.getOrCreate(width, height, config)

                    override fun freeBitmap(bitmap: Bitmap) {
                        sketch.bitmapPool.free(bitmap, "MovieDrawable:recycle")
                    }
                }
            } else null,
        ).apply {
            setRepeatCount(request.repeatCount() ?: ANIMATION_REPEAT_INFINITE)

            // Set the animated transformation to be applied on each frame.
            setAnimatedTransformation(request.animatedTransformation())
        }

        val imageInfo = ImageInfo(width, height, ImageFormat.GIF.mimeType)

        val animatableDrawable = SketchAnimatableDrawable(
            requestKey = request.key,
            requestUri = request.uriString,
            imageInfo = imageInfo,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = null,
            animatableDrawable = movieDrawable,
            "MovieDrawable"
        ).apply {
            // Set the start and end animation callbacks if any one is supplied through the request.
            val onStart = request.animationStartCallback()
            val onEnd = request.animationEndCallback()
            if (onStart != null || onEnd != null) {
                registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
            }
        }

        return DrawableDecodeResult(
            drawable = animatableDrawable,
            imageInfo = imageInfo,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = null,
        )
    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): GifMovieDrawableDecoder? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !request.disallowAnimatedImage) {
                val imageFormat = mimeTypeToImageFormat(fetchResult.mimeType)
                val isGif =
                    if (imageFormat == null) fetchResult.headerBytes.isGif() else imageFormat == ImageFormat.GIF
                if (isGif) {
                    return GifMovieDrawableDecoder(sketch, request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifDrawableDecoder"

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
