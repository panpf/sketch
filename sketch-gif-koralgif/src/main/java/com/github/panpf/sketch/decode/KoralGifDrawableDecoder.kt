package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import androidx.exifinterface.media.ExifInterface
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.GifDrawableDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.drawable.SketchKoralGifDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.repeatCount
import pl.droidsonroids.gif.GifDrawable

class KoralGifDrawableDecoder(
    private val request: DisplayRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    override suspend fun decode(): DrawableDecodeResult {
        val request = request
        val gifDrawable = when (val source = dataSource) {
            is ByteArrayDataSource -> {
                GifDrawable(source.data)
            }
            is DiskCacheDataSource -> {
                GifDrawable(source.diskCacheSnapshot.file)
            }
            is ResourceDataSource -> {
                GifDrawable(source.context.resources, source.drawableId)
            }
            is ContentDataSource -> {
                val contentResolver = source.context.contentResolver
                GifDrawable(contentResolver, source.contentUri)
            }
            is FileDataSource -> {
                GifDrawable(source.file)
            }
            is AssetDataSource -> {
                GifDrawable(source.context.assets, source.assetFileName)
            }
            else -> {
                throw Exception("Unsupported DataSource: ${source::class.qualifiedName}")
            }
        }

        gifDrawable.loopCount =
            (request.repeatCount() ?: MovieDrawable.REPEAT_INFINITE).takeIf { it != -1 } ?: 0

        // Set the animated transformation to be applied on each frame.
//        drawable.setAnimatedTransformation(request.animatedTransformation())

        val width = gifDrawable.intrinsicWidth
        val height = gifDrawable.intrinsicHeight
        val imageInfo = ImageInfo(width, height, MIME_TYPE)
        val drawable = SketchKoralGifDrawable(
            requestKey = request.key,
            requestUri = request.uriString,
            imageInfo = imageInfo,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            gifDrawable = gifDrawable
        )

        // Set the start and end animation callbacks if any one is supplied through the request.
        val onStart = request.animationStartCallback()
        val onEnd = request.animationEndCallback()
        if (onStart != null || onEnd != null) {
            drawable.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable?) {
                    onStart?.invoke()
                }

                override fun onAnimationEnd(drawable: Drawable?) {
                    onEnd?.invoke()
                }
            })
        }

        return DrawableDecodeResult(
            drawable,
            imageInfo,
            ExifInterface.ORIENTATION_UNDEFINED,
            dataSource.dataFrom
        )
    }

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): KoralGifDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
                    return KoralGifDrawableDecoder(request, fetchResult.dataSource)
                } else if (fetchResult.headerBytes.isGif()) {
                    return KoralGifDrawableDecoder(request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "KoralGifDrawableDecoder"
    }
}