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

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.keyOrNull

/**
 * Build and set the [ImageOptions]
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageOptionsTest.testImageOptions
 */
fun ImageOptions(
    configBlock: (ImageOptions.Builder.() -> Unit)? = null
): ImageOptions = ImageOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()

/**
 * Stores parameters required to download, load, display images
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageOptionsTest
 */
data class ImageOptions(

    /**
     * The processing depth of the request.
     */
    val depthHolder: DepthHolder?,

    /**
     * A map of generic values that can be used to pass custom data to [Fetcher] and [Decoder].
     */
    val extras: Extras?,

    /**
     * Http download cache policy
     *
     * @see com.github.panpf.sketch.fetch.HttpUriFetcher
     */
    val downloadCachePolicy: CachePolicy?,


    /**
     * Bitmap color type
     */
    val colorType: BitmapColorType?,

    /**
     * Bitmap color space
     */
    val colorSpace: BitmapColorSpace?,

    /**
     * Lazy calculation of resize size. If resize size is null at runtime, size is calculated and assigned to size
     */
    val sizeResolver: SizeResolver?,

    /**
     * val finalSize = sizeResolver.size() * sizeMultiplier
     */
    val sizeMultiplier: Float?,

    /**
     * Decide what Precision to use with [sizeResolver] to calculate the size of the final Bitmap
     */
    val precisionDecider: PrecisionDecider?,

    /**
     * Which part of the original image to keep when [precisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scaleDecider: ScaleDecider?,

    /**
     * The list of [Transformation]s to be applied to this request
     */
    val transformations: List<Transformation>?,

    /**
     * Disk caching policy for Bitmaps affected by [sizeResolver] or [transformations]
     *
     * @see com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy?,


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
    val memoryCachePolicy: CachePolicy?,


    /**
     * Components that are only valid for the current request
     */
    val componentRegistry: ComponentRegistry?,
) {

    /**
     * Create a new [ImageOptions.Builder] based on the current [ImageOptions].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    /**
     * Create a new [ImageOptions] based on the current [ImageOptions].
     *
     * You can extend it with a trailing lambda function [configBlock]
     */
    fun newOptions(
        configBlock: (Builder.() -> Unit)? = null
    ): ImageOptions = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    /**
     * Merge the current [ImageOptions] and the new [ImageOptions] into a new [ImageOptions]. Currently [ImageOptions] takes precedence
     */
    fun merged(
        options: ImageOptions?
    ): ImageOptions {
        if (options == null) return this
        return Builder(this).apply {
            merge(options)
        }.build()
    }

    /**
     * Returns true if all properties are empty
     */
    fun isEmpty(): Boolean =
        depthHolder == null
                && extras?.isEmpty() != false
                && downloadCachePolicy == null
                && colorType == null
                && colorSpace == null
                && sizeResolver == null
                && sizeMultiplier == null
                && precisionDecider == null
                && scaleDecider == null
                && transformations == null
                && resultCachePolicy == null
                && placeholder == null
                && fallback == null
                && error == null
                && transitionFactory == null
                && resizeOnDraw == null
                && allowNullImage == null
                && memoryCachePolicy == null
                && componentRegistry == null

    class Builder {

        private var depthHolder: DepthHolder? = null
        private var extrasBuilder: Extras.Builder? = null

        private var downloadCachePolicy: CachePolicy? = null

        private var colorType: BitmapColorType? = null
        private var colorSpace: BitmapColorSpace? = null
        private var sizeResolver: SizeResolver? = null
        private var sizeMultiplier: Float? = null
        private var precisionDecider: PrecisionDecider? = null
        private var scaleDecider: ScaleDecider? = null
        private var transformations: MutableList<Transformation>? = null
        private var resultCachePolicy: CachePolicy? = null

        private var placeholder: StateImage? = null
        private var fallback: StateImage? = null
        private var error: StateImage? = null
        private var transitionFactory: Transition.Factory? = null
        private var resizeOnDraw: Boolean? = null
        private var allowNullImage: Boolean? = null
        private var memoryCachePolicy: CachePolicy? = null

        private var componentRegistry: ComponentRegistry? = null

        constructor()

        internal constructor(options: ImageOptions) {
            this.depthHolder = options.depthHolder
            this.extrasBuilder = options.extras?.newBuilder()

            this.downloadCachePolicy = options.downloadCachePolicy

            this.colorType = options.colorType
            this.colorSpace = options.colorSpace
            this.sizeResolver = options.sizeResolver
            this.sizeMultiplier = options.sizeMultiplier
            this.precisionDecider = options.precisionDecider
            this.scaleDecider = options.scaleDecider
            this.transformations = options.transformations?.toMutableList()
            this.resultCachePolicy = options.resultCachePolicy

            this.placeholder = options.placeholder
            this.fallback = options.fallback
            this.error = options.error
            this.transitionFactory = options.transitionFactory
            this.resizeOnDraw = options.resizeOnDraw
            this.allowNullImage = options.allowNullImage
            this.memoryCachePolicy = options.memoryCachePolicy

            this.componentRegistry = options.componentRegistry
        }


        /**
         * Set the requested depth
         */
        fun depth(depth: Depth?, from: String? = null): Builder = apply {
            this.depthHolder = depth?.let { DepthHolder(it, from) }
        }


        /**
         * Bulk set parameters for this request
         */
        fun extras(extras: Extras?): Builder = apply {
            this.extrasBuilder = extras?.newBuilder()
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
            this.extrasBuilder = (this.extrasBuilder ?: Extras.Builder()).apply {
                set(key, value, cacheKey, requestKey)
            }
        }

        /**
         * Remove a parameter from this request.
         */
        fun removeExtra(key: String): Builder = apply {
            this.extrasBuilder?.remove(key)
        }

        /**
         * Set http download cache policy
         */
        fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            this.downloadCachePolicy = cachePolicy
        }

        /**
         * Set bitmap color type
         */
        fun colorType(colorType: BitmapColorType?): Builder = apply {
            this.colorType = colorType
        }

        /**
         * Set bitmap color type
         */
        fun colorType(colorType: String?): Builder = apply {
            this.colorType = colorType?.let { BitmapColorType(it) }
        }

        /**
         * Set bitmap color space
         */
        fun colorSpace(colorSpace: BitmapColorSpace?): Builder = apply {
            this.colorSpace = colorSpace
        }

        /**
         * Set bitmap color space
         */
        fun colorSpace(colorSpace: String?): Builder = apply {
            this.colorSpace = colorSpace?.let { BitmapColorSpace(it) }
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
            this.sizeResolver = size
            this.precisionDecider = precision
            this.scaleDecider = scale
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
        ): Builder = resize(
            SizeResolver(size),
            precision?.let { PrecisionDecider(it) },
            scale?.let { ScaleDecider(it) }
        )

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
        ): Builder = resize(
            FixedSizeResolver(width, height),
            precision?.let { PrecisionDecider(it) },
            scale?.let { ScaleDecider(it) }
        )

        /**
         * Set the [SizeResolver] to lazy resolve the requested size.
         */
        fun size(sizeResolver: SizeResolver?): Builder = apply {
            this.sizeResolver = sizeResolver
        }

        /**
         * Set the resize size
         */
        fun size(size: Size): Builder = size(SizeResolver(size))

        /**
         * Set the resize size
         */
        fun size(width: Int, height: Int): Builder = size(SizeResolver(width, height))

        /**
         * val finalSize = sizeResolver.size() * sizeMultiplier
         */
        fun sizeMultiplier(multiplier: Float?): Builder = apply {
            this.sizeMultiplier = multiplier
        }

        /**
         * Set the resize precision, default is [Precision.LESS_PIXELS]
         */
        fun precision(precisionDecider: PrecisionDecider?): Builder = apply {
            this.precisionDecider = precisionDecider
        }

        /**
         * Set the resize precision, default is [Precision.LESS_PIXELS]
         */
        fun precision(precision: Precision): Builder =
            precision(PrecisionDecider(precision))

        /**
         * Set the resize scale, default is [Scale.CENTER_CROP]
         */
        fun scale(scaleDecider: ScaleDecider?): Builder = apply {
            this.scaleDecider = scaleDecider
        }

        /**
         * Set the resize scale, default is [Scale.CENTER_CROP]
         */
        fun scale(scale: Scale): Builder = scale(ScaleDecider(scale))

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = transformations?.toMutableList()
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        fun transformations(vararg transformations: Transformation): Builder =
            transformations(transformations.toList())

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        fun addTransformations(transformations: List<Transformation>): Builder = apply {
            val filterTransformations = transformations.filter { newTransformation ->
                this.transformations?.find { it.key == newTransformation.key } == null
            }
            this.transformations = (this.transformations ?: mutableListOf()).apply {
                addAll(filterTransformations)
            }
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        fun addTransformations(vararg transformations: Transformation): Builder =
            addTransformations(transformations.toList())

        /**
         * Bulk remove from current [Transformation] list
         */
        fun removeTransformations(removeTransformations: List<Transformation>): Builder =
            apply {
                this.transformations = this.transformations?.filter { oldTransformation ->
                    removeTransformations.find { it.key == oldTransformation.key } == null
                }?.toMutableList()
            }

        /**
         * Bulk remove from current [Transformation] list
         */
        fun removeTransformations(vararg removeTransformations: Transformation): Builder =
            removeTransformations(removeTransformations.toList())

        /**
         * Set disk caching policy for Bitmaps affected by [size] or [transformations]
         */
        fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            this.resultCachePolicy = cachePolicy
        }


        /**
         * Set placeholder image when loading
         */
        fun placeholder(stateImage: StateImage?): Builder = apply {
            this.placeholder = stateImage
        }

        /**
         * Set placeholder image when uri is invalid
         */
        fun fallback(stateImage: StateImage?): Builder = apply {
            this.fallback = stateImage
        }

        /**
         * Set image to display when loading fails.
         */
        fun error(stateImage: StateImage?): Builder = apply {
            this.error = stateImage
        }

        /**
         * Set the transition between the current image and the new image
         */
        fun transitionFactory(transitionFactory: Transition.Factory?): Builder = apply {
            this.transitionFactory = transitionFactory
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
            this.transitionFactory = CrossfadeTransition.Factory(
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
            if (enable) {
                crossfade()
            } else {
                this.transitionFactory = null
            }
        }

        /**
         * Use ResizeDrawable or ResizePainter to wrap an Image to resize it while drawing, it will act on placeholder, fallback, error and the decoded image
         */
        fun resizeOnDraw(apply: Boolean? = true): Builder = apply {
            this.resizeOnDraw = apply
        }

        /**
         * Allow setting null Image to ImageView or AsyncImage
         */
        fun allowNullImage(allow: Boolean? = true): Builder = apply {
            this.allowNullImage = allow
        }

        /**
         * Set bitmap memory caching policy
         */
        fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            this.memoryCachePolicy = cachePolicy
        }

        /**
         * Set the [ComponentRegistry]
         */
        fun components(components: ComponentRegistry?): Builder = apply {
            this.componentRegistry = components
        }

        /**
         * Build and set the [ComponentRegistry]
         */
        fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder =
            components(ComponentRegistry.Builder().apply(configBlock).build())

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(components: ComponentRegistry?): Builder = apply {
            this.componentRegistry = this.componentRegistry.merged(components)
        }

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder =
            addComponents(ComponentRegistry.Builder().apply(configBlock).build())


        /**
         * Merge the specified [ImageOptions] into the current [Builder]. Currently [Builder] takes precedence
         */
        fun merge(options: ImageOptions?): Builder = apply {
            if (options == null) return@apply

            if (this.depthHolder == null) {
                this.depthHolder = options.depthHolder
            }
            options.extras?.let {
                extrasBuilder = extrasBuilder?.build().merged(it)?.newBuilder()
            }

            if (this.downloadCachePolicy == null) {
                this.downloadCachePolicy = options.downloadCachePolicy
            }

            if (this.colorType == null) {
                this.colorType = options.colorType
            }
            if (this.colorSpace == null) {
                this.colorSpace = options.colorSpace
            }
            if (this.sizeResolver == null) {
                this.sizeResolver = options.sizeResolver
            }
            if (this.sizeMultiplier == null) {
                this.sizeMultiplier = options.sizeMultiplier
            }
            if (this.precisionDecider == null) {
                this.precisionDecider = options.precisionDecider
            }
            if (this.scaleDecider == null) {
                this.scaleDecider = options.scaleDecider
            }
            options.transformations?.takeIf { it.isNotEmpty() }?.let {
                addTransformations(it)
            }
            if (this.resultCachePolicy == null) {
                this.resultCachePolicy = options.resultCachePolicy
            }

            if (this.placeholder == null) {
                this.placeholder = options.placeholder
            }
            if (this.fallback == null) {
                this.fallback = options.fallback
            }
            if (this.error == null) {
                this.error = options.error
            }
            if (this.transitionFactory == null) {
                this.transitionFactory = options.transitionFactory
            }
            if (this.resizeOnDraw == null) {
                this.resizeOnDraw = options.resizeOnDraw
            }
            if (this.allowNullImage == null) {
                this.allowNullImage = options.allowNullImage
            }
            if (this.memoryCachePolicy == null) {
                this.memoryCachePolicy = options.memoryCachePolicy
            }

            componentRegistry = componentRegistry.merged(options.componentRegistry)
        }


        fun build(): ImageOptions {
            val extras = extrasBuilder?.build()?.takeIf { it.isNotEmpty() }
            val transformations = transformations?.takeIf { it.isNotEmpty() }
            return ImageOptions(
                depthHolder = depthHolder,
                extras = extras,
                downloadCachePolicy = downloadCachePolicy,
                resultCachePolicy = resultCachePolicy,
                colorType = colorType,
                colorSpace = colorSpace,
                sizeResolver = sizeResolver,
                sizeMultiplier = sizeMultiplier,
                precisionDecider = precisionDecider,
                scaleDecider = scaleDecider,
                transformations = transformations,
                placeholder = placeholder,
                fallback = fallback,
                error = error,
                transitionFactory = transitionFactory,
                resizeOnDraw = resizeOnDraw,
                allowNullImage = allowNullImage,
                memoryCachePolicy = memoryCachePolicy,
                componentRegistry = componentRegistry,
            )
        }
    }
}

/**
 * Returns true as long as any property is not empty
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageOptionsTest.testIsEmpty
 */
fun ImageOptions.isNotEmpty(): Boolean = !isEmpty()