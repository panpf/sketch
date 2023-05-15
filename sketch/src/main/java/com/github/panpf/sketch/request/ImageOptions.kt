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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.isNotEmpty
import com.github.panpf.sketch.http.merged
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import java.util.LinkedList

const val DEPTH_FROM_KEY = "sketch#depth_from"

/**
 * Build and set the [ImageOptions]
 */
fun ImageOptions(
    configBlock: (ImageOptions.Builder.() -> Unit)? = null
): ImageOptions = ImageOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()

/**
 * Stores parameters required to download, load, display images
 */
interface ImageOptions {

    /**
     * The processing depth of the request.
     */
    val depth: Depth?

    /**
     * where does this depth come from
     */
    val depthFrom: String?
        get() = parameters?.value(DEPTH_FROM_KEY)

    /**
     * A map of generic values that can be used to pass custom data to [Fetcher] and [BitmapDecoder] and [DrawableDecoder].
     */
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
    val downloadCachePolicy: CachePolicy?


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
    val preferQualityOverSpeed: Boolean?

    /**
     * Lazy calculation of resize size. If resize size is null at runtime, size is calculated and assigned to resizeSize
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val resizeSizeResolver: SizeResolver?

    /**
     * Decide what Precision to use with [resizeSizeResolver] to calculate the size of the final Bitmap
     */
    val resizePrecisionDecider: PrecisionDecider?

    /**
     * Which part of the original image to keep when [resizePrecisionDecider] returns [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     */
    val resizeScaleDecider: ScaleDecider?

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
    val disallowReuseBitmap: Boolean?

    /**
     * Ignore Orientation property in file Exif info
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     *
     * @see com.github.panpf.sketch.decode.internal.appliedExifOrientation
     */
    val ignoreExifOrientation: Boolean?

    /**
     * Disk caching policy for Bitmaps affected by [resizeSizeResolver] or [transformations]
     *
     * Only works on [LoadRequest] and [DisplayRequest]
     *
     * @see com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy?


    /**
     * Placeholder image when loading
     *
     * Only works on [DisplayRequest]
     */
    val placeholder: StateImage?

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
    val disallowAnimatedImage: Boolean?

    /**
     * Wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSizeResolver]
     *
     * Only works on [DisplayRequest]
     */
    val resizeApplyToDrawable: Boolean?

    /**
     * Bitmap memory caching policy
     *
     * Only works on [DisplayRequest]
     *
     * @see com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
     */
    val memoryCachePolicy: CachePolicy?


    /**
     * Components that are only valid for the current request
     *
     * Only works on [DisplayRequest]
     */
    val componentRegistry: ComponentRegistry?


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
    @Suppress("DEPRECATION")
    fun isEmpty(): Boolean =
        depth == null
                && parameters?.isEmpty() != false
                && httpHeaders?.isEmpty() != false
                && downloadCachePolicy == null
                && bitmapConfig == null
                && (VERSION.SDK_INT < VERSION_CODES.O || colorSpace == null)
                && preferQualityOverSpeed == null
                && resizeSizeResolver == null
                && resizePrecisionDecider == null
                && resizeScaleDecider == null
                && transformations == null
                && disallowReuseBitmap == null
                && ignoreExifOrientation == null
                && resultCachePolicy == null
                && placeholder == null
                && error == null
                && transitionFactory == null
                && disallowAnimatedImage == null
                && resizeApplyToDrawable == null
                && memoryCachePolicy == null
                && componentRegistry == null

    class Builder {

        private var depth: Depth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeadersBuilder: HttpHeaders.Builder? = null
        private var downloadCachePolicy: CachePolicy? = null

        private var bitmapConfig: BitmapConfig? = null
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resizeSizeResolver: SizeResolver? = null
        private var resizePrecisionDecider: PrecisionDecider? = null
        private var resizeScaleDecider: ScaleDecider? = null
        private var transformations: MutableList<Transformation>? = null
        private var disallowReuseBitmap: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var resultCachePolicy: CachePolicy? = null

        private var placeholder: StateImage? = null
        private var error: ErrorStateImage? = null
        private var transitionFactory: Transition.Factory? = null
        private var disallowAnimatedImage: Boolean? = null
        private var resizeApplyToDrawable: Boolean? = null
        private var memoryCachePolicy: CachePolicy? = null

