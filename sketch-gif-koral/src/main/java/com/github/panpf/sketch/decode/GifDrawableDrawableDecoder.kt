package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.InSampledTransformed
import com.github.panpf.sketch.decode.internal.calculateSampleSizeWithTolerance
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.repeatCount
import pl.droidsonroids.gif.GifInfoHandleCompat
import pl.droidsonroids.gif.GifOptions
import pl.droidsonroids.gif.transforms.Transform

/**
 * Only the following attributes are supported:
 *
 * resize.size
 *
 * resize.precision: It is always LESS_PIXELS
 *
 * repeatCount
 *
 * animatedTransformation
 *
 * onAnimationStart
 *
 * onAnimationEnd
 */
// todo GifDrawable will play a random number of times on API 28 and then stop playing
class GifDrawableDrawableDecoder(
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): DrawableDecodeResult {
        val gifInfoHandleCompat = GifInfoHandleCompat(dataSource)
        val imageWidth = gifInfoHandleCompat.width
        val imageHeight = gifInfoHandleCompat.height
        val resize = request.resize
        var inSampleSize = 1
        if (resize != null) {
            inSampleSize =
                calculateSampleSizeWithTolerance(
                    imageWidth,
                    imageHeight,
                    resize.width,
                    resize.height
                )
            gifInfoHandleCompat.setOptions(GifOptions().apply {
                setInSampleSize(inSampleSize)
            })
        }
        val gifDrawable = gifInfoHandleCompat.toGifDrawable().apply {
            loopCount =
                (request.repeatCount() ?: ANIMATION_REPEAT_INFINITE).takeIf { it != -1 } ?: 0

            // Set the animated transformation to be applied on each frame.
            val transformation = request.animatedTransformation()
            if (transformation != null) {
                transform = object : Transform {
                    override fun onBoundsChange(bounds: Rect?) {
                    }

                    override fun onDraw(canvas: Canvas, paint: Paint?, buffer: Bitmap?) {
                        transformation.transform(canvas)
                    }
                }
            }
        }

        val transformedList =
            if (inSampleSize != 1) listOf(InSampledTransformed(inSampleSize)) else null
        val imageInfo = ImageInfo(imageWidth, imageHeight, ImageFormat.GIF.mimeType)
        val animatableDrawable = SketchAnimatableDrawable(
            requestKey = this.request.key,
            requestUri = this.request.uriString,
            imageInfo = imageInfo,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList,
            animatableDrawable = gifDrawable,
            "GifDrawable"
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
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): GifDrawableDrawableDecoder? {
            if (!request.disabledAnimatedImage) {
                val imageFormat = ImageFormat.valueOfMimeType(fetchResult.mimeType)
                // Some sites disguise the suffix of a GIF file as a JPEG, which must be identified by the file header
                if (imageFormat == ImageFormat.GIF || fetchResult.headerBytes.isGif()) {
                    return GifDrawableDrawableDecoder(request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifDrawableDrawableDecoder"
    }
}