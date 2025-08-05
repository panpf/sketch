/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CacheKeyMapper
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.checkPlatformContext
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.ImageOptions.Builder
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.RequestOptions
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.*
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.*

/**
 * Build and set the [ImageRequest]
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageRequestTest.testImageRequest
 */
fun ImageRequest(
    context: PlatformContext,
    uri: String?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(context, uri).apply {
    block?.invoke(this)
}.build()

/**
 * An immutable image request that contains all the required parameters
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageRequestTest
 */
data class ImageRequest(

    /**
     * App Context
     */
    val context: PlatformContext,

    /**
     * The uri of the image to be loaded.
     */
    val uri: Uri,

    /**
     * [Target] is used to receive Drawable and draw it
     */
    val target: Target?,


    /**
     * [Listener] is used to receive the state and result of the request
     */
    val listener: Listener?,

    /**
     * [ProgressListener] is used to receive the download progress of the request
     */
    val progressListener: ProgressListener?,

    /**
     * The [Lifecycle] resolver for this request.
     * The request will be started when Lifecycle is in [Lifecycle.State.STARTED]
     * and canceled when Lifecycle is in [Lifecycle.State.DESTROYED].
     *
     * When [Lifecycle] is not actively set,
     * Sketch first obtains the Lifecycle at the nearest location through `view.findViewTreeLifecycleOwner()` and `LocalLifecycleOwner.current.lifecycle` APIs
     * Secondly, get the [Lifecycle] of Activity through context, and finally use [GlobalLifecycle]
     */
    val lifecycleResolver: LifecycleResolver,

    /**
     * Stores user-provided request-related parameters such as [Listener], [ProgressListener], [LifecycleResolver], etc.
     */
    val definedRequestOptions: RequestOptions,


    /**
     * Stores User-provided image related parameters
     */
    val definedOptions: ImageOptions,

    /**
     * Default image related parameters
     */
    val defaultOptions: ImageOptions?,


    /**
     * The processing depth of the request.
     */
    val depthHolder: DepthHolder,

    /**
     * A map of generic values that can be used to pass custom data to [Fetcher] and [Decoder].
     */
    val extras: Extras?,

    /**
     * Http download cache policy
     *
     * @see com.github.panpf.sketch.fetch.HttpUriFetcher
     */
    val downloadCachePolicy: CachePolicy,

    /**
     * The key used to cache the image in the download cache.
     *
     * @see com.github.panpf.sketch.request.RequestContext.downloadCacheKey
     */
    val downloadCacheKey: String?,

    /**
     * Mapper for unified modification of the automatically generated download cache key. [downloadCacheKey] Priority
     *
     * @see com.github.panpf.sketch.request.RequestContext.downloadCacheKey
     */
    val downloadCacheKeyMapper: CacheKeyMapper?,


    /**
     * Bitmap color type
     */
    val colorType: BitmapColorType?,

    /**
     * Bitmap color space
     */
    val colorSpace: BitmapColorSpace?,

    /**
     * Lazy calculation of resize size. If size is null at runtime, size is calculated and assigned to size
     */
    val sizeResolver: SizeResolver,

    /**
     * val finalSize = sizeResolver.size() * sizeMultiplier
     */
    val sizeMultiplier: Float?,

    /**
     * Decide what Precision to use with [sizeResolver] to calculate the size of the final Bitmap
     */
    val precisionDecider: PrecisionDecider,

    /**
     * Which part of the original image to keep when [precisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scaleDecider: ScaleDecider,

    /**
     * The list of [Transformation]s to be applied to this request
     */
    val transformations: List<Transformation>?,

    /**
     * Disk caching policy for Bitmaps affected by [sizeResolver] or [transformations]
     *
     * @see com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy,

    /**
     * The key used to cache the result image in the result cache.
     *
     * @see com.github.panpf.sketch.request.RequestContext.resultCacheKey
     */
    val resultCacheKey: String?,

    /**
     * Mapper for unified modification of the automatically generated result cache key. [resultCacheKey] Priority
     *
     * @see com.github.panpf.sketch.request.RequestContext.resultCacheKey
     */
    val resultCacheKeyMapper: CacheKeyMapper?,


    /**
     * Placeholder image when loading
     */
    val placeholder: StateImage?,

    /**
     * Image to display when uri is invalid
     */
    val fallback: StateImage?,

    /**
     * Image to display when loading fails
     */
    val error: StateImage?,

    /**
     * How the current image and the new image transition
     */
    val transitionFactory: Transition.Factory?,

    /**
     * Use ResizeDrawable or ResizePainter to wrap an Image to resize it while drawing, it will act on placeholder, fallback, error and the decoded image
     */
    val resizeOnDraw: Boolean?,

    /**
     * Allow setting null Image to ImageView or AsyncImage
     */
    val allowNullImage: Boolean?,

    /**
     * Bitmap memory caching policy
     *
     * @see com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
     */
    val memoryCachePolicy: CachePolicy,

    /**
     * The key used to cache the image in the memory cache.
     *
     * @see com.github.panpf.sketch.request.RequestContext.memoryCacheKey
     */
    val memoryCacheKey: String?,

    /**
     * Mapper for unified modification of the automatically generated memory cache key. [memoryCacheKey] Priority
     *
     * @see com.github.panpf.sketch.request.RequestContext.memoryCacheKey
     */
    val memoryCacheKeyMapper: CacheKeyMapper?,


    /** Components that are only valid for the current request */
    val componentRegistry: ComponentRegistry?,
) : Key {

    /**
     * The unique identifier for this request.
     */
    override val key: String by lazy { newKey() }

    // For keep binary compatibility
    constructor(
        context: PlatformContext,
        uri: Uri,
        target: Target?,
        listener: Listener?,
        progressListener: ProgressListener?,
        lifecycleResolver: LifecycleResolver,
        definedRequestOptions: RequestOptions,
        definedOptions: ImageOptions,
        defaultOptions: ImageOptions?,
        depthHolder: DepthHolder,
        extras: Extras?,
        downloadCachePolicy: CachePolicy,
        colorType: BitmapColorType?,
        colorSpace: BitmapColorSpace?,
        sizeResolver: SizeResolver,
        sizeMultiplier: Float?,
        precisionDecider: PrecisionDecider,
        scaleDecider: ScaleDecider,
        transformations: List<Transformation>?,
        resultCachePolicy: CachePolicy,
        placeholder: StateImage?,
        fallback: StateImage?,
        error: StateImage?,
        transitionFactory: Transition.Factory?,
        resizeOnDraw: Boolean?,
        allowNullImage: Boolean?,
        memoryCachePolicy: CachePolicy,
        componentRegistry: ComponentRegistry?,
    ) : this(
        context = context,
        uri = uri,
        target = target,
        listener = listener,
        progressListener = progressListener,
        lifecycleResolver = lifecycleResolver,
        definedRequestOptions = definedRequestOptions,
        definedOptions = definedOptions,
        defaultOptions = defaultOptions,
        depthHolder = depthHolder,
        extras = extras,
        downloadCachePolicy = downloadCachePolicy,
        downloadCacheKey = null,
        downloadCacheKeyMapper = null,
        colorType = colorType,
        colorSpace = colorSpace,
        sizeResolver = sizeResolver,
        sizeMultiplier = sizeMultiplier,
        precisionDecider = precisionDecider,
        scaleDecider = scaleDecider,
        transformations = transformations,
        resultCachePolicy = resultCachePolicy,
        resultCacheKey = null,
        resultCacheKeyMapper = null,
        placeholder = placeholder,
        fallback = fallback,
        error = error,
        transitionFactory = transitionFactory,
        resizeOnDraw = resizeOnDraw,
        allowNullImage = allowNullImage,
        memoryCachePolicy = memoryCachePolicy,
        memoryCacheKey = null,
        memoryCacheKeyMapper = null,
        componentRegistry = componentRegistry
    )

    init {
        checkPlatformContext(context)
    }

    /**
     * Create a new [ImageRequest.Builder] based on the current [ImageRequest].
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newBuilder(
        uri: String? = this.uri.toString(),
        block: (Builder.() -> Unit)? = null
    ): Builder = Builder(this, uri).apply {
        block?.invoke(this)
    }

    /**
     * Create a new [ImageRequest] based on the current [ImageRequest].
     *
     * You can extend it with a trailing lambda function [block]
     */
    fun newRequest(
        uri: String? = this.uri.toString(),
        block: (Builder.() -> Unit)? = null
    ): ImageRequest = Builder(this, uri).apply {
        block?.invoke(this)
    }.build()

    // For keep binary compatibility
    fun copy(
        context: PlatformContext = this.context,
        uri: Uri = this.uri,
        target: Target? = this.target,
        listener: Listener? = this.listener,
        progressListener: ProgressListener? = this.progressListener,
        lifecycleResolver: LifecycleResolver = this.lifecycleResolver,
        definedRequestOptions: RequestOptions = this.definedRequestOptions,
        definedOptions: ImageOptions = this.definedOptions,
        defaultOptions: ImageOptions? = this.defaultOptions,
        depthHolder: DepthHolder = this.depthHolder,
        extras: Extras? = this.extras,
        downloadCachePolicy: CachePolicy = this.downloadCachePolicy,
        colorType: BitmapColorType? = this.colorType,
        colorSpace: BitmapColorSpace? = this.colorSpace,
        sizeResolver: SizeResolver = this.sizeResolver,
        sizeMultiplier: Float? = this.sizeMultiplier,
        precisionDecider: PrecisionDecider = this.precisionDecider,
        scaleDecider: ScaleDecider = this.scaleDecider,
        transformations: List<Transformation>? = this.transformations,
        resultCachePolicy: CachePolicy = this.resultCachePolicy,
        placeholder: StateImage? = this.placeholder,
        fallback: StateImage? = this.fallback,
        error: StateImage? = this.error,
        transitionFactory: Transition.Factory? = this.transitionFactory,
        resizeOnDraw: Boolean? = this.resizeOnDraw,
        allowNullImage: Boolean? = this.allowNullImage,
        memoryCachePolicy: CachePolicy = this.memoryCachePolicy,
        componentRegistry: ComponentRegistry? = this.componentRegistry
    ): ImageRequest = ImageRequest(
        context = context,
        uri = uri,
        target = target,
        listener = listener,
        progressListener = progressListener,
        lifecycleResolver = lifecycleResolver,
        definedRequestOptions = definedRequestOptions,
        definedOptions = definedOptions,
        defaultOptions = defaultOptions,
        depthHolder = depthHolder,
        extras = extras,
        downloadCachePolicy = downloadCachePolicy,
        downloadCacheKey = downloadCacheKey,
        downloadCacheKeyMapper = downloadCacheKeyMapper,
        colorType = colorType,
        colorSpace = colorSpace,
        sizeResolver = sizeResolver,
        sizeMultiplier = sizeMultiplier,
        precisionDecider = precisionDecider,
        scaleDecider = scaleDecider,
        transformations = transformations,
        resultCachePolicy = resultCachePolicy,
        resultCacheKey = resultCacheKey,
        resultCacheKeyMapper = resultCacheKeyMapper,
        placeholder = placeholder,
        fallback = fallback,
        error = error,
        transitionFactory = transitionFactory,
        resizeOnDraw = resizeOnDraw,
        allowNullImage = allowNullImage,
        memoryCachePolicy = memoryCachePolicy,
        memoryCacheKey = memoryCacheKey,
        memoryCacheKeyMapper = memoryCacheKeyMapper,
        componentRegistry = componentRegistry
    )

    class Builder {

        private val context: PlatformContext
        private val uri: Uri

        private var target: Target? = null

        private var defaultOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder
        private val definedRequestOptionsBuilder: RequestOptions.Builder

        constructor(context: PlatformContext, uri: String?) {
            this.context = context.application
            checkPlatformContext(this.context)
            this.uri = uri.orEmpty().toUri()
            this.definedOptionsBuilder = Builder()
            this.definedRequestOptionsBuilder = RequestOptions.Builder()
        }

        constructor(request: ImageRequest, uri: String? = request.uri.toString()) {
            this.context = request.context
            this.uri = uri.orEmpty().toUri()
            this.target = request.target
            this.defaultOptions = request.defaultOptions
            this.definedOptionsBuilder = request.definedOptions.newBuilder()
            this.definedRequestOptionsBuilder = request.definedRequestOptions.newBuilder()
        }

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
        @Suppress("unused")
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
         * Set the [Lifecycle] for this request.
         *
         * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            definedRequestOptionsBuilder.lifecycle(
                if (lifecycle != null) LifecycleResolver(
                    lifecycle
                ) else null
            )
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
        fun depth(depth: Depth?, from: String? = null): Builder = apply {
            definedOptionsBuilder.depth(depth, from)
        }

        /**
         * Bulk set parameters for this request
         */
        fun extras(extras: Extras?): Builder = apply {
            definedOptionsBuilder.extras(extras)
        }

        /**
         * Set a parameter for this request.
         */
        fun setExtra(
            key: String,
            value: Any?,
            cacheKey: String? = keyOrNull(value),
            requestKey: String? = keyOrNull(value),
        ): Builder = apply {
            definedOptionsBuilder.setExtra(key, value, cacheKey, requestKey)
        }

        /**
         * Remove a parameter from this request.
         */
        fun removeExtra(key: String): Builder = apply {
            definedOptionsBuilder.removeExtra(key)
        }

        /**
         * Set http download cache policy
         */
        fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.downloadCachePolicy(cachePolicy)
        }

        /**
         * Set the key used to cache the downloaded image in the download cache.
         */
        fun downloadCacheKey(key: String?): Builder = apply {
            definedOptionsBuilder.downloadCacheKey(key)
        }

        /**
         * Set a mapper that maps a download cache key to a different string representation.
         */
        fun downloadCacheKeyMapper(mapper: CacheKeyMapper?): Builder = apply {
            definedOptionsBuilder.downloadCacheKeyMapper(mapper)
        }

        /**
         * Set bitmap color type
         */
        fun colorType(colorType: BitmapColorType?): Builder = apply {
            definedOptionsBuilder.colorType(colorType)
        }

        /**
         * Set bitmap color type
         */
        fun colorType(colorType: String?): Builder = apply {
            definedOptionsBuilder.colorType(colorType)
        }

        /**
         * Set bitmap color space
         */
        fun colorSpace(colorSpace: BitmapColorSpace?): Builder = apply {
            definedOptionsBuilder.colorSpace(colorSpace)
        }

        /**
         * Set bitmap color space
         */
        fun colorSpace(colorSpace: String?): Builder = apply {
            definedOptionsBuilder.colorSpace(colorSpace)
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
            width: Int,
            height: Int,
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
        fun size(width: Int, height: Int): Builder = apply {
            definedOptionsBuilder.size(width, height)
        }

        /**
         * val finalSize = sizeResolver.size() * sizeMultiplier
         */
        fun sizeMultiplier(multiplier: Float?): Builder = apply {
            definedOptionsBuilder.sizeMultiplier(multiplier)
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
         * Set disk caching policy for Bitmaps affected by [size] or [transformations]
         */
        fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.resultCachePolicy(cachePolicy)
        }

        /**
         * Set the key used to cache the result image in the result cache.
         */
        fun resultCacheKey(key: String?): Builder = apply {
            definedOptionsBuilder.resultCacheKey(key)
        }

        /**
         * Set a mapper that maps a result cache key to a different string representation.
         */
        fun resultCacheKeyMapper(mapper: CacheKeyMapper?): Builder = apply {
            definedOptionsBuilder.resultCacheKeyMapper(mapper)
        }


        /**
         * Set placeholder image when loading
         */
        fun placeholder(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.placeholder(stateImage)
        }

        /**
         * Set placeholder image when uri is invalid
         */
        fun fallback(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.fallback(stateImage)
        }

        /**
         * Set image to display when loading fails.
         */
        fun error(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.error(stateImage)
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
            durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
            fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
            preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
            alwaysUse: Boolean = CrossfadeTransition.DEFAULT_ALWAYS_USE,
        ): Builder = apply {
            definedOptionsBuilder.crossfade(
                durationMillis = durationMillis,
                fadeStart = fadeStart,
                preferExactIntrinsicSize = preferExactIntrinsicSize,
                alwaysUse = alwaysUse
            )
        }

        /**
         * Sets the transition that crossfade
         */
        fun crossfade(enable: Boolean): Builder = apply {
            definedOptionsBuilder.crossfade(enable)
        }

        /**
         * Use ResizeDrawable or ResizePainter to wrap an Image to resize it while drawing, it will act on placeholder, fallback, error and the decoded image
         */
        fun resizeOnDraw(apply: Boolean? = true): Builder = apply {
            definedOptionsBuilder.resizeOnDraw(apply)
        }

        /**
         * Allow setting null Image to ImageView or AsyncImage
         */
        fun allowNullImage(allow: Boolean? = true): Builder = apply {
            definedOptionsBuilder.allowNullImage(allow)
        }

        /**
         * Set bitmap memory caching policy
         */
        fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.memoryCachePolicy(cachePolicy)
        }

        /**
         * Set the key used to cache the memory image in the memory cache.
         */
        fun memoryCacheKey(key: String?): Builder = apply {
            definedOptionsBuilder.memoryCacheKey(key)
        }

        /**
         * Set a mapper that maps a memory cache key to a different string representation.
         */
        fun memoryCacheKeyMapper(mapper: CacheKeyMapper?): Builder = apply {
            definedOptionsBuilder.memoryCacheKeyMapper(mapper)
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
        fun defaultOptions(options: ImageOptions?): Builder = apply {
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
        fun components(block: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.components(block)
        }

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(components: ComponentRegistry?): Builder = apply {
            definedOptionsBuilder.addComponents(components)
        }

        /**
         * Build and merge the [ComponentRegistry]
         */
        fun addComponents(block: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.addComponents(block)
        }


        fun build(): ImageRequest {
            val target = target
            val definedRequestOptions = definedRequestOptionsBuilder.build()
            val listener = combinationListener(definedRequestOptions, target)
            val progressListener = combinationProgressListener(definedRequestOptions, target)
            val lifecycleResolver =
                definedRequestOptions.lifecycleResolver ?: resolveLifecycleResolver()
            val targetOptions = target?.getImageOptions()
            val definedOptions = definedOptionsBuilder.merge(targetOptions).build()
            val finalOptions = definedOptions.merged(defaultOptions)
            val depthHolder = finalOptions.depthHolder ?: DepthHolder.Default
            val extras = finalOptions.extras
            val downloadCachePolicy = finalOptions.downloadCachePolicy ?: CachePolicy.ENABLED
            val downloadCacheKey = finalOptions.downloadCacheKey
            val downloadCacheKeyMapper = finalOptions.downloadCacheKeyMapper
            val colorType = finalOptions.colorType
            val colorSpace = finalOptions.colorSpace
            val sizeResolver = finalOptions.sizeResolver ?: resolveSizeResolver()
            val sizeMultiplier = finalOptions.sizeMultiplier
            val precisionDecider =
                finalOptions.precisionDecider ?: PrecisionDecider(Precision.LESS_PIXELS)
            val scaleDecider = finalOptions.scaleDecider ?: resolveScaleDecider()
            val transformations = finalOptions.transformations
            val resultCachePolicy = finalOptions.resultCachePolicy ?: CachePolicy.ENABLED
            val resultCacheKey = finalOptions.resultCacheKey
            val resultCacheKeyMapper = finalOptions.resultCacheKeyMapper
            val placeholder = finalOptions.placeholder
            val fallback = finalOptions.fallback
            val error = finalOptions.error
            val transitionFactory = finalOptions.transitionFactory
            val resizeOnDraw = finalOptions.resizeOnDraw
            val allowNullImage = finalOptions.allowNullImage
            val memoryCachePolicy = finalOptions.memoryCachePolicy ?: CachePolicy.ENABLED
            val memoryCacheKey = finalOptions.memoryCacheKey
            val memoryCacheKeyMapper = finalOptions.memoryCacheKeyMapper
            val targetComponents = target?.getComponents()
            val componentRegistry = finalOptions.componentRegistry.merged(targetComponents)

            return ImageRequest(
                context = context,
                uri = uri,
                listener = listener,
                progressListener = progressListener,
                target = target,
                lifecycleResolver = lifecycleResolver,
                defaultOptions = defaultOptions,
                definedOptions = definedOptions,
                definedRequestOptions = definedRequestOptions,
                depthHolder = depthHolder,
                extras = extras,
                downloadCachePolicy = downloadCachePolicy,
                downloadCacheKey = downloadCacheKey,
                downloadCacheKeyMapper = downloadCacheKeyMapper,
                colorType = colorType,
                colorSpace = colorSpace,
                sizeResolver = sizeResolver,
                sizeMultiplier = sizeMultiplier,
                precisionDecider = precisionDecider,
                scaleDecider = scaleDecider,
                transformations = transformations,
                resultCachePolicy = resultCachePolicy,
                resultCacheKey = resultCacheKey,
                resultCacheKeyMapper = resultCacheKeyMapper,
                placeholder = placeholder,
                fallback = fallback,
                error = error,
                transitionFactory = transitionFactory,
                resizeOnDraw = resizeOnDraw,
                allowNullImage = allowNullImage,
                memoryCachePolicy = memoryCachePolicy,
                memoryCacheKey = memoryCacheKey,
                memoryCacheKeyMapper = memoryCacheKeyMapper,
                componentRegistry = componentRegistry,
            )
        }

        private fun resolveSizeResolver(): SizeResolver =
            target?.getSizeResolver() ?: SizeResolver(context.screenSize())

        private fun resolveLifecycleResolver(): LifecycleResolver =
            target?.getLifecycleResolver() ?: FixedLifecycleResolver(GlobalLifecycle)

        private fun resolveScaleDecider(): ScaleDecider =
            target?.getScaleDecider() ?: ScaleDecider(Scale.CENTER_CROP)

        private fun combinationListener(
            definedRequestOptions: RequestOptions,
            target: Target?
        ): Listener? {
            val builderListener = definedRequestOptions.listener
            val targetListener = target?.getListener()
            return if (builderListener != null && targetListener != null) {
                PairListener(first = builderListener, second = targetListener)
            } else {
                builderListener ?: targetListener
            }
        }

        private fun combinationProgressListener(
            definedRequestOptions: RequestOptions,
            target: Target?
        ): ProgressListener? {
            val builderProgressListener = definedRequestOptions.progressListener
            val targetProgressListener = target?.getProgressListener()
            return if (builderProgressListener != null && targetProgressListener != null) {
                PairProgressListener(
                    first = builderProgressListener,
                    second = targetProgressListener
                )
            } else {
                builderProgressListener ?: targetProgressListener
            }
        }
    }
}