        private var componentRegistry: ComponentRegistry? = null

        constructor()

        internal constructor(request: ImageOptions) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()

            this.httpHeadersBuilder = request.httpHeaders?.newBuilder()
            this.downloadCachePolicy = request.downloadCachePolicy

            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            @Suppress("DEPRECATION")
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resizeSizeResolver = request.resizeSizeResolver
            this.resizePrecisionDecider = request.resizePrecisionDecider
            this.resizeScaleDecider = request.resizeScaleDecider
            this.transformations = request.transformations?.toMutableList()
            this.disallowReuseBitmap = request.disallowReuseBitmap
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.resultCachePolicy = request.resultCachePolicy

            this.placeholder = request.placeholder
            this.error = request.error
            this.transitionFactory = request.transitionFactory
            this.disallowAnimatedImage = request.disallowAnimatedImage
            this.resizeApplyToDrawable = request.resizeApplyToDrawable
            this.memoryCachePolicy = request.memoryCachePolicy

            this.componentRegistry = request.componentRegistry
        }


        /**
         * Set the requested depth
         */
        fun depth(depth: Depth?, depthFrom: String? = null): Builder = apply {
            this.depth = depth
            if (depth != null && depthFrom != null) {
                setParameter(DEPTH_FROM_KEY, depthFrom, null)
            } else {
                removeParameter(DEPTH_FROM_KEY)
            }
        }


        /**
         * Bulk set parameters for this request
         */
        fun parameters(parameters: Parameters?): Builder = apply {
            this.parametersBuilder = parameters?.newBuilder()
        }

        /**
         * Set a parameter for this request.
         */
        fun setParameter(
            key: String, value: Any?, cacheKey: String? = value?.toString()
        ): Builder = apply {
            this.parametersBuilder = (this.parametersBuilder ?: Parameters.Builder()).apply {
                set(key, value, cacheKey)
            }
        }

        /**
         * Remove a parameter from this request.
         */
        fun removeParameter(key: String): Builder = apply {
            this.parametersBuilder?.remove(key)
        }


