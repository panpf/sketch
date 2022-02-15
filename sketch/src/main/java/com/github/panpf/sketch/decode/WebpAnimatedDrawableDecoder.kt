package com.github.panpf.sketch.decode

import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.internal.isAnimatedWebP
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.request.repeatCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.P)
class WebpAnimatedDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    override suspend fun decode(): DrawableDecodeResult {
        val source = when (dataSource) {
            is AssetDataSource -> {
                ImageDecoder.createSource(sketch.appContext.assets, dataSource.assetFileName)
            }
            is ResourceDataSource -> {
                ImageDecoder.createSource(dataSource.resources, dataSource.drawableId)
            }
            is ContentDataSource -> {
                ImageDecoder.createSource(sketch.appContext.contentResolver, dataSource.contentUri)
            }
            is ByteArrayDataSource -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ImageDecoder.createSource(dataSource.data)
                } else {
                    ImageDecoder.createSource(ByteBuffer.wrap(dataSource.data))
                }
            }
            else -> {
                withContext(Dispatchers.IO) {
                    ImageDecoder.createSource(dataSource.file())
                }
            }
        }

        val drawable = ImageDecoder.decodeDrawable(source)

        if (drawable is AnimatedImageDrawable) {
            drawable.repeatCount = request.repeatCount()
                ?.takeIf { it != ANIMATION_REPEAT_INFINITE }
                ?: AnimatedImageDrawable.REPEAT_INFINITE
        }

        if (drawable !is Animatable) throw Exception("")
        val imageInfo = ImageInfo(drawable.intrinsicWidth, drawable.intrinsicHeight, MIME_TYPE)
        val animatableDrawable = SketchAnimatableDrawable(
            requestKey = request.key,
            requestUri = request.uriString,
            imageInfo = imageInfo,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            animatableDrawable = drawable,
            drawable::class.java.simpleName
        )
        val onStart = request.animationStartCallback()
        val onEnd = request.animationEndCallback()
        if (onStart != null || onEnd != null) {
            animatableDrawable.registerAnimationCallback(
                animatable2CompatCallbackOf(onStart, onEnd)
            )
        }
        return DrawableDecodeResult(
            drawable = animatableDrawable,
            imageInfo = imageInfo,
            exifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = null
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): WebpAnimatedDrawableDecoder? {
            if (request.disabledAnimationDrawable != true) {
                if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                    && fetchResult.headerBytes.isAnimatedWebP()
                ) {
                    return WebpAnimatedDrawableDecoder(sketch, request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "WebpAnimatedDrawableDecoder"
    }

    companion object {
        const val MIME_TYPE = "image/webp"
    }
}