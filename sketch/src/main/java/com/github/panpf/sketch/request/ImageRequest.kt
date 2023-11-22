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
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
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
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.findLifecycle
import com.github.panpf.sketch.util.ifOrNull

/**
 * An immutable image request that contains all the required parameters,
 * you need to use its three concrete implementations [DisplayRequest], [LoadRequest], [DownloadRequest]
 */
interface ImageRequest {

    val context: Context
    val uriString: String
    val lifecycleResolver: LifecycleResolver
    val target: Target?
    val listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
    val progressListener: ProgressListener<ImageRequest>?
    val definedOptions: ImageOptions
    val defaultOptions: ImageOptions?

    /** The processing depth of the request. */
    val depth: Depth

    /** where does this depth come from */
    val depthFrom: String?
        get() = parameters?.value(DEPTH_FROM_KEY)

    /** A map of generic values that can be used to pass custom data to [Fetcher] and [BitmapDecoder] and [DrawableDecoder]. */
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
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val bitmapConfig: BitmapConfig?

    /**
     * [Bitmap]'s [ColorSpace]
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredColorSpace]
     *
     * Only works on [LoadRequest] and [DisplayRequest]
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
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
    val preferQualityOverSpeed: Boolean

    /**
     * Lazy calculation of resize size. If resizeSize is null at runtime, size is calculated and assigned to resizeSize
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val resizeSizeResolver: SizeResolver

    /**
     * Decide what Precision to use with [resizeSizeResolver] to calculate the size of the final Bitmap
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val resizePrecisionDecider: PrecisionDecider

    /**
     * Which part of the original image to keep when [resizePrecisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val resizeScaleDecider: ScaleDecider

    /**
     * The list of [Transformation]s to be applied to this request
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val transformations: List<Transformation>?

    /**
     * Disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val disallowReuseBitmap: Boolean

    /**
     * Ignore Orientation property in file Exif info
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     *
     * @see com.github.panpf.sketch.decode.internal.appliedExifOrientation
     */
    val ignoreExifOrientation: Boolean

    /**
     * Disk caching policy for Bitmaps affected by [resizeSizeResolver] or [transformations]
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     *
     * @see com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy


    /**
     * Placeholder image when loading
     *
     * Only works on [DisplayRequest]
     */
    val placeholder: StateImage?

    /**
     * Image to display when uri is empty
     *
     * Only works on [DisplayRequest]
     */
    val uriEmpty: StateImage?

    /**
     * Image to display when loading fails
     *
     * Only works on [DisplayRequest]
     */
    val error: ErrorStateImage?

    /**
     * How the current image and the new image transition
     *
     * Only works on [DisplayRequest]
     */
    val transitionFactory: Transition.Factory?

    /**
     * Disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
     *
     * Only works on [DisplayRequest]
     */
    val disallowAnimatedImage: Boolean

    /**
     * Wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSizeResolver]
     *
     * Only works on [DisplayRequest]
     */
    val resizeApplyToDrawable: Boolean

