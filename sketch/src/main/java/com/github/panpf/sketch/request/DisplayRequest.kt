package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap.Config
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import androidx.annotation.RequiresApi
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
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.ImageViewDisplayTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size

/**
 * Build and set the [DisplayRequest]
 */
fun DisplayRequest(
    context: Context,
    uriString: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): DisplayRequest = DisplayRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

/**
 * Build and set the [DisplayRequest], target is an ImageView
 */
fun DisplayRequest(
    imageView: ImageView,
    uriString: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): DisplayRequest = DisplayRequest.Builder(imageView.context, uriString).apply {
    target(imageView)
    configBlock?.invoke(this)
}.build()


/**
 * Display the image request, and finally get a Drawable.
 *
 * [Target] can only be [DisplayTarget], [ImageResult] can only be [DisplayResult]
 */
interface DisplayRequest : ImageRequest {

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
     * Create a new [DisplayRequest.Builder] based on the current [DisplayRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newDisplayBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [DisplayRequest] based on the current [DisplayRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newDisplayRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): DisplayRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder : ImageRequest.Builder {

        constructor(context: Context, uriString: String?) : super(context, uriString)

        constructor(request: DisplayRequest) : super(request)

        /**
         * Set the [Listener]
         */
        fun listener(
            listener: Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error>?
        ): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            super.listener(listener as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?)
        }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: DisplayRequest) -> Unit = {},
            crossinline onCancel: (request: DisplayRequest) -> Unit = {},
            crossinline onError: (request: DisplayRequest, result: DisplayResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: DisplayRequest, result: DisplayResult.Success) -> Unit = { _, _ -> }
        ): Builder = listener(object :
            Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error> {
            override fun onStart(request: DisplayRequest) = onStart(request)
            override fun onCancel(request: DisplayRequest) = onCancel(request)
            override fun onError(
                request: DisplayRequest, result: DisplayResult.Error
            ) = onError(request, result)

            override fun onSuccess(
                request: DisplayRequest, result: DisplayResult.Success
            ) = onSuccess(request, result)
        })

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener<DisplayRequest>?
        ): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            super.progressListener(progressListener as ProgressListener<ImageRequest>?)
        }

        /**
         * Set the [Target]. Can only be an implementation of [DisplayTarget]
         */
        fun target(target: DisplayTarget?): Builder = apply {
            super.target(target)
        }

        /**
         * Convenience function to create and set the [DisplayTarget].
         */
        inline fun target(
            crossinline onStart: (placeholder: Drawable?) -> Unit = {},
            crossinline onError: (error: Drawable?) -> Unit = {},
            crossinline onSuccess: (result: Drawable) -> Unit = {}
        ) = target(object : DisplayTarget {
            override fun onStart(placeholder: Drawable?) = onStart(placeholder)
            override fun onError(error: Drawable?) = onError(error)
            override fun onSuccess(result: Drawable) = onSuccess(result)
        })

        /**
         * Set the target to the ImageView
         */
        fun target(imageView: ImageView): Builder = apply {
            super.target(ImageViewDisplayTarget(imageView))
        }

        override fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            super.lifecycle(lifecycle)
        }

        override fun build(): DisplayRequest {
            return super.build() as DisplayRequest
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

        @RequiresApi(VERSION_CODES.O)
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
            durationMillis: Int, preferExactIntrinsicSize: Boolean, alwaysUse: Boolean,
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

    class DisplayRequestImpl internal constructor(
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
    ) : BaseImageRequest(), DisplayRequest {

        override fun toString(): String {
            return "DisplayRequest(${key})"
        }
    }
}