package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap.Config
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.ImageRequest.BaseImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchException

fun DownloadRequest(
    context: Context,
    uriString: String?,
    configBlock: (DownloadRequest.Builder.() -> Unit)? = null
): DownloadRequest = DownloadRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

interface DownloadRequest : ImageRequest {

    override fun newBuilder(
        configBlock: (ImageRequest.Builder.() -> Unit)?
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    override fun newRequest(
        configBlock: (ImageRequest.Builder.() -> Unit)?
    ): ImageRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    fun newDownloadBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun newDownloadRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder : ImageRequest.Builder {

        constructor(context: Context, uriString: String?) : super(context, uriString)

        constructor(request: DownloadRequest) : super(request)

        fun listener(listener: Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                super.listener(listener as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?)
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: DownloadRequest) -> Unit = {},
            crossinline onCancel: (request: DownloadRequest) -> Unit = {},
            crossinline onError: (request: DownloadRequest, result: DownloadResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: DownloadRequest, result: DownloadResult.Success) -> Unit = { _, _ -> }
        ): Builder = listener(object :
            Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
            override fun onStart(request: DownloadRequest) = onStart(request)
            override fun onCancel(request: DownloadRequest) = onCancel(request)
            override fun onError(request: DownloadRequest, result: DownloadResult.Error) =
                onError(request, result)

            override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) =
                onSuccess(request, result)
        })

        fun progressListener(
            progressListener: ProgressListener<DownloadRequest>?
        ): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            super.progressListener(progressListener as ProgressListener<ImageRequest>?)
        }

        fun target(target: DownloadTarget?): Builder = apply {
            super.target(target)
        }

        /**
         * Convenience function to create and set the [DownloadTarget].
         */
        inline fun target(
            crossinline onStart: () -> Unit = {},
            crossinline onError: (exception: SketchException) -> Unit = {},
            crossinline onSuccess: (result: DownloadData) -> Unit = {}
        ) = target(object : DownloadTarget {
            override fun onStart() = onStart()
            override fun onError(exception: SketchException) = onError(exception)
            override fun onSuccess(result: DownloadData) = onSuccess(result)
        })

        override fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            super.lifecycle(lifecycle)
        }

        override fun build(): DownloadRequest {
            return super.build() as DownloadRequest
        }


        override fun depth(depth: RequestDepth?): Builder = apply {
            super.depth(depth)
        }

        override fun depthFrom(from: String?): Builder = apply {
            super.depthFrom(from)
        }

        override fun parameters(parameters: Parameters?): Builder = apply {
            super.parameters(parameters)
        }

        override fun setParameter(key: String, value: Any?, cacheKey: String?): Builder = apply {
            super.setParameter(key, value, cacheKey)
        }

        override fun removeParameter(key: String): Builder = apply {
            super.removeParameter(key)
        }

        override fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            super.httpHeaders(httpHeaders)
        }

        override fun addHttpHeader(name: String, value: String): Builder = apply {
            super.addHttpHeader(name, value)
        }

        override fun setHttpHeader(name: String, value: String): Builder = apply {
            super.setHttpHeader(name, value)
        }

        override fun removeHttpHeader(name: String): Builder = apply {
            super.removeHttpHeader(name)
        }

        override fun downloadDiskCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.downloadDiskCachePolicy(cachePolicy)
        }

        override fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            super.bitmapConfig(bitmapConfig)
        }

        override fun bitmapConfig(bitmapConfig: Config): Builder = apply {
            super.bitmapConfig(bitmapConfig)
        }

        override fun lowQualityBitmapConfig(): Builder = apply {
            super.lowQualityBitmapConfig()
        }

        override fun middenQualityBitmapConfig(): Builder = apply {
            super.middenQualityBitmapConfig()
        }

        override fun highQualityBitmapConfig(): Builder = apply {
            super.highQualityBitmapConfig()
        }

        override fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                super.colorSpace(colorSpace)
            }
        }

        @Suppress("OverridingDeprecatedMember", "DeprecatedCallableAddReplaceWith")
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        override fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            @Suppress("DEPRECATION")
            super.preferQualityOverSpeed(inPreferQualityOverSpeed)
        }

        override fun resize(
            size: Size, precision: PrecisionDecider, scale: ScaleDecider
        ): Builder = apply {
            super.resize(size, precision, scale)
        }

        override fun resize(
            size: Size, precision: Precision, scale: Scale
        ): Builder = apply {
            super.resize(size, precision, scale)
        }

        override fun resize(size: Size): Builder = apply {
            super.resize(size)
        }

        override fun resize(
            width: Int, height: Int, precision: PrecisionDecider, scale: ScaleDecider
        ): Builder = apply {
            super.resize(width, height, precision, scale)
        }

        override fun resize(
            width: Int, height: Int, precision: Precision, scale: Scale
        ): Builder = apply {
            super.resize(width, height, precision, scale)
        }

        override fun resize(width: Int, height: Int): Builder = apply {
            super.resize(width, height)
        }

        override fun resizeSize(size: Size?): Builder = apply {
            super.resizeSize(size)
        }

        override fun resizeSize(width: Int, height: Int): Builder = apply {
            super.resizeSize(width, height)
        }

        override fun resizeSizeResolver(sizeResolver: SizeResolver?): Builder = apply {
            super.resizeSizeResolver(sizeResolver)
        }

        override fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            super.resizePrecision(precisionDecider)
        }

        override fun resizePrecision(precision: Precision): Builder = apply {
            super.resizePrecision(precision)
        }

        override fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            super.resizeScale(scaleDecider)
        }

        override fun resizeScale(scale: Scale): Builder = apply {
            super.resizeScale(scale)
        }

        override fun transformations(transformations: List<Transformation>?): Builder = apply {
            super.transformations(transformations)
        }

        override fun transformations(vararg transformations: Transformation): Builder = apply {
            super.transformations(*transformations)
        }

        override fun addTransformations(transformations: List<Transformation>): Builder = apply {
            super.addTransformations(transformations)
        }

        override fun addTransformations(vararg transformations: Transformation): Builder = apply {
            super.addTransformations(*transformations)
        }

        override fun removeTransformations(transformations: List<Transformation>): Builder = apply {
            super.removeTransformations(transformations)
        }

        override fun removeTransformations(vararg transformations: Transformation): Builder =
            apply {
                super.removeTransformations(*transformations)
            }

        override fun disabledReuseBitmap(disabled: Boolean?): Builder = apply {
            super.disabledReuseBitmap(disabled)
        }

        override fun ignoreExifOrientation(ignore: Boolean?): Builder = apply {
            super.ignoreExifOrientation(ignore)
        }

        override fun bitmapResultDiskCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.bitmapResultDiskCachePolicy(cachePolicy)
        }

        override fun placeholder(stateImage: StateImage?): Builder = apply {
            super.placeholder(stateImage)
        }

        override fun placeholder(drawable: Drawable): Builder = apply {
            super.placeholder(drawable)
        }

        override fun placeholder(drawableResId: Int): Builder = apply {
            super.placeholder(drawableResId)
        }

        override fun error(
            stateImage: StateImage?, configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.error(stateImage, configBlock)
        }

        override fun error(
            drawable: Drawable, configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.error(drawable, configBlock)
        }

        override fun error(
            drawableResId: Int, configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.error(drawableResId, configBlock)
        }

        override fun transition(transition: Factory?): Builder = apply {
            super.transition(transition)
        }

        override fun crossfade(
            durationMillis: Int, preferExactIntrinsicSize: Boolean
        ): Builder = apply {
            super.crossfade(durationMillis, preferExactIntrinsicSize)
        }

        override fun disabledAnimatedImage(disabled: Boolean?): Builder = apply {
            super.disabledAnimatedImage(disabled)
        }

        override fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean?): Builder = apply {
            super.resizeApplyToDrawable(resizeApplyToDrawable)
        }

        override fun bitmapMemoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.bitmapMemoryCachePolicy(cachePolicy)
        }


        override fun merge(options: ImageOptions?): Builder = apply {
            super.merge(options)
        }

        override fun global(options: ImageOptions?): Builder = apply {
            super.global(options)
        }
    }

    class DownloadRequestImpl internal constructor(
        override val context: Context,
        override val uriString: String,
        override val listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?,
        override val progressListener: ProgressListener<ImageRequest>?,
        override val target: Target?,
        override val lifecycle: Lifecycle,
        override val definedOptions: ImageOptions,
        override val globalOptions: ImageOptions?,
        override val depth: RequestDepth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val downloadDiskCachePolicy: CachePolicy,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScaleDecider: ScaleDecider,
        override val transformations: List<Transformation>?,
        override val disabledReuseBitmap: Boolean,
        override val ignoreExifOrientation: Boolean,
        override val bitmapResultDiskCachePolicy: CachePolicy,
        override val placeholderImage: StateImage?,
        override val errorImage: StateImage?,
        override val transition: Factory?,
        override val disabledAnimatedImage: Boolean,
        override val resizeApplyToDrawable: Boolean,
        override val bitmapMemoryCachePolicy: CachePolicy,
    ) : BaseImageRequest(), DownloadRequest
}