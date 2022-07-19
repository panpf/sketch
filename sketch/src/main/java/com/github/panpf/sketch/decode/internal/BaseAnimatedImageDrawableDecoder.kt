package com.github.panpf.sketch.decode.internal

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.transform.asPostProcessor
import java.nio.ByteBuffer

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
abstract class BaseAnimatedImageDrawableDecoder(
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): DrawableDecodeResult {
        val source = when (dataSource) {
            is AssetDataSource -> {
                ImageDecoder.createSource(request.context.assets, dataSource.assetFileName)
            }
            is ResourceDataSource -> {
                ImageDecoder.createSource(dataSource.resources, dataSource.drawableId)
            }
            is ContentDataSource -> {
                ImageDecoder.createSource(request.context.contentResolver, dataSource.contentUri)
            }
            is ByteArrayDataSource -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ImageDecoder.createSource(dataSource.data)
                } else {
                    ImageDecoder.createSource(ByteBuffer.wrap(dataSource.data))
                }
            }
            else -> {
                ImageDecoder.createSource(dataSource.file())
            }
        }

        var imageInfo: ImageInfo? = null
        var inSampleSize = 1
        val drawable = ImageDecoder.decodeDrawable(source) { decoder, info, _ ->
            imageInfo = ImageInfo(info.size.width, info.size.height, info.mimeType)
            val resize = request.resize
            if (resize != null) {
                inSampleSize = calculateSampleSize(
                    info.size.width, info.size.height, resize.width, resize.height
                )
                decoder.setTargetSampleSize(inSampleSize)

                request.colorSpace?.let {
                    decoder.setTargetColorSpace(it)
                }

                // Set the animated transformation to be applied on each frame.
                decoder.postProcessor = request.animatedTransformation?.asPostProcessor()
            }
        }
        if (drawable !is AnimatedImageDrawable) {
            throw Exception("Only support AnimatedImageDrawable")
        }
        drawable.repeatCount = request.repeatCount
            ?.takeIf { it != ANIMATION_REPEAT_INFINITE }
            ?: AnimatedImageDrawable.REPEAT_INFINITE

        val transformedList =
            if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
        val animatableDrawable = SketchAnimatableDrawable(
            imageUri = request.uriString,
            requestKey = request.key,
            requestCacheKey = request.cacheKey,
            imageInfo = imageInfo!!,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList,
            // AnimatedImageDrawable cannot be scaled using bounds, which will be exposed in the ResizeDrawable
            // Use ScaledAnimatedImageDrawable package solution to this it
            animatableDrawable = ScaledAnimatedImageDrawable(drawable),
            animatableDrawableName = drawable::class.java.simpleName
        ).apply {
            val onStart = request.animationStartCallback
            val onEnd = request.animationEndCallback
            if (onStart != null || onEnd != null) {
                registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
            }
        }
        return DrawableDecodeResult(
            drawable = animatableDrawable,
            imageInfo = imageInfo!!,
            imageExifOrientation = ExifInterface.ORIENTATION_UNDEFINED,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList
        )
    }
}