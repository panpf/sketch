package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap.Config
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.AnyThread
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.ImageRequest.BaseImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchException

/**
 * Build and set the [DownloadRequest]
 */
fun DownloadRequest(
    context: Context,
    uriString: String?,
    configBlock: (DownloadRequest.Builder.() -> Unit)? = null
): DownloadRequest = DownloadRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()


/**
 * Display the image request, and finally get a [DownloadData].
 *
 * [Target] can only be [DownloadTarget], [ImageResult] can only be [DownloadResult]
 */
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

    /**
     * Create a new [DownloadRequest.Builder] based on the current [DownloadRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newDownloadBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [DownloadRequest] based on the current [DownloadRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newDownloadRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    /**
     * Execute current DownloadRequest asynchronously.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    @AnyThread
    fun enqueue(): Disposable<DownloadResult> {
        return context.sketch.enqueue(this)
    }

    /**
     * Execute current DownloadRequest synchronously in the current coroutine scope.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [DownloadResult.Success] if the request completes successfully. Else, returns an [DownloadResult.Error].
     */
    suspend fun execute(): DownloadResult {
        return context.sketch.execute(this)
    }

    class Builder : ImageRequest.Builder {

        constructor(context: Context, uriString: String?) : super(context, uriString)

        constructor(request: DownloadRequest) : super(request)

        /**
         * Set the [Listener]
         */
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

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener<DownloadRequest>?
        ): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            super.progressListener(progressListener as ProgressListener<ImageRequest>?)
        }

        /**
         * Set the [Target]. Can only be an implementation of [DownloadTarget]
         */
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


        override fun depth(depth: Depth?): Builder = apply {
            super.depth(depth)
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

        override fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.downloadCachePolicy(cachePolicy)
        }

        override fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            super.bitmapConfig(bitmapConfig)
        }

        override fun bitmapConfig(bitmapConfig: Config): Builder = apply {
            super.bitmapConfig(bitmapConfig)
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

        override fun resize(resize: Resize?): Builder = apply {
            super.resize(resize)
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

        override fun disallowReuseBitmap(disabled: Boolean?): Builder = apply {
            super.disallowReuseBitmap(disabled)
        }

        override fun ignoreExifOrientation(ignore: Boolean?): Builder = apply {
            super.ignoreExifOrientation(ignore)
        }

        override fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.resultCachePolicy(cachePolicy)
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
            durationMillis: Int, preferExactIntrinsicSize: Boolean, alwaysUse: Boolean
        ): Builder = apply {
            super.crossfade(durationMillis, preferExactIntrinsicSize, alwaysUse)
        }

        override fun disallowAnimatedImage(disabled: Boolean?): Builder = apply {
            super.disallowAnimatedImage(disabled)
        }

        override fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean?): Builder = apply {
            super.resizeApplyToDrawable(resizeApplyToDrawable)
        }

        override fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            super.memoryCachePolicy(cachePolicy)
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
        override val depth: Depth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val downloadCachePolicy: CachePolicy,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver?,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScaleDecider: ScaleDecider,
        override val transformations: List<Transformation>?,
        override val disallowReuseBitmap: Boolean,
        override val ignoreExifOrientation: Boolean,
        override val resultCachePolicy: CachePolicy,
        override val placeholder: StateImage?,
        override val error: StateImage?,
        override val transition: Factory?,
        override val disallowAnimatedImage: Boolean,
        override val resizeApplyToDrawable: Boolean,
        override val memoryCachePolicy: CachePolicy,
    ) : BaseImageRequest(), DownloadRequest {

        override fun toString(): String {
            return "DownloadRequest(${key})"
        }
    }
}