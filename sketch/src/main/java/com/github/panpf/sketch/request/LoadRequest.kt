package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap
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
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchException

/**
 * Build and set the [LoadRequest]
 */
fun LoadRequest(
    context: Context,
    uriString: String?,
    configBlock: (LoadRequest.Builder.() -> Unit)? = null
): LoadRequest = LoadRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()


/**
 * Display the image request, and finally get a Bitmap.
 *
 * [Target] can only be [LoadTarget], [ImageResult] can only be [LoadResult]
 */
interface LoadRequest : ImageRequest {

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
     * Create a new [LoadRequest.Builder] based on the current [LoadRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newLoadBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [LoadRequest] based on the current [LoadRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newLoadRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): LoadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder : ImageRequest.Builder {

        constructor(context: Context, uriString: String?) : super(context, uriString)

        constructor(request: LoadRequest) : super(request)

        /**
         * Set the [Listener]
         */
        fun listener(listener: Listener<LoadRequest, LoadResult.Success, LoadResult.Error>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                super.listener(listener as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?)
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: LoadRequest) -> Unit = {},
            crossinline onCancel: (request: LoadRequest) -> Unit = {},
            crossinline onError: (request: LoadRequest, result: LoadResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: LoadRequest, result: LoadResult.Success) -> Unit = { _, _ -> }
        ): Builder =
            listener(object :
                Listener<LoadRequest, LoadResult.Success, LoadResult.Error> {
                override fun onStart(request: LoadRequest) = onStart(request)
                override fun onCancel(request: LoadRequest) = onCancel(request)
                override fun onError(request: LoadRequest, result: LoadResult.Error) =
                    onError(request, result)

                override fun onSuccess(request: LoadRequest, result: LoadResult.Success) =
                    onSuccess(request, result)
            })

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener<LoadRequest>?
        ): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            super.progressListener(progressListener as ProgressListener<ImageRequest>?)
        }

        /**
         * Set the [Target]. Can only be an implementation of [LoadTarget]
         */
        fun target(target: LoadTarget?): Builder = apply {
            super.target(target)
        }

        /**
         * Convenience function to create and set the [LoadTarget].
         */
        inline fun target(
            crossinline onStart: () -> Unit = {},
            crossinline onError: (exception: SketchException) -> Unit = {},
            crossinline onSuccess: (result: Bitmap) -> Unit = {}
        ) = target(object : LoadTarget {
            override fun onStart() = onStart()
            override fun onError(exception: SketchException) = onError(exception)
            override fun onSuccess(result: Bitmap) = onSuccess(result)
        })

        override fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            super.lifecycle(lifecycle)
        }

        override fun build(): LoadRequest {
            return super.build() as LoadRequest
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
            durationMillis: Int, preferExactIntrinsicSize: Boolean
        ): Builder = apply {
            super.crossfade(durationMillis, preferExactIntrinsicSize)
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

    class LoadRequestImpl internal constructor(
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
    ) : BaseImageRequest(), LoadRequest {

        override fun toString(): String {
            return "LoadRequest(${key})"
        }
    }
}