    /**
     * Bitmap memory caching policy
     *
     * Only works on [DisplayRequest]
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
    ): Builder

    /**
     * Create a new [ImageRequest] based on the current [ImageRequest].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): ImageRequest

    abstract class Builder {

        private val context: Context
        private val uriString: String
        private var listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? = null
        private var providerListener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? =
            null
        private var progressListener: ProgressListener<ImageRequest>? = null
        private var providerProgressListener: ProgressListener<ImageRequest>? = null
        private var target: Target? = null
        private var lifecycleResolver: LifecycleResolver? = null
        private var defaultOptions: ImageOptions? = null
        private var viewTargetOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder

        protected constructor(context: Context, uriString: String?) {
            this.context = context
            this.uriString = uriString.orEmpty()
            this.definedOptionsBuilder = ImageOptions.Builder()
        }

        protected constructor(request: ImageRequest) {
            this.context = request.context
            this.uriString = request.uriString
            val oldListener = request.listener
            if (oldListener is CombinedListener<ImageRequest, ImageResult.Success, ImageResult.Error>) {
                this.listener = oldListener.fromBuilderListener
                this.providerListener = oldListener.fromProviderListener
            } else {
                this.listener = oldListener
                this.providerListener = null
            }
            val oldProgressListener = request.progressListener
            if (oldProgressListener is CombinedProgressListener<ImageRequest>) {
                this.progressListener = oldProgressListener.fromBuilderProgressListener
                this.providerProgressListener = oldProgressListener.fromProviderProgressListener
            } else {
                this.progressListener = oldProgressListener
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
        protected fun listener(listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?): Builder =
            apply {
                this.listener = listener
            }

        /**
         * Set the [ProgressListener]
         */
        protected fun progressListener(
            progressListener: ProgressListener<ImageRequest>?
        ): Builder = apply {
            this.progressListener = progressListener
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
        open fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
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
        open fun lifecycle(lifecycleResolver: LifecycleResolver?): Builder = apply {
            this.lifecycleResolver = lifecycleResolver
        }

        /**
         * Set the [Target].
         */
        protected fun target(target: Target?): Builder = apply {
            this.target = target
            this.viewTargetOptions = target.asOrNull<ViewDisplayTarget<*>>()
                ?.view.asOrNull<ImageOptionsProvider>()
                ?.displayImageOptions
        }


        /**
         * Set the requested depth
         */
        open fun depth(depth: Depth?, depthFrom: String? = null): Builder = apply {
            definedOptionsBuilder.depth(depth, depthFrom)
        }

        /**
         * Bulk set parameters for this request
         */
        open fun parameters(parameters: Parameters?): Builder = apply {
            definedOptionsBuilder.parameters(parameters)
        }

        /**
         * Set a parameter for this request.
         */
        open fun setParameter(
            key: String, value: Any?, cacheKey: String? = value?.toString()
        ): Builder = apply {
            definedOptionsBuilder.setParameter(key, value, cacheKey)
        }

        /**
         * Remove a parameter from this request.
         */
        open fun removeParameter(key: String): Builder = apply {
            definedOptionsBuilder.removeParameter(key)
        }


        /**
         * Bulk set headers for any network request for this request
         */
        open fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            definedOptionsBuilder.httpHeaders(httpHeaders)
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        open fun addHttpHeader(name: String, value: String): Builder = apply {
            definedOptionsBuilder.addHttpHeader(name, value)
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        open fun setHttpHeader(name: String, value: String): Builder = apply {
            definedOptionsBuilder.setHttpHeader(name, value)
        }

        /**
         * Remove all network headers with the key [name].
         */
        open fun removeHttpHeader(name: String): Builder = apply {
            definedOptionsBuilder.removeHttpHeader(name)
        }

        /**
         * Set http download cache policy
         */
        open fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.downloadCachePolicy(cachePolicy)
        }


        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun bitmapConfig(bitmapConfig: Bitmap.Config): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set preferred [Bitmap]'s [ColorSpace]
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        @RequiresApi(VERSION_CODES.O)
        open fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
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
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        open fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): Builder =
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
        open fun resize(
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
        open fun resize(
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
        open fun resize(
            @Px width: Int,
            @Px height: Int,
            precision: Precision? = null,
            scale: Scale? = null
        ): Builder = apply {
            definedOptionsBuilder.resize(width, height, precision, scale)
        }

        /**
         * Set the [SizeResolver] to lazy resolve the requested size.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizeSize(sizeResolver: SizeResolver?): Builder = apply {
            definedOptionsBuilder.resizeSize(sizeResolver)
        }

        /**
         * Set the resize size
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizeSize(size: Size): Builder = apply {
            definedOptionsBuilder.resizeSize(size)
        }

        /**
         * Set the resize size
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            definedOptionsBuilder.resizeSize(width, height)
        }

        /**
         * Set the resize precision
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            definedOptionsBuilder.resizePrecision(precisionDecider)
        }

        /**
         * Set the resize precision
         */
        open fun resizePrecision(precision: Precision): Builder = apply {
            definedOptionsBuilder.resizePrecision(precision)
        }

        /**
         * Set the resize scale
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            definedOptionsBuilder.resizeScale(scaleDecider)
        }

        /**
         * Set the resize scale
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resizeScale(scale: Scale): Builder = apply {
            definedOptionsBuilder.resizeScale(scale)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun transformations(transformations: List<Transformation>?): Builder = apply {
            definedOptionsBuilder.transformations(transformations)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun transformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.transformations(transformations.toList())
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun addTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations)
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun addTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations.toList())
        }

        /**
         * Bulk remove from current [Transformation] list
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun removeTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations)
        }

        /**
         * Bulk remove from current [Transformation] list
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun removeTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations.toList())
        }

        /**
         * Set disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun disallowReuseBitmap(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowReuseBitmap(disabled)
        }

        /**
         * Set ignore Orientation property in file Exif info
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun ignoreExifOrientation(ignore: Boolean? = true): Builder = apply {
            definedOptionsBuilder.ignoreExifOrientation(ignore)
        }

        /**
         * Set disk caching policy for Bitmaps affected by [resizeSize] or [transformations]
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        open fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.resultCachePolicy(cachePolicy)
        }


        /**
         * Set placeholder image when loading
         *
         * Only works on [DisplayRequest]
         */
        open fun placeholder(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.placeholder(stateImage)
        }

        /**
         * Set Drawable placeholder image when loading
         *
         * Only works on [DisplayRequest]
         */
        open fun placeholder(drawable: Drawable): Builder = apply {
            definedOptionsBuilder.placeholder(drawable)
        }

        /**
         * Set Drawable res placeholder image when loading
         *
         * Only works on [DisplayRequest]
         */
        open fun placeholder(@DrawableRes drawableResId: Int): Builder = apply {
            definedOptionsBuilder.placeholder(drawableResId)
        }

        /**
         * Set placeholder image when uri is empty
         *
         * Only works on [DisplayRequest]
         */
        open fun uriEmpty(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.uriEmpty(stateImage)
        }

        /**
         * Set Drawable placeholder image when uri is empty
         *
         * Only works on [DisplayRequest]
         */
        open fun uriEmpty(drawable: Drawable): Builder = apply {
            definedOptionsBuilder.uriEmpty(drawable)
        }

        /**
         * Set Drawable res placeholder image when uri is empty
         *
         * Only works on [DisplayRequest]
         */
        open fun uriEmpty(@DrawableRes drawableResId: Int): Builder = apply {
            definedOptionsBuilder.uriEmpty(drawableResId)
        }

        /**
         * Set image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         *
         * Only works on [DisplayRequest]
         */
        open fun error(
            defaultStateImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultStateImage, configBlock)
        }

        /**
         * Set Drawable image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         *
         * Only works on [DisplayRequest]
         */
        open fun error(
            defaultDrawable: Drawable, configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultDrawable, configBlock)
        }

