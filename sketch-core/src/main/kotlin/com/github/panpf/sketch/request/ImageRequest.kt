/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.findLifecycle
import com.github.panpf.sketch.util.ifOrNull

/**
 * Build and set the [ImageRequest]
 */
fun ImageRequest(
    context: Context,
    uriString: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

/**
 * An immutable image request that contains all the required parameters,
 */
@Stable
interface ImageRequest {

    val key: String

    /** App Context */
    val context: Context

    /** The uri of the image to be loaded. */
    val uriString: String

    /**
     * The [Lifecycle] resolver for this request.
     * The request will be started when Lifecycle is in [Lifecycle.State.STARTED]
     * and canceled when Lifecycle is in [Lifecycle.State.DESTROYED].
     *
     * When [Lifecycle] is not actively set,
     * Sketch first obtains the Lifecycle at the nearest location through `view.findViewTreeLifecycleOwner()` and `LocalLifecycleOwner.current.lifecycle` APIs
     * Secondly, get the [Lifecycle] of Activity through context, and finally use [GlobalLifecycle]
     */
    val lifecycleResolver: LifecycleResolver

    /** [Target] is used to receive Drawable and draw it */
    val target: Target?

    /** [Listener] is used to receive the state and result of the request */
    val listener: Listener?

    /** [ProgressListener] is used to receive the download progress of the request */
    val progressListener: ProgressListener?

    /** User-provided ImageOptions */
    val definedOptions: ImageOptions

    /** Default ImageOptions */
    val defaultOptions: ImageOptions?

    /** The processing depth of the request. */
    val depth: Depth

    /** where does this depth come from */
    val depthFrom: String?
        get() = parameters?.value(DEPTH_FROM_KEY)

    /** A map of generic values that can be used to pass custom data to [Fetcher] and [Decoder]. */
    val parameters: Parameters?


    /**
     * Set headers for http requests
     *
     * @see com.github.panpf.sketch.http.HurlStack.getResponse
     */
    val httpHeaders: HttpHeaders?

    /**
     * Http download cache policy
     *
     * @see com.github.panpf.sketch.fetch.HttpUriFetcher
     */
    val downloadCachePolicy: CachePolicy


    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    val bitmapConfig: BitmapConfig?

    /**
     * [Bitmap]'s [ColorSpace]
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredColorSpace]
     */
    @get:RequiresApi(VERSION_CODES.O)
    val colorSpace: ColorSpace?

    /**
     * From Android N (API 24), this is ignored.  The output will always be high quality.
     *
     * In [android.os.Build.VERSION_CODES.M] and below, if
     * inPreferQualityOverSpeed is set to true, the decoder will try to
     * decode the reconstructed image to a higher quality even at the
     * expense of the decoding speed. Currently the field only affects JPEG
     * decode, in the case of which a more accurate, but slightly slower,
     * IDCT method will be used instead.
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
     */
    @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
    val preferQualityOverSpeed: Boolean

    /**
     * Lazy calculation of resize size. If resizeSize is null at runtime, size is calculated and assigned to resizeSize
     */
    val resizeSizeResolver: SizeResolver

    /**
     * Decide what Precision to use with [resizeSizeResolver] to calculate the size of the final Bitmap
     */
    val resizePrecisionDecider: PrecisionDecider

    /**
     * Which part of the original image to keep when [resizePrecisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val resizeScaleDecider: ScaleDecider

    /**
     * The list of [Transformation]s to be applied to this request
     */
    val transformations: List<Transformation>?

    /**
     * Disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
     */
    val disallowReuseBitmap: Boolean

    /**
     * Ignore Orientation property in file Exif info
     *
     * @see com.github.panpf.sketch.decode.internal.appliedExifOrientation
     */
    val ignoreExifOrientation: Boolean

    /**
     * Disk caching policy for Bitmaps affected by [resizeSizeResolver] or [transformations]
     *
     * @see com.github.panpf.sketch.decode.internal.ResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy


    /**
     * Placeholder image when loading
     */
    val placeholder: StateImage?

    /**
     * Image to display when uri is empty
     */
    val uriEmpty: StateImage?

    /**
     * Image to display when loading fails
     */
    val error: ErrorStateImage?

    /**
     * How the current image and the new image transition
     */
    val transitionFactory: Transition.Factory?

    /**
     * Disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
     */
    val disallowAnimatedImage: Boolean

    /**
     * Wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSizeResolver]
     */
    val resizeApplyToDrawable: Boolean

    /**
     * Bitmap memory caching policy
     *
     * @see com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
     */
    val memoryCachePolicy: CachePolicy


    /** Components that are only valid for the current request */
    val componentRegistry: ComponentRegistry?

    /**
     * Create a new [ImageRequest.Builder] based on the current [ImageRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [ImageRequest] based on the current [ImageRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): ImageRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder {

        private val context: Context
        private val uriString: String

        private var listeners: MutableSet<Listener>? = null
        private var listener: Listener? = null
        private var providerListener: Listener? = null
        private var progressListeners: MutableSet<ProgressListener>? = null
        private var progressListener: ProgressListener? = null
        private var providerProgressListener: ProgressListener? = null
        private var target: Target? = null
        private var lifecycleResolver: LifecycleResolver? = null
        private var defaultOptions: ImageOptions? = null
        private var viewTargetOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder

        constructor(context: Context, uriString: String?) {
            this.context = context
            this.uriString = uriString.orEmpty()
            this.definedOptionsBuilder = ImageOptions.Builder()
        }

        constructor(request: ImageRequest) {
            this.context = request.context
            this.uriString = request.uriString
            val oldListener = request.listener
            if (oldListener is CombinedListener) {
                this.listener = oldListener.fromBuilderListener
                this.listeners = oldListener.fromBuilderListeners?.toMutableSet()
                this.providerListener = oldListener.fromProviderListener
            } else {
                this.listener = oldListener
                this.listeners = null
                this.providerListener = null
            }
            val oldProgressListener = request.progressListener
            if (oldProgressListener is CombinedProgressListener) {
                this.progressListener = oldProgressListener.fromBuilderProgressListener
                this.progressListeners =
                    oldProgressListener.fromBuilderProgressListeners?.toMutableSet()
                this.providerProgressListener = oldProgressListener.fromProviderProgressListener
            } else {
                this.progressListener = oldProgressListener
                this.progressListeners = null
                this.providerProgressListener = null
            }
            this.target = request.target
            this.lifecycleResolver = request.lifecycleResolver
            this.defaultOptions = request.defaultOptions
            this.definedOptionsBuilder = request.definedOptions.newBuilder()
        }

        /**
         * Set the [Listener]
         */
        fun listener(
            listener: Listener?
        ): Builder = apply {
            this.listener = listener
        }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: ImageRequest) -> Unit = {},
            crossinline onCancel: (request: ImageRequest) -> Unit = {},
            crossinline onError: (request: ImageRequest, result: ImageResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: ImageRequest, result: ImageResult.Success) -> Unit = { _, _ -> }
        ): Builder = listener(object :
            Listener {
            override fun onStart(request: ImageRequest) = onStart(request)
            override fun onCancel(request: ImageRequest) = onCancel(request)
            override fun onError(
                request: ImageRequest, error: ImageResult.Error
            ) = onError(request, error)

            override fun onSuccess(
                request: ImageRequest, result: ImageResult.Success
            ) = onSuccess(request, result)
        })

        /**
         * Add the [Listener] to set
         */
        fun addListener(
            listener: Listener
        ): Builder = apply {
            val listeners = listeners
                ?: mutableSetOf<Listener>().apply {
                    this@Builder.listeners = this
                }
            listeners.add(listener)
        }

        /**
         * Add the [Listener] to set
         */
        inline fun addListener(
            crossinline onStart: (request: ImageRequest) -> Unit = {},
            crossinline onCancel: (request: ImageRequest) -> Unit = {},
            crossinline onError: (request: ImageRequest, result: ImageResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: ImageRequest, result: ImageResult.Success) -> Unit = { _, _ -> }
        ): Builder = addListener(object :
            Listener {
            override fun onStart(request: ImageRequest) = onStart(request)
            override fun onCancel(request: ImageRequest) = onCancel(request)
            override fun onError(
                request: ImageRequest, error: ImageResult.Error
            ) = onError(request, error)

            override fun onSuccess(
                request: ImageRequest, result: ImageResult.Success
            ) = onSuccess(request, result)
        })

        /**
         * Remove the [Listener] from set
         */
        fun removeListener(
            listener: Listener
        ): Builder = apply {
            listeners?.remove(listener)
        }

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener?
        ): Builder = apply {
            this.progressListener = progressListener
        }

        /**
         * Add the [ProgressListener] to set
         */
        fun addProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            val progressListeners =
                progressListeners ?: mutableSetOf<ProgressListener>().apply {
                    this@Builder.progressListeners = this
                }
            progressListeners.add(progressListener)
        }

        /**
         * Remove the [ProgressListener] from set
         */
        fun removeProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            progressListeners?.remove(progressListener)
        }

        /**
         * Set the [Lifecycle] for this request.
         *
         * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            this.lifecycleResolver = if (lifecycle != null) LifecycleResolver(lifecycle) else null
        }

        /**
         * Set the [LifecycleResolver] for this request.
         *
         * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycleResolver: LifecycleResolver?): Builder = apply {
            this.lifecycleResolver = lifecycleResolver
        }

        /**
         * Set the [Target].
         */
        fun target(target: Target?): Builder = apply {
            this.target = target
            this.viewTargetOptions = target.asOrNull<ViewTarget<*>>()
                ?.view.asOrNull<ImageOptionsProvider>()
                ?.displayImageOptions
        }


        /**
         * Set the requested depth
         */
        fun depth(depth: Depth?, depthFrom: String? = null): Builder = apply {
            definedOptionsBuilder.depth(depth, depthFrom)
        }

        /**
         * Bulk set parameters for this request
         */
        fun parameters(parameters: Parameters?): Builder = apply {
            definedOptionsBuilder.parameters(parameters)
        }

        /**
         * Set a parameter for this request.
         */
        fun setParameter(
            key: String, value: Any?, cacheKey: String? = value?.toString()
        ): Builder = apply {
            definedOptionsBuilder.setParameter(key, value, cacheKey)
        }

        /**
         * Remove a parameter from this request.
         */
        fun removeParameter(key: String): Builder = apply {
            definedOptionsBuilder.removeParameter(key)
        }


        /**
         * Bulk set headers for any network request for this request
         */
        fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            definedOptionsBuilder.httpHeaders(httpHeaders)
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        fun addHttpHeader(name: String, value: String): Builder = apply {
            definedOptionsBuilder.addHttpHeader(name, value)
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        fun setHttpHeader(name: String, value: String): Builder = apply {
            definedOptionsBuilder.setHttpHeader(name, value)
        }

        /**
         * Remove all network headers with the key [name].
         */
        fun removeHttpHeader(name: String): Builder = apply {
            definedOptionsBuilder.removeHttpHeader(name)
        }

        /**
         * Set http download cache policy
         */
        fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.downloadCachePolicy(cachePolicy)
        }


        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         */
        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         */
        fun bitmapConfig(bitmapConfig: Bitmap.Config): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set preferred [Bitmap]'s [ColorSpace]
         */
        @RequiresApi(VERSION_CODES.O)
        fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
            definedOptionsBuilder.colorSpace(colorSpace)
        }

        /**
         * From Android N (API 24), this is ignored.  The output will always be high quality.
         *
         * In [android.os.Build.VERSION_CODES.M] and below, if
         * inPreferQualityOverSpeed is set to true, the decoder will try to
         * decode the reconstructed image to a higher quality even at the
         * expense of the decoding speed. Currently the field only affects JPEG
         * decode, in the case of which a more accurate, but slightly slower,
         * IDCT method will be used instead.
         *
         * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): Builder =
            apply {
                @Suppress("DEPRECATION")
                definedOptionsBuilder.preferQualityOverSpeed(inPreferQualityOverSpeed)
            }

        /**
         * Set how to resize image
         *
         * @param size Expected Bitmap size Resolver
         * @param precision precision of size, default is [Precision.LESS_PIXELS]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        fun resize(
            size: SizeResolver?,
            precision: PrecisionDecider? = null,
            scale: ScaleDecider? = null
        ): Builder = apply {
            definedOptionsBuilder.resize(size, precision, scale)
        }

        /**
         * Set how to resize image
         *
         * @param size Expected Bitmap size
         * @param precision precision of size, default is [Precision.LESS_PIXELS]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        fun resize(
            size: Size,
            precision: Precision? = null,
            scale: Scale? = null
        ): Builder = apply {
            definedOptionsBuilder.resize(size, precision, scale)
        }

        /**
         * Set how to resize image
         *
         * @param width Expected Bitmap width
         * @param height Expected Bitmap height
         * @param precision precision of size, default is [Precision.LESS_PIXELS]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        fun resize(
            @Px width: Int,
            @Px height: Int,
            precision: Precision? = null,
            scale: Scale? = null
        ): Builder = apply {
            definedOptionsBuilder.resize(width, height, precision, scale)
        }

        /**
         * Set the [SizeResolver] to lazy resolve the requested size.
         */
        fun resizeSize(sizeResolver: SizeResolver?): Builder = apply {
            definedOptionsBuilder.resizeSize(sizeResolver)
        }

        /**
         * Set the resize size
         */
        fun resizeSize(size: Size): Builder = apply {
            definedOptionsBuilder.resizeSize(size)
        }

        /**
         * Set the resize size
         */
        fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            definedOptionsBuilder.resizeSize(width, height)
        }

        /**
         * Set the resize precision
         */
        fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            definedOptionsBuilder.resizePrecision(precisionDecider)
        }

        /**
         * Set the resize precision
         */
        fun resizePrecision(precision: Precision): Builder = apply {
            definedOptionsBuilder.resizePrecision(precision)
        }

        /**
         * Set the resize scale
         */
        fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            definedOptionsBuilder.resizeScale(scaleDecider)
        }

        /**
         * Set the resize scale
         */
        fun resizeScale(scale: Scale): Builder = apply {
            definedOptionsBuilder.resizeScale(scale)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        fun transformations(transformations: List<Transformation>?): Builder = apply {
            definedOptionsBuilder.transformations(transformations)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        fun transformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.transformations(transformations.toList())
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        fun addTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations)
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        fun addTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations.toList())
        }

        /**
         * Bulk remove from current [Transformation] list
         */
        fun removeTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations)
        }

        /**
         * Bulk remove from current [Transformation] list
         */
        fun removeTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations.toList())
        }

        /**
         * Set disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
         */
        fun disallowReuseBitmap(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowReuseBitmap(disabled)
        }

        /**
         * Set ignore Orientation property in file Exif info
         */
        fun ignoreExifOrientation(ignore: Boolean? = true): Builder = apply {
            definedOptionsBuilder.ignoreExifOrientation(ignore)
        }

        /**
         * Set disk caching policy for Bitmaps affected by [resizeSize] or [transformations]
         */
        fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.resultCachePolicy(cachePolicy)
        }


        /**
         * Set placeholder image when loading
         */
        fun placeholder(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.placeholder(stateImage)
        }

        /**
         * Set Drawable placeholder image when loading
         */
        fun placeholder(drawable: Drawable): Builder = apply {
            definedOptionsBuilder.placeholder(drawable)
        }

        /**
         * Set Drawable res placeholder image when loading
         */
        fun placeholder(@DrawableRes drawableResId: Int): Builder = apply {
            definedOptionsBuilder.placeholder(drawableResId)
        }

        /**
         * Set placeholder image when uri is empty
         */
        fun uriEmpty(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.uriEmpty(stateImage)
        }

        /**
         * Set Drawable placeholder image when uri is empty
         */
        fun uriEmpty(drawable: Drawable): Builder = apply {
            definedOptionsBuilder.uriEmpty(drawable)
        }

        /**
         * Set Drawable res placeholder image when uri is empty
         */
        fun uriEmpty(@DrawableRes drawableResId: Int): Builder = apply {
            definedOptionsBuilder.uriEmpty(drawableResId)
        }

        /**
         * Set image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultStateImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultStateImage, configBlock)
        }

        /**
         * Set Drawable image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultDrawable: Drawable, configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultDrawable, configBlock)
        }

        /**
         * Set Drawable res image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultDrawableResId: Int, configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultDrawableResId, configBlock)
        }

        /**
         * Set Drawable res image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(configBlock)
        }

        /**
         * Set the transition between the current image and the new image
         */
        fun transitionFactory(transitionFactory: Transition.Factory?): Builder = apply {
            definedOptionsBuilder.transitionFactory(transitionFactory)
        }

        /**
         * Sets the transition that crossfade
         */
        fun crossfade(
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            fadeStart: Boolean = true,
            preferExactIntrinsicSize: Boolean = false,
            alwaysUse: Boolean = false,
        ): Builder = apply {
            definedOptionsBuilder.crossfade(
                durationMillis,
                fadeStart,
                preferExactIntrinsicSize,
                alwaysUse
            )
        }

        /**
         * Set disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
         */
        fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowAnimatedImage(disabled)
        }

        /**
         * Set wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSize]
         */
        fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean? = true): Builder = apply {
            definedOptionsBuilder.resizeApplyToDrawable(resizeApplyToDrawable)
        }

        /**
         * Set bitmap memory caching policy
         */
        fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.memoryCachePolicy(cachePolicy)
        }


        /**
         * Merge the specified [ImageOptions] into the current [Builder]. Currently [Builder] takes precedence
         */
        fun merge(options: ImageOptions?): Builder = apply {
            definedOptionsBuilder.merge(options)
        }

        /**
         * Set a final [ImageOptions] to complement properties not set
         */
        fun default(options: ImageOptions?): Builder = apply {
            this.defaultOptions = options
        }


        /**
         * Set the [ComponentRegistry]
         */
        fun components(components: ComponentRegistry?): Builder = apply {
            definedOptionsBuilder.components(components)
        }

        /**
         * Build and set the [ComponentRegistry]
         */
        fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.components(configBlock)
        }


        @Suppress("DEPRECATION")
        @SuppressLint("NewApi")
        fun build(): ImageRequest {
            val listener = combinationListener()
            val progressListener = combinationProgressListener()
            val lifecycleResolver =
                lifecycleResolver ?: DefaultLifecycleResolver(resolveLifecycleResolver())
            val definedOptions = definedOptionsBuilder.merge(viewTargetOptions).build()
            val finalOptions = definedOptions.merged(defaultOptions)
            val depth = finalOptions.depth ?: Depth.NETWORK
            val parameters = finalOptions.parameters
            val httpHeaders = finalOptions.httpHeaders
            val downloadCachePolicy = finalOptions.downloadCachePolicy ?: CachePolicy.ENABLED
            val resultCachePolicy = finalOptions.resultCachePolicy ?: CachePolicy.ENABLED
            val bitmapConfig = finalOptions.bitmapConfig
            val colorSpace = ifOrNull(VERSION.SDK_INT >= VERSION_CODES.O) {
                finalOptions.colorSpace
            }
            val preferQualityOverSpeed = finalOptions.preferQualityOverSpeed ?: false
            val resizeSizeResolver = finalOptions.resizeSizeResolver
                ?: resolveResizeSizeResolver()
            val resizePrecisionDecider = finalOptions.resizePrecisionDecider
                ?: PrecisionDecider(Precision.LESS_PIXELS)
            val resizeScaleDecider =
                finalOptions.resizeScaleDecider ?: ScaleDecider(resolveResizeScale())
            val transformations = finalOptions.transformations
            val disallowReuseBitmap = finalOptions.disallowReuseBitmap ?: false
            val ignoreExifOrientation = finalOptions.ignoreExifOrientation ?: false
            val placeholder = finalOptions.placeholder
            val uriEmpty = finalOptions.uriEmpty
            val error = finalOptions.error
            val transitionFactory = finalOptions.transitionFactory
            val disallowAnimatedImage = finalOptions.disallowAnimatedImage ?: false
            val resizeApplyToDrawable = finalOptions.resizeApplyToDrawable ?: false
            val memoryCachePolicy = finalOptions.memoryCachePolicy ?: CachePolicy.ENABLED
            val componentRegistry = finalOptions.componentRegistry

            return ImageRequestImpl(
                context = context,
                uriString = uriString,
                listener = listener,
                progressListener = progressListener,
                target = target,
                lifecycleResolver = lifecycleResolver,
                defaultOptions = defaultOptions,
                definedOptions = definedOptions,
                depth = depth,
                parameters = parameters,
                httpHeaders = httpHeaders,
                downloadCachePolicy = downloadCachePolicy,
                resultCachePolicy = resultCachePolicy,
                bitmapConfig = bitmapConfig,
                colorSpace = colorSpace,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resizeSizeResolver = resizeSizeResolver,
                resizePrecisionDecider = resizePrecisionDecider,
                resizeScaleDecider = resizeScaleDecider,
                transformations = transformations,
                disallowReuseBitmap = disallowReuseBitmap,
                ignoreExifOrientation = ignoreExifOrientation,
                placeholder = placeholder,
                uriEmpty = uriEmpty,
                error = error,
                transitionFactory = transitionFactory,
                disallowAnimatedImage = disallowAnimatedImage,
                resizeApplyToDrawable = resizeApplyToDrawable,
                memoryCachePolicy = memoryCachePolicy,
                componentRegistry = componentRegistry,
            )
        }

        private fun resolveResizeSizeResolver(): SizeResolver {
            val target = target
            return if (target is ViewTarget<*>) {
                target.view?.let { ViewSizeResolver(it) } ?: DisplaySizeResolver(context)
            } else {
                DisplaySizeResolver(context)
            }
        }

        private fun resolveLifecycleResolver(): LifecycleResolver {
            val view = target.asOrNull<ViewTarget<*>>()?.view
            if (view != null) {
                return ViewLifecycleResolver(view)
            }
            val lifecycleFromContext = context.findLifecycle()
            if (lifecycleFromContext != null) {
                return FixedLifecycleResolver(lifecycleFromContext)
            }
            return FixedLifecycleResolver(GlobalLifecycle)
        }

        private fun resolveResizeScale(): Scale =
            target.asOrNull<ViewTarget<*>>()
                ?.view?.asOrNull<ImageView>()
                ?.scaleType?.let {
                    when (it) {
                        ScaleType.FIT_START -> Scale.START_CROP
                        ScaleType.FIT_CENTER -> Scale.CENTER_CROP
                        ScaleType.FIT_END -> Scale.END_CROP
                        ScaleType.CENTER_INSIDE -> Scale.CENTER_CROP
                        ScaleType.CENTER -> Scale.CENTER_CROP
                        ScaleType.CENTER_CROP -> Scale.CENTER_CROP
                        else -> Scale.FILL
                    }
                } ?: Scale.CENTER_CROP

        private fun combinationListener(): Listener? {
            val target = target
            val listener = listener
            val listeners = listeners?.takeIf { it.isNotEmpty() }?.toList()
            val providerListener = providerListener
                ?: target.asOrNull<ViewTarget<*>>()
                    ?.view?.asOrNull<ListenerProvider>()
                    ?.getListener()
            return if (listeners != null || providerListener != null) {
                CombinedListener(
                    fromProviderListener = providerListener,
                    fromBuilderListener = listener,
                    fromBuilderListeners = listeners
                )
            } else {
                listener
            }
        }

        private fun combinationProgressListener(): ProgressListener? {
            val target = target
            val progressListener = progressListener
            val progressListeners = progressListeners?.takeIf { it.isNotEmpty() }?.toList()
            val providerProgressListener = providerProgressListener
                ?: target.asOrNull<ViewTarget<*>>()
                    ?.view?.asOrNull<ListenerProvider>()
                    ?.getProgressListener()
            return if (progressListeners != null || providerProgressListener != null) {
                CombinedProgressListener(
                    fromProviderProgressListener = providerProgressListener,
                    fromBuilderProgressListener = progressListener,
                    fromBuilderProgressListeners = progressListeners
                )
            } else {
                progressListener
            }
        }
    }

    data class ImageRequestImpl internal constructor(
        override val context: Context,
        override val uriString: String,
        override val listener: Listener?,
        override val progressListener: ProgressListener?,
        override val target: Target?,
        override val lifecycleResolver: LifecycleResolver,
        override val definedOptions: ImageOptions,
        override val defaultOptions: ImageOptions?,
        override val depth: Depth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val downloadCachePolicy: CachePolicy,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean,
        override val resizeSizeResolver: SizeResolver,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScaleDecider: ScaleDecider,
        override val transformations: List<Transformation>?,
        override val disallowReuseBitmap: Boolean,
        override val ignoreExifOrientation: Boolean,
        override val resultCachePolicy: CachePolicy,
        override val placeholder: StateImage?,
        override val uriEmpty: StateImage?,
        override val error: ErrorStateImage?,
        override val transitionFactory: Transition.Factory?,
        override val disallowAnimatedImage: Boolean,
        override val resizeApplyToDrawable: Boolean,
        override val memoryCachePolicy: CachePolicy,
        override val componentRegistry: ComponentRegistry?,
    ) : ImageRequest {

        override val key: String by lazy { newKey() }
    }
}