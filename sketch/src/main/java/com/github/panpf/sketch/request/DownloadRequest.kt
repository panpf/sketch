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
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size

fun DownloadRequest(
    context: Context,
    uriString: String?,
    configBlock: (DownloadRequest.Builder.() -> Unit)? = null
): DownloadRequest = DownloadRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

fun DownloadRequestBuilder(
    context: Context,
    uriString: String?,
    configBlock: (DownloadRequest.Builder.() -> Unit)? = null
): DownloadRequest.Builder = DownloadRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}

interface DownloadRequest : ImageRequest {

    override fun newBuilder(
        context: Context,
        configBlock: (ImageRequest.Builder.() -> Unit)?
    ): Builder = Builder(context, this).apply {
        configBlock?.invoke(this)
    }

    override fun newRequest(
        context: Context,
        configBlock: (ImageRequest.Builder.() -> Unit)?
    ): ImageRequest = Builder(context, this).apply {
        configBlock?.invoke(this)
    }.build()

    fun newDownloadBuilder(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(context, this).apply {
        configBlock?.invoke(this)
    }

    fun newDownloadRequest(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): DownloadRequest = Builder(context, this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder : ImageRequest.Builder {

        //        constructor(context: Context, uriString: String?) : super(context, DOWNLOAD, uriString)
        constructor(context: Context, uriString: String?) : super(context, uriString)

        constructor(context: Context, request: DownloadRequest) : super(context, request)

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
        ): Builder =
            listener(object :
                Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
                override fun onStart(request: DownloadRequest) = onStart(request)
                override fun onCancel(request: DownloadRequest) = onCancel(request)
                override fun onError(request: DownloadRequest, result: DownloadResult.Error) =
                    onError(request, result)

                override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) =
                    onSuccess(request, result)
            })

        fun target(target: DownloadTarget): Builder = apply {
            super.target(target)
        }

        override fun build(): DownloadRequest {
            return super.build() as DownloadRequest
        }


        override fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            super.lifecycle(lifecycle)
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

        override fun networkContentDiskCachePolicy(networkContentDiskCachePolicy: CachePolicy?): Builder =
            apply {
                super.networkContentDiskCachePolicy(networkContentDiskCachePolicy)
            }

        override fun bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy: CachePolicy?): Builder =
            apply {
                super.bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy)
            }

        override fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            super.bitmapConfig(bitmapConfig)
        }

        override fun bitmapConfig(bitmapConfig: Config?): Builder = apply {
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

        @Suppress("OverridingDeprecatedMember")
        override fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            @Suppress("DEPRECATION")
            super.preferQualityOverSpeed(inPreferQualityOverSpeed)
        }

        override fun resizeSizeResolver(sizeResolver: SizeResolver?): Builder = apply {
            super.resizeSizeResolver(sizeResolver)
        }

        override fun resizeSize(size: Size?): Builder = apply {
            super.resizeSize(size)
        }

        override fun resizeSize(width: Int, height: Int): Builder = apply {
            super.resizeSize(width, height)
        }

        override fun resizePrecision(precisionDecider: PrecisionDecider): Builder = apply {
            super.resizePrecision(precisionDecider)
        }

        override fun resizePrecision(precision: Precision): Builder = apply {
            super.resizePrecision(precision)
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

        override fun removeTransformations(removeTransformations: List<Transformation>): Builder =
            apply {
                super.removeTransformations(removeTransformations)
            }

        override fun removeTransformations(vararg removeTransformations: Transformation): Builder =
            apply {
                super.removeTransformations(*removeTransformations)
            }

        override fun disabledBitmapPool(disabledBitmapPool: Boolean?): Builder = apply {
            super.disabledBitmapPool(disabledBitmapPool)
        }

        override fun ignoreExifOrientation(ignoreExifOrientation: Boolean?): Builder = apply {
            super.ignoreExifOrientation(ignoreExifOrientation)
        }

        override fun bitmapMemoryCachePolicy(bitmapMemoryCachePolicy: CachePolicy?): Builder =
            apply {
                super.bitmapMemoryCachePolicy(bitmapMemoryCachePolicy)
            }

        override fun disabledAnimationDrawable(disabledAnimationDrawable: Boolean?): Builder =
            apply {
                super.disabledAnimationDrawable(disabledAnimationDrawable)
            }

        override fun placeholderImage(placeholderImage: StateImage?): Builder = apply {
            super.placeholderImage(placeholderImage)
        }

        override fun placeholderImage(placeholderDrawable: Drawable?): Builder = apply {
            super.placeholderImage(placeholderDrawable)
        }

        override fun placeholderImage(placeholderDrawableResId: Int?): Builder = apply {
            super.placeholderImage(placeholderDrawableResId)
        }

        override fun errorImage(
            errorImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.errorImage(errorImage, configBlock)
        }

        override fun errorImage(
            errorDrawable: Drawable?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.errorImage(errorDrawable, configBlock)
        }

        override fun errorImage(
            errorDrawableResId: Int?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)?
        ): Builder = apply {
            super.errorImage(errorDrawableResId, configBlock)
        }

        override fun transition(transition: Factory?): Builder = apply {
            super.transition(transition)
        }

        override fun crossfadeTransition(
            durationMillis: Int,
            preferExactIntrinsicSize: Boolean
        ): Builder = apply {
            super.crossfadeTransition(durationMillis, preferExactIntrinsicSize)
        }

        override fun options(options: ImageOptions, requestFirst: Boolean): Builder = apply {
            super.options(options, requestFirst)
        }
    }

    class DownloadRequestImpl internal constructor(
        override val context: Context,
        override val uriString: String,
        override val listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?,
        override val parameters: Parameters?,
        override val depth: RequestDepth,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy,
        override val progressListener: ProgressListener<ImageRequest>?,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        @Suppress("OverridingDeprecatedMember") override val preferQualityOverSpeed: Boolean,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScale: Scale,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean,
        override val ignoreExifOrientation: Boolean,
        override val bitmapResultDiskCachePolicy: CachePolicy,
        override val target: Target?,
        override val lifecycle: Lifecycle?,
        override val disabledAnimationDrawable: Boolean,
        override val bitmapMemoryCachePolicy: CachePolicy,
        override val placeholderImage: StateImage?,
        override val errorImage: StateImage?,
        override val transition: Factory?,
        override val viewOptions: ImageOptions?,
        override val definedOptions: ImageOptions,
        override val globalOptions: ImageOptions?
    ) : BaseImageRequest(), DownloadRequest
}