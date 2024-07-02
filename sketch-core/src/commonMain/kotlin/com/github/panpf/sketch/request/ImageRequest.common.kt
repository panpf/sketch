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

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.RequestOptions
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.keyOrNull
import com.github.panpf.sketch.util.screenSize

/**
 * Build and set the [ImageRequest]
 */
fun ImageRequest(
    context: PlatformContext,
    uri: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(context, uri).apply {
    configBlock?.invoke(this)
}.build()

/**
 * An immutable image request that contains all the required parameters,
 */
data class ImageRequest(

    /**
     * App Context
     */
    val context: PlatformContext,

    /**
     * The uri of the image to be loaded.
     */
    val uri: String,

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
     * Set headers for http requests
     *
     * @see com.github.panpf.sketch.http.HurlStack.getResponse
     */
    val httpHeaders: HttpHeaders?,

    /**
     * Http download cache policy
     *
     * @see com.github.panpf.sketch.fetch.HttpUriFetcher
     */
    val downloadCachePolicy: CachePolicy,


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
    val error: ErrorStateImage?,

    /**
     * How the current image and the new image transition
     */
    val transitionFactory: Transition.Factory?,

    /**
     * Disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
     */
    val disallowAnimatedImage: Boolean,

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


    /** Components that are only valid for the current request */
    val componentRegistry: ComponentRegistry?,
) : Key {

    /**
     * The unique identifier for this request.
     */
    override val key: String by lazy { newKey() }

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
        private val uri: String

        private var target: Target? = null

        private var defaultOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder
        private val definedRequestOptionsBuilder: RequestOptions.Builder

        constructor(context: PlatformContext, uri: String?) {
            this.context = context
            this.uri = uri.orEmpty()
            this.definedOptionsBuilder = ImageOptions.Builder()
            this.definedRequestOptionsBuilder = RequestOptions.Builder()
        }

        constructor(request: ImageRequest) {
            this.context = request.context
            this.uri = request.uri
            this.target = request.target
            this.defaultOptions = request.defaultOptions
            this.definedOptionsBuilder = request.definedOptions.newBuilder()
            this.definedRequestOptionsBuilder = request.definedRequestOptions.newBuilder()
        }

        /**
         * Add the [Listener] to set
         */
        fun registerListener(
            listener: Listener
        ): Builder = apply {
            definedRequestOptionsBuilder.registerListener(listener)
        }

        /**
         * Add the [Listener] to set
         */
        @Suppress("unused")
        inline fun registerListener(
            crossinline onStart: (request: ImageRequest) -> Unit = {},
            crossinline onCancel: (request: ImageRequest) -> Unit = {},
            crossinline onError: (request: ImageRequest, result: ImageResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: ImageRequest, result: ImageResult.Success) -> Unit = { _, _ -> }
        ): Builder = registerListener(object :
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
        fun unregisterListener(
            listener: Listener
        ): Builder = apply {
            definedRequestOptionsBuilder.unregisterListener(listener)
        }

        /**
         * Add the [ProgressListener] to set
         */
        fun registerProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            definedRequestOptionsBuilder.registerProgressListener(progressListener)
        }

        /**
         * Remove the [ProgressListener] from set
         */
        fun unregisterProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            definedRequestOptionsBuilder.unregisterProgressListener(progressListener)
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
            definedRequestOptionsBuilder.lifecycle(lifecycle)
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
        fun depth(depth: Depth?, depthFrom: String? = null): Builder = apply {
            definedOptionsBuilder.depth(depth, depthFrom)
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
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultImage, configBlock)
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
         * Set disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
         */
        fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowAnimatedImage(disabled)
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
        fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.components(configBlock)
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
        fun addComponents(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            definedOptionsBuilder.addComponents(configBlock)
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
            val httpHeaders = finalOptions.httpHeaders
            val downloadCachePolicy = finalOptions.downloadCachePolicy ?: CachePolicy.ENABLED
            val resultCachePolicy = finalOptions.resultCachePolicy ?: CachePolicy.ENABLED
            val sizeResolver = finalOptions.sizeResolver ?: resolveSizeResolver()
            val sizeMultiplier = finalOptions.sizeMultiplier
            val precisionDecider =
                finalOptions.precisionDecider ?: PrecisionDecider(Precision.LESS_PIXELS)
            val scaleDecider = finalOptions.scaleDecider ?: resolveScaleDecider()
            val transformations = finalOptions.transformations
            val placeholder = finalOptions.placeholder
            val fallback = finalOptions.fallback
            val error = finalOptions.error
            val transitionFactory = finalOptions.transitionFactory
            val disallowAnimatedImage = finalOptions.disallowAnimatedImage ?: false
            val resizeOnDraw = finalOptions.resizeOnDraw
            val allowNullImage = finalOptions.allowNullImage
            val memoryCachePolicy = finalOptions.memoryCachePolicy ?: CachePolicy.ENABLED
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
                httpHeaders = httpHeaders,
                downloadCachePolicy = downloadCachePolicy,
                resultCachePolicy = resultCachePolicy,
                sizeResolver = sizeResolver,
                sizeMultiplier = sizeMultiplier,
                precisionDecider = precisionDecider,
                scaleDecider = scaleDecider,
                transformations = transformations,
                placeholder = placeholder,
                fallback = fallback,
                error = error,
                transitionFactory = transitionFactory,
                disallowAnimatedImage = disallowAnimatedImage,
                resizeOnDraw = resizeOnDraw,
                allowNullImage = allowNullImage,
                memoryCachePolicy = memoryCachePolicy,
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