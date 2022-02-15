package com.github.panpf.sketch.decode

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.GifDrawableDecoder.Companion.MIME_TYPE
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isGif
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
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
            (request.repeatCount() ?: ANIMATION_REPEAT_INFINITE).takeIf { it != -1 } ?: 0

        // Set the animated transformation to be applied on each frame.
//        drawable.setAnimatedTransformation(request.animatedTransformation())

        val width = gifDrawable.intrinsicWidth
        val height = gifDrawable.intrinsicHeight
        val imageInfo = ImageInfo(width, height, MIME_TYPE)
        val drawable = SketchAnimatableDrawable(
            requestKey = request.key,
            requestUri = request.uriString,
            imageInfo = imageInfo,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            animatableDrawable = gifDrawable,
            "KoralGifDrawable"
        )

        // Set the start and end animation callbacks if any one is supplied through the request.
        val onStart = request.animationStartCallback()
        val onEnd = request.animationEndCallback()
        if (onStart != null || onEnd != null) {
            drawable.registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
        }

        return DrawableDecodeResult(
            drawable,
            imageInfo,
            ExifInterface.ORIENTATION_UNDEFINED,
            dataSource.dataFrom
        )
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