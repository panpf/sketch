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

import androidx.annotation.Px
import androidx.compose.runtime.Stable
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.internal.RequestOptions
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.defaultSizeResolver
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.TargetLifecycle
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Crossfade
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size

/**
 * Build and set the [ImageRequest]
 */
fun ImageRequest(
    context: PlatformContext,
    uriString: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

/**
 * Sets the transition that crossfade
 */
fun ImageRequest.Builder.crossfade(
    durationMillis: Int = Crossfade.DEFAULT_DURATION_MILLIS,
    fadeStart: Boolean = Crossfade.DEFAULT_FADE_START,
    preferExactIntrinsicSize: Boolean = Crossfade.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
    alwaysUse: Boolean = Crossfade.DEFAULT_ALWAYS_USE,
): ImageRequest.Builder = apply {
    setParameter(
        key = CROSSFADE_KEY,
        value = Crossfade(
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize,
            alwaysUse = alwaysUse
        ),
        cacheKey = null
    )
}

fun ImageRequest.Builder.removeCrossfade(): ImageRequest.Builder = apply {
    removeParameter(CROSSFADE_KEY)
}

val ImageRequest.crossfade: Crossfade?
    get() = parameters?.value<Crossfade>(CROSSFADE_KEY)

/**
 * An immutable image request that contains all the required parameters,
 */
@Stable
interface ImageRequest {

    /**
     * The unique identifier for this request.
     */
    val key: String

    /** App Context */
    val context: PlatformContext

    /** The uri of the image to be loaded. */
    val uriString: String

    /**
     * The [TargetLifecycle] resolver for this request.
     * The request will be started when TargetLifecycle is in [TargetLifecycle.State.STARTED]
     * and canceled when TargetLifecycle is in [TargetLifecycle.State.DESTROYED].
     *
     * When [TargetLifecycle] is not actively set,
     * Sketch first obtains the TargetLifecycle at the nearest location through `view.findViewTreeLifecycleOwner()` and `LocalLifecycleOwner.current.lifecycle` APIs
     * Secondly, get the [TargetLifecycle] of Activity through context, and finally use [GlobalTargetLifecycle]
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

    val definedRequestOptions: RequestOptions


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
     * Lazy calculation of resize size. If size is null at runtime, size is calculated and assigned to size
     */
    val sizeResolver: SizeResolver

    /**
     * Decide what Precision to use with [sizeResolver] to calculate the size of the final Bitmap
     */
    val precisionDecider: PrecisionDecider

    /**
     * Which part of the original image to keep when [precisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scaleDecider: ScaleDecider

    /**
     * The list of [Transformation]s to be applied to this request
     */
    val transformations: List<Transformation>?

    /**
     * Ignore Orientation property in file Exif info
     *
     * @see com.github.panpf.sketch.decode.internal.appliedExifOrientation
     */
    val ignoreExifOrientation: Boolean

    /**
     * Disk caching policy for Bitmaps affected by [sizeResolver] or [transformations]
     *
     * @see com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
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
     * Wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [sizeResolver]
     */
    val resizeOnDrawHelper: ResizeOnDrawHelper?

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

        private val context: PlatformContext
        private val uriString: String

        private var target: Target? = null

        private var defaultOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder
        private val definedRequestOptionsBuilder: RequestOptions.Builder

        constructor(context: PlatformContext, uriString: String?) {
            this.context = context
            this.uriString = uriString.orEmpty()
            this.definedOptionsBuilder = ImageOptions.Builder()
            this.definedRequestOptionsBuilder = RequestOptions.Builder()
        }

        constructor(request: ImageRequest) {
            this.context = request.context
            this.uriString = request.uriString
            this.target = request.target
            this.defaultOptions = request.defaultOptions
            this.definedOptionsBuilder = request.definedOptions.newBuilder()
            this.definedRequestOptionsBuilder = request.definedRequestOptions.newBuilder()
        }

        /**
         * Set the [Listener]
         */
        fun listener(
            listener: Listener?
        ): Builder = apply {
            definedRequestOptionsBuilder.listener(listener)
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
            definedRequestOptionsBuilder.addListener(listener)
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
            definedRequestOptionsBuilder.removeListener(listener)
        }

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener?
        ): Builder = apply {
            definedRequestOptionsBuilder.progressListener(progressListener)
        }

        /**
         * Add the [ProgressListener] to set
         */
        fun addProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            definedRequestOptionsBuilder.addProgressListener(progressListener)
        }

        /**
         * Remove the [ProgressListener] from set
         */
        fun removeProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            definedRequestOptionsBuilder.removeProgressListener(progressListener)
        }

        /**
         * Set the [TargetLifecycle] for this request.
         *
         * Requests are queued while the lifecycle is not at least [TargetLifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [TargetLifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycle: TargetLifecycle?): Builder = apply {
            definedRequestOptionsBuilder.lifecycle(lifecycle)
        }

        /**
         * Set the [LifecycleResolver] for this request.
         *
         * Requests are queued while the lifecycle is not at least [TargetLifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [TargetLifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycleResolver: LifecycleResolver?): Builder = apply {
            definedRequestOptionsBuilder.lifecycle(lifecycleResolver)
        }

        /**
         * Set the [Target].
         */
        fun target(target: Target?): Builder = apply {
            this.target = target
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
        fun size(sizeResolver: SizeResolver?): Builder = apply {
            definedOptionsBuilder.size(sizeResolver)
        }

        /**
         * Set the resize size
         */
        fun size(size: Size): Builder = apply {
            definedOptionsBuilder.size(size)
        }

        /**
         * Set the resize size
         */
        fun size(@Px width: Int, @Px height: Int): Builder = apply {
            definedOptionsBuilder.size(width, height)
        }

        /**
         * Set the resize precision
         */
        fun precision(precisionDecider: PrecisionDecider?): Builder = apply {
            definedOptionsBuilder.precision(precisionDecider)
        }

        /**
         * Set the resize precision
         */
        fun precision(precision: Precision): Builder = apply {
            definedOptionsBuilder.precision(precision)
        }

        /**
         * Set the resize scale
         */
        fun scale(scaleDecider: ScaleDecider?): Builder = apply {
            definedOptionsBuilder.scale(scaleDecider)
        }

        /**
         * Set the resize scale
         */
        fun scale(scale: Scale): Builder = apply {
            definedOptionsBuilder.scale(scale)
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
         * Set ignore Orientation property in file Exif info
         */
        fun ignoreExifOrientation(ignore: Boolean? = true): Builder = apply {
            definedOptionsBuilder.ignoreExifOrientation(ignore)
        }

        /**
         * Set disk caching policy for Bitmaps affected by [size] or [transformations]
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
         * Set placeholder image when uri is empty
         */
        fun uriEmpty(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.uriEmpty(stateImage)
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
         * Set disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
         */
        fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowAnimatedImage(disabled)
        }

        /**
         * Set wrap the final [Drawable] or [Painter] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [size]
         */
        fun resizeOnDraw(helper: ResizeOnDrawHelper?): Builder = apply {
            definedOptionsBuilder.resizeOnDraw(helper)
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


        fun build(): ImageRequest {
            val target = target
            val definedRequestOptions = definedRequestOptionsBuilder.build()
            val listener = combinationListener(definedRequestOptions, target)
            val progressListener = combinationProgressListener(definedRequestOptions, target)
            val lifecycleResolver = definedRequestOptions.lifecycleResolver
                ?: DefaultLifecycleResolver(resolveLifecycleResolver())
            val targetOptions = target?.getImageOptions()
            val definedOptions = definedOptionsBuilder.merge(targetOptions).build()
            val finalOptions = definedOptions.merged(defaultOptions)
            val depth = finalOptions.depth ?: Depth.NETWORK
            val parameters = finalOptions.parameters
            val httpHeaders = finalOptions.httpHeaders
            val downloadCachePolicy = finalOptions.downloadCachePolicy ?: CachePolicy.ENABLED
            val resultCachePolicy = finalOptions.resultCachePolicy ?: CachePolicy.ENABLED
            val sizeResolver = finalOptions.sizeResolver
                ?: resolveSizeResolver()
            val precisionDecider = finalOptions.precisionDecider
                ?: PrecisionDecider(Precision.LESS_PIXELS)
            val scaleDecider = finalOptions.scaleDecider ?: ScaleDecider(resolveScale())
            val transformations = finalOptions.transformations
            val ignoreExifOrientation = finalOptions.ignoreExifOrientation ?: false
            val placeholder = finalOptions.placeholder
            val uriEmpty = finalOptions.uriEmpty
            val error = finalOptions.error
            val transitionFactory = finalOptions.transitionFactory
            val disallowAnimatedImage = finalOptions.disallowAnimatedImage ?: false
            val resizeOnDrawHelper = finalOptions.resizeOnDrawHelper
            val memoryCachePolicy = finalOptions.memoryCachePolicy ?: CachePolicy.ENABLED
            val componentRegistry = finalOptions.componentRegistry

            // TODO 转换 Crossfade 为 CrossfadeTransition.Factory

            return ImageRequestImpl(
                context = context,
                uriString = uriString,
                listener = listener,
                progressListener = progressListener,
                target = target,
                lifecycleResolver = lifecycleResolver,
                defaultOptions = defaultOptions,
                definedOptions = definedOptions,
                definedRequestOptions = definedRequestOptions,
                depth = depth,
                parameters = parameters,
                httpHeaders = httpHeaders,
                downloadCachePolicy = downloadCachePolicy,
                resultCachePolicy = resultCachePolicy,
                sizeResolver = sizeResolver,
                precisionDecider = precisionDecider,
                scaleDecider = scaleDecider,
                transformations = transformations,
                ignoreExifOrientation = ignoreExifOrientation,
                placeholder = placeholder,
                uriEmpty = uriEmpty,
                error = error,
                transitionFactory = transitionFactory,
                disallowAnimatedImage = disallowAnimatedImage,
                resizeOnDrawHelper = resizeOnDrawHelper,
                memoryCachePolicy = memoryCachePolicy,
                componentRegistry = componentRegistry,
            )
        }

        private fun resolveSizeResolver(): SizeResolver =
            target?.getSizeResolver() ?: defaultSizeResolver(context)

        private fun resolveLifecycleResolver(): LifecycleResolver =
            target?.getLifecycleResolver() ?: FixedLifecycleResolver(GlobalTargetLifecycle)

        private fun resolveScale(): Scale =
            target?.getScale() ?: Scale.CENTER_CROP

        private fun combinationListener(
            definedRequestOptions: RequestOptions,
            target: Target?
        ): Listener? {
            val listener = definedRequestOptions.listener
            val listeners = definedRequestOptions.listeners?.takeIf { it.isNotEmpty() }?.toList()
            val targetListener = target?.getListener()
            return if (listeners != null || targetListener != null) {
                CombinedListener(
                    fromTargetListener = targetListener,
                    fromBuilderListener = listener,
                    fromBuilderListeners = listeners
                )
            } else {
                listener
            }
        }

        private fun combinationProgressListener(
            definedRequestOptions: RequestOptions,
            target: Target?
        ): ProgressListener? {
            val progressListener = definedRequestOptions.progressListener
            val progressListeners =
                definedRequestOptions.progressListeners?.takeIf { it.isNotEmpty() }?.toList()
            val targetProgressListener = target?.getProgressListener()
            return if (progressListeners != null || targetProgressListener != null) {
                CombinedProgressListener(
                    fromTargetProgressListener = targetProgressListener,
                    fromBuilderProgressListener = progressListener,
                    fromBuilderProgressListeners = progressListeners
                )
            } else {
                progressListener
            }
        }
    }

    data class ImageRequestImpl internal constructor(
        override val context: PlatformContext,
        override val uriString: String,
        override val listener: Listener?,
        override val progressListener: ProgressListener?,
        override val target: Target?,
        override val lifecycleResolver: LifecycleResolver,
        override val definedOptions: ImageOptions,
        override val defaultOptions: ImageOptions?,
        override val definedRequestOptions: RequestOptions,
        override val depth: Depth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val downloadCachePolicy: CachePolicy,
        override val sizeResolver: SizeResolver,
        override val precisionDecider: PrecisionDecider,
        override val scaleDecider: ScaleDecider,
        override val transformations: List<Transformation>?,
        override val ignoreExifOrientation: Boolean,
        override val resultCachePolicy: CachePolicy,
        override val placeholder: StateImage?,
        override val uriEmpty: StateImage?,
        override val error: ErrorStateImage?,
        override val transitionFactory: Transition.Factory?,
        override val disallowAnimatedImage: Boolean,
        override val resizeOnDrawHelper: ResizeOnDrawHelper?,
        override val memoryCachePolicy: CachePolicy,
        override val componentRegistry: ComponentRegistry?,
    ) : ImageRequest {

        override val key: String by lazy { newKey() }
    }
}