        /**
         * Set Drawable res image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         *
         * Only works on [DisplayRequest]
         */
        open fun error(
            defaultDrawableResId: Int, configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultDrawableResId, configBlock)
        }

        /**
         * Set Drawable res image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         *
         * Only works on [DisplayRequest]
         */
        open fun error(
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(configBlock)
        }

        /**
         * Set the transition between the current image and the new image
         *
         * Only works on [DisplayRequest]
         */
        open fun transitionFactory(transitionFactory: Transition.Factory?): Builder = apply {
            definedOptionsBuilder.transitionFactory(transitionFactory)
        }

        /**
         * Sets the transition that crossfade
         *
         * Only works on [DisplayRequest]
         */
        open fun crossfade(
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
         *
         * Only works on [DisplayRequest]
         */
        open fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowAnimatedImage(disabled)
        }

        /**
         * Set wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSize]
         *
         * Only works on [DisplayRequest]
         */
        open fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean? = true): Builder = apply {
            definedOptionsBuilder.resizeApplyToDrawable(resizeApplyToDrawable)
        }

        /**
         * Set bitmap memory caching policy
         *
         * Only works on [DisplayRequest]
         */
        open fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.memoryCachePolicy(cachePolicy)
        }


        /**
         * Merge the specified [ImageOptions] into the current [Builder]. Currently [Builder] takes precedence
         *
         * Only works on [DisplayRequest]
         */
        open fun merge(options: ImageOptions?): Builder = apply {
            definedOptionsBuilder.merge(options)
        }

        /**
         * Set a final [ImageOptions] to complement properties not set
         *
         * Only works on [DisplayRequest]
         */
        open fun default(options: ImageOptions?): Builder = apply {
            this.defaultOptions = options
        }


        /**
         * Set the [ComponentRegistry]
         */
        open fun components(components: ComponentRegistry?): Builder = apply {
            definedOptionsBuilder.components(components)
        }

        /**
         * Build and set the [ComponentRegistry]
         */
        open fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.components(configBlock)
        }


        @Suppress("DEPRECATION")
        @SuppressLint("NewApi")
        open fun build(): ImageRequest {
            val listener = combinationListener()
            val progressListener = combinationProgressListener()
            val lifecycleResolver = lifecycleResolver ?: DefaultLifecycleResolver(resolveLifecycleResolver())
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
                ?: FixedPrecisionDecider(Precision.LESS_PIXELS)
            val resizeScaleDecider =
                finalOptions.resizeScaleDecider ?: FixedScaleDecider(resolveResizeScale())
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

            return when (this@Builder) {
                is DisplayRequest.Builder -> {
                    DisplayRequest.DisplayRequestImpl(
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

                is LoadRequest.Builder -> {
                    LoadRequest.LoadRequestImpl(
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

                is DownloadRequest.Builder -> {
                    DownloadRequest.DownloadRequestImpl(
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

                else -> throw UnsupportedOperationException("Unsupported ImageRequest.Builder: ${this@Builder::class.java}")
            }
        }

        private fun resolveResizeSizeResolver(): SizeResolver {
            val target = target
            return if (target is ViewDisplayTarget<*>) {
                target.view?.let { ViewSizeResolver(it) } ?: DisplaySizeResolver(context)
            } else {
                DisplaySizeResolver(context)
            }
        }

        private fun resolveLifecycleResolver(): LifecycleResolver {
            val view = target.asOrNull<ViewDisplayTarget<*>>()?.view
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
            target.asOrNull<ViewDisplayTarget<*>>()
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

        @Suppress("UNCHECKED_CAST")
        private fun combinationListener(): Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? {
            val target = target
            val listener = listener
            val providerListener = providerListener
                ?: target.asOrNull<ViewDisplayTarget<*>>()
                    ?.view?.asOrNull<DisplayListenerProvider>()
                    ?.getDisplayListener() as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
            return if (providerListener != null) {
                CombinedListener(providerListener, listener)
            } else {
                listener
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun combinationProgressListener(): ProgressListener<ImageRequest>? {
            val target = target
            val progressListener = progressListener
            val providerProgressListener = providerProgressListener
                ?: target.asOrNull<ViewDisplayTarget<*>>()
                    ?.view?.asOrNull<DisplayListenerProvider>()
                    ?.getDisplayProgressListener() as ProgressListener<ImageRequest>?
            return if (providerProgressListener != null) {
                CombinedProgressListener(providerProgressListener, progressListener)
            } else {
                progressListener
            }
        }
    }
}