        /**
         * Bulk set headers for any network request for this request
         */
        fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            this.httpHeadersBuilder = httpHeaders?.newBuilder()
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        fun addHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeadersBuilder = (this.httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                add(name, value)
            }
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        fun setHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeadersBuilder = (this.httpHeadersBuilder ?: HttpHeaders.Builder()).apply {
                set(name, value)
            }
        }

        /**
         * Remove all network headers with the key [name].
         */
        fun removeHttpHeader(name: String): Builder = apply {
            this.httpHeadersBuilder?.removeAll(name)
        }

        /**
         * Set http download cache policy
         */
        fun downloadCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            this.downloadCachePolicy = cachePolicy
        }

        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         */
        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         */
        fun bitmapConfig(bitmapConfig: Bitmap.Config): Builder =
            bitmapConfig(BitmapConfig(bitmapConfig))

        /**
         * Set preferred [Bitmap]'s [ColorSpace]
         */
        @RequiresApi(VERSION_CODES.O)
        fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
            this.colorSpace = colorSpace
        }

        /**
         * From Android N (API 24), this is ignored. The output will always be high quality.
         *
         * In [android.os.Build.VERSION_CODES.M] and below, if
         * inPreferQualityOverSpeed is set to true, the decoder will try to
         * decode the reconstructed image to a higher quality even at the
         * expense of the decoding speed. Currently the field only affects JPEG
         * decode, in the case of which a more accurate, but slightly slower,
         * IDCT method will be used instead.
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): Builder = apply {
            this.preferQualityOverSpeed = inPreferQualityOverSpeed
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
            this.resizeSizeResolver = size
            this.resizePrecisionDecider = precision
            this.resizeScaleDecider = scale
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
            FixedSizeResolver(size),
            precision?.let { FixedPrecisionDecider(it) },
            scale?.let { FixedScaleDecider(it) }
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
            @Px width: Int,
            @Px height: Int,
            precision: Precision? = null,
            scale: Scale? = null
        ): Builder = resize(
            FixedSizeResolver(width, height),
            precision?.let { FixedPrecisionDecider(it) },
            scale?.let { FixedScaleDecider(it) }
        )

        /**
         * Set the [SizeResolver] to lazy resolve the requested size.
         *
         * Only works on [LoadRequest] and [DisplayRequest]
         */
        fun resizeSize(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
        }

        /**
         * Set the resize size
         */
        fun resizeSize(resizeSize: Size): Builder =
            resizeSize(FixedSizeResolver(resizeSize))

        /**
         * Set the resize size
         */
        fun resizeSize(@Px width: Int, @Px height: Int): Builder =
            resizeSize(FixedSizeResolver(width, height))

        /**
         * Set the resize precision, default is [Precision.LESS_PIXELS]
         */
        fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            this.resizePrecisionDecider = precisionDecider
        }

        /**
         * Set the resize precision, default is [Precision.LESS_PIXELS]
         */
        fun resizePrecision(precision: Precision): Builder =
            resizePrecision(FixedPrecisionDecider(precision))

        /**
         * Set the resize scale, default is [Scale.CENTER_CROP]
         */
        fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            this.resizeScaleDecider = scaleDecider
        }

        /**
         * Set the resize scale, default is [Scale.CENTER_CROP]
         */
        fun resizeScale(scale: Scale): Builder = resizeScale(FixedScaleDecider(scale))

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
            this.transformations = (this.transformations ?: LinkedList()).apply {
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
         * Set disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
         */
        fun disallowReuseBitmap(disabled: Boolean? = true): Builder = apply {
            this.disallowReuseBitmap = disabled
        }

        /**
         * Set ignore Orientation property in file Exif info
         */
        fun ignoreExifOrientation(ignore: Boolean? = true): Builder = apply {
            this.ignoreExifOrientation = ignore
        }

        /**
         * Set disk caching policy for Bitmaps affected by [resizeSize] or [transformations]
         */
        fun resultCachePolicy(cachePolicy: CachePolicy?): Builder =
            apply {
                this.resultCachePolicy = cachePolicy
            }


        /**
         * Set placeholder image when loading
         */
        fun placeholder(stateImage: StateImage?): Builder = apply {
            this.placeholder = stateImage
        }

        /**
         * Set Drawable placeholder image when loading
         */
        fun placeholder(drawable: Drawable): Builder =
            placeholder(DrawableStateImage(drawable))

        /**
         * Set Drawable res placeholder image when loading
         */
        fun placeholder(@DrawableRes drawableResId: Int): Builder =
            placeholder(DrawableStateImage(drawableResId))

        /**
         * Set image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultStateImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.error = ErrorStateImage(defaultStateImage, configBlock)
                .takeIf { it.matcherList.isNotEmpty() }
        }

        /**
         * Set Drawable image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultDrawable: Drawable,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = error(DrawableStateImage(defaultDrawable), configBlock)

        /**
         * Set Drawable res image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            defaultDrawableResId: Int,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = error(DrawableStateImage(defaultDrawableResId), configBlock)

        /**
         * Set image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        fun error(
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = error(null, configBlock)

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
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            fadeStart: Boolean = true,
            preferExactIntrinsicSize: Boolean = false,
            alwaysUse: Boolean = false,
        ): Builder = apply {
            transitionFactory(
                CrossfadeTransition.Factory(
                    durationMillis = durationMillis,
                    fadeStart = fadeStart,
                    preferExactIntrinsicSize = preferExactIntrinsicSize,
                    alwaysUse = alwaysUse
                )
            )
        }

        /**
         * Set disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
         */
        fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            this.disallowAnimatedImage = disabled
        }

        /**
         * Set wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSize]
         */
        fun resizeApplyToDrawable(apply: Boolean? = true): Builder = apply {
            this.resizeApplyToDrawable = apply
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
         * Merge the specified [ImageOptions] into the current [Builder]. Currently [Builder] takes precedence
         */
        fun merge(options: ImageOptions?): Builder = apply {
            if (options == null) return@apply

            if (this.depth == null) {
                this.depth = options.depth
            }
            options.parameters?.let {
                parametersBuilder = parametersBuilder?.build().merged(it)?.newBuilder()
            }

            options.httpHeaders?.let {
                httpHeadersBuilder = httpHeadersBuilder?.build().merged(it)?.newBuilder()
            }
            if (this.downloadCachePolicy == null) {
                this.downloadCachePolicy = options.downloadCachePolicy
            }

            if (this.bitmapConfig == null) {
                this.bitmapConfig = options.bitmapConfig
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O && this.colorSpace == null) {
                this.colorSpace = options.colorSpace
            }
            if (this.preferQualityOverSpeed == null) {
                @Suppress("DEPRECATION")
                this.preferQualityOverSpeed = options.preferQualityOverSpeed
            }
            if (this.resizeSizeResolver == null) {
                this.resizeSizeResolver = options.resizeSizeResolver
            }
            if (this.resizePrecisionDecider == null) {
                this.resizePrecisionDecider = options.resizePrecisionDecider
            }
            if (this.resizeScaleDecider == null) {
                this.resizeScaleDecider = options.resizeScaleDecider
            }
            options.transformations?.takeIf { it.isNotEmpty() }?.let {
                addTransformations(it)
            }
            if (this.disallowReuseBitmap == null) {
                this.disallowReuseBitmap = options.disallowReuseBitmap
            }
            if (this.ignoreExifOrientation == null) {
                this.ignoreExifOrientation = options.ignoreExifOrientation
            }
            if (this.resultCachePolicy == null) {
                this.resultCachePolicy = options.resultCachePolicy
            }

            if (this.placeholder == null) {
                this.placeholder = options.placeholder
            }
            if (this.error == null) {
                this.error = options.error
            }
            if (this.transitionFactory == null) {
                this.transitionFactory = options.transitionFactory
            }
            if (this.disallowAnimatedImage == null) {
                this.disallowAnimatedImage = options.disallowAnimatedImage
            }
            if (this.resizeApplyToDrawable == null) {
                this.resizeApplyToDrawable = options.resizeApplyToDrawable
            }
            if (this.memoryCachePolicy == null) {
                this.memoryCachePolicy = options.memoryCachePolicy
            }

            componentRegistry = componentRegistry.merged(options.componentRegistry)
        }


        @SuppressLint("NewApi")
        fun build(): ImageOptions = ImageOptionsImpl(
            depth = depth,
            parameters = parametersBuilder?.build()?.takeIf { it.isNotEmpty() },
            httpHeaders = httpHeadersBuilder?.build()?.takeIf { it.isNotEmpty() },
            downloadCachePolicy = downloadCachePolicy,
            resultCachePolicy = resultCachePolicy,
            bitmapConfig = bitmapConfig,
            colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resizeSizeResolver = resizeSizeResolver,
            resizePrecisionDecider = resizePrecisionDecider,
            resizeScaleDecider = resizeScaleDecider,
            transformations = transformations?.takeIf { it.isNotEmpty() },
            disallowReuseBitmap = disallowReuseBitmap,
            ignoreExifOrientation = ignoreExifOrientation,
            placeholder = placeholder,
            error = error,
            transitionFactory = transitionFactory,
            disallowAnimatedImage = disallowAnimatedImage,
            resizeApplyToDrawable = resizeApplyToDrawable,
            memoryCachePolicy = memoryCachePolicy,
            componentRegistry = componentRegistry,
        )
    }

    class ImageOptionsImpl(
        override val depth: Depth?,
        override val parameters: Parameters?,

        override val httpHeaders: HttpHeaders?,
        override val downloadCachePolicy: CachePolicy?,

        override val bitmapConfig: BitmapConfig?,
        @get:RequiresApi(VERSION_CODES.O)
        override val colorSpace: ColorSpace?,
        @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
        override val preferQualityOverSpeed: Boolean?,
        override val resizeSizeResolver: SizeResolver?,
        override val resizePrecisionDecider: PrecisionDecider?,
        override val resizeScaleDecider: ScaleDecider?,
        override val transformations: List<Transformation>?,
        override val disallowReuseBitmap: Boolean?,
        override val ignoreExifOrientation: Boolean?,
        override val resultCachePolicy: CachePolicy?,
        override val placeholder: StateImage?,
        override val error: ErrorStateImage?,
        override val transitionFactory: Transition.Factory?,
        override val disallowAnimatedImage: Boolean?,
        override val resizeApplyToDrawable: Boolean?,
        override val memoryCachePolicy: CachePolicy?,
        override val componentRegistry: ComponentRegistry?,
    ) : ImageOptions {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ImageOptionsImpl
            if (depth != other.depth) return false
            if (parameters != other.parameters) return false
            if (httpHeaders != other.httpHeaders) return false
            if (downloadCachePolicy != other.downloadCachePolicy) return false
            if (bitmapConfig != other.bitmapConfig) return false
            if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != other.colorSpace) return false
            @Suppress("DEPRECATION") if (preferQualityOverSpeed != other.preferQualityOverSpeed) return false
            if (resizeSizeResolver != other.resizeSizeResolver) return false
            if (resizePrecisionDecider != other.resizePrecisionDecider) return false
            if (resizeScaleDecider != other.resizeScaleDecider) return false
            if (transformations != other.transformations) return false
            if (disallowReuseBitmap != other.disallowReuseBitmap) return false
            if (ignoreExifOrientation != other.ignoreExifOrientation) return false
            if (resultCachePolicy != other.resultCachePolicy) return false
            if (placeholder != other.placeholder) return false
            if (error != other.error) return false
            if (transitionFactory != other.transitionFactory) return false
            if (disallowAnimatedImage != other.disallowAnimatedImage) return false
            if (resizeApplyToDrawable != other.resizeApplyToDrawable) return false
            if (memoryCachePolicy != other.memoryCachePolicy) return false
            if (componentRegistry != other.componentRegistry) return false
            return true
        }

        override fun hashCode(): Int {
            var result = depth?.hashCode() ?: 0
            result = 31 * result + (parameters?.hashCode() ?: 0)
            result = 31 * result + (httpHeaders?.hashCode() ?: 0)
            result = 31 * result + (downloadCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (bitmapConfig?.hashCode() ?: 0)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                result = 31 * result + (colorSpace?.hashCode() ?: 0)
            }
            @Suppress("DEPRECATION")
            result = 31 * result + (preferQualityOverSpeed?.hashCode() ?: 0)
            result = 31 * result + (resizeSizeResolver?.hashCode() ?: 0)
            result = 31 * result + (resizePrecisionDecider?.hashCode() ?: 0)
            result = 31 * result + (resizeScaleDecider?.hashCode() ?: 0)
            result = 31 * result + (transformations?.hashCode() ?: 0)
            result = 31 * result + (disallowReuseBitmap?.hashCode() ?: 0)
            result = 31 * result + (ignoreExifOrientation?.hashCode() ?: 0)
            result = 31 * result + (resultCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (placeholder?.hashCode() ?: 0)
            result = 31 * result + (error?.hashCode() ?: 0)
            result = 31 * result + (transitionFactory?.hashCode() ?: 0)
            result = 31 * result + (disallowAnimatedImage?.hashCode() ?: 0)
            result = 31 * result + (resizeApplyToDrawable?.hashCode() ?: 0)
            result = 31 * result + (memoryCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (componentRegistry?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return buildString {
                append("ImageOptionsImpl(")
                append("depth=$depth, ")
                append("parameters=$parameters, ")
                append("httpHeaders=$httpHeaders, ")
                append("downloadCachePolicy=$downloadCachePolicy, ")
                append("bitmapConfig=$bitmapConfig, ")
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    append("colorSpace=$colorSpace, ")
                }
                @Suppress("DEPRECATION")
                append("preferQualityOverSpeed=$preferQualityOverSpeed, ")
                append("resizeSizeResolver=$resizeSizeResolver, ")
                append("resizePrecisionDecider=$resizePrecisionDecider, ")
                append("resizeScaleDecider=$resizeScaleDecider, ")
                append("transformations=$transformations, ")
                append("disallowReuseBitmap=$disallowReuseBitmap, ")
                append("ignoreExifOrientation=$ignoreExifOrientation, ")
                append("resultCachePolicy=$resultCachePolicy, ")
                append("placeholder=$placeholder, ")
                append("error=$error, ")
                append("transition=$transitionFactory, ")
                append("disallowAnimatedImage=$disallowAnimatedImage, ")
                append("resizeApplyToDrawable=$resizeApplyToDrawable")
                append("memoryCachePolicy=$memoryCachePolicy, ")
                append("componentRegistry=$componentRegistry, ")
                append(")")
            }
        }
    }
}

/**
 * Returns true as long as any property is not empty
 */
fun ImageOptions.isNotEmpty(): Boolean = !isEmpty()