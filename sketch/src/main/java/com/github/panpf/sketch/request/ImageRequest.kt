package com.github.panpf.sketch.request

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.DefaultSizeResolver
import com.github.panpf.sketch.resize.DisplaySizeResolver
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.ViewSizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.resize.fixedScale
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.DisplayListenerProvider
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.getLifecycle

/**
 * An immutable image request that contains all the required parameters,
 * you need to use its three concrete implementations [DisplayRequest], [LoadRequest], [DownloadRequest]
 */
interface ImageRequest {

    val context: Context
    val uriString: String
    val listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
    val progressListener: ProgressListener<ImageRequest>?
    val target: Target?
    val lifecycle: Lifecycle
    val uri: Uri
    val key: String

    /** Used to cache bitmaps in memory and on disk */
    val cacheKey: String
    val definedOptions: ImageOptions
    val defaultOptions: ImageOptions?

    /**
     * The processing depth of the request.
     */
    val depth: Depth

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
     */
    val httpHeaders: HttpHeaders?

    /**
     * Http download cache policy
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

    /** The size of the desired bitmap */
    val resize: Resize?

    /**
     * The size of the Bitmap expected to be finally loaded into memory is also affected by [resizePrecisionDecider] and [resizeScaleDecider]
     *
     * Applied to [android.graphics.BitmapFactory.Options.inSampleSize]
     */
    val resizeSize: Size?

    /**
     * Lazy calculation of resize size. If resizeSize is null at runtime, size is calculated and assigned to resizeSize
     */
    val resizeSizeResolver: SizeResolver?

    /**
     * Decide what Precision to use with [resizeSize] to calculate the size of the final Bitmap
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
     * @see com.github.panpf.sketch.decode.internal.applyExifOrientation
     */
    val ignoreExifOrientation: Boolean

    /**
     * Disk caching policy for Bitmaps affected by [resizeSize] or [transformations]
     *
     * @see com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
     */
    val resultCachePolicy: CachePolicy


    /**
     * Placeholder image when loading
     */
    val placeholder: StateImage?

    /**
     * Image to display when loading fails
     */
    val error: ErrorStateImage?

    /**
     * How the current image and the new image transition
     */
    val transition: Transition.Factory?

    /**
     * Disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
     */
    val disallowAnimatedImage: Boolean

    /**
     * Wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSize]
     */
    val resizeApplyToDrawable: Boolean

    /**
     * Bitmap memory caching policy
     */
    val memoryCachePolicy: CachePolicy

    abstract class BaseImageRequest : ImageRequest {
        override val uri: Uri by lazy { Uri.parse(uriString) }

        override val key: String by lazy { newKey() }

        /** Used to cache bitmaps in memory and on disk */
        override val cacheKey: String by lazy { newCacheKey() }

        override val resize: Resize? by lazy {
            resizeSize?.takeIf { it.width > 0 && it.height > 0 }?.let {
                Resize(
                    width = it.width, height = it.height,
                    precision = resizePrecisionDecider,
                    scale = resizeScaleDecider
                )
            }
        }
    }

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
        private var progressListener: ProgressListener<ImageRequest>? = null
        private var target: Target? = null
        private var lifecycle: Lifecycle? = null
        private var defaultOptions: ImageOptions? = null
        private var viewTargetOptions: ImageOptions? = null
        private val definedOptionsBuilder: ImageOptions.Builder
        private var resizeSizeResolver: SizeResolver? = null

        protected constructor(context: Context, uriString: String?) {
            this.context = context
            this.uriString = uriString.orEmpty()
            this.definedOptionsBuilder = ImageOptions.Builder()
        }

        protected constructor(request: ImageRequest) {
            this.context = request.context
            this.uriString = request.uriString
            this.listener = request.listener
                .asOrNull<CombinedListener<ImageRequest, ImageResult.Success, ImageResult.Error>>()
                ?.fromBuilderListener
                ?: request.listener
            this.progressListener = request.progressListener
                .asOrNull<CombinedProgressListener<ImageRequest>>()
                ?.fromBuilderProgressListener
                ?: request.progressListener
            this.target = request.target
            this.lifecycle = request.lifecycle
            this.defaultOptions = request.defaultOptions
            this.definedOptionsBuilder = request.definedOptions.newBuilder()
            this.resizeSizeResolver = request.resizeSizeResolver
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
            this.lifecycle = lifecycle
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
         */
        open fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set [Bitmap.Config] to use when creating the bitmap.
         * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
         */
        open fun bitmapConfig(bitmapConfig: Bitmap.Config): Builder = apply {
            definedOptionsBuilder.bitmapConfig(bitmapConfig)
        }

        /**
         * Set preferred [Bitmap]'s [ColorSpace]
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
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        open fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean? = true): Builder =
            apply {
                @Suppress("DEPRECATION")
                definedOptionsBuilder.preferQualityOverSpeed(inPreferQualityOverSpeed)
            }

        /**
         * Set how to resize image
         */
        open fun resize(resize: Resize?): Builder = apply {
            definedOptionsBuilder.resize(resize)
        }

        /**
         * Set how to resize image
         *
         * @param size Expected Bitmap size
         * @param precision precision of size, default is [Precision.EXACTLY]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        open fun resize(
            size: Size,
            precision: PrecisionDecider = fixedPrecision(EXACTLY),
            scale: ScaleDecider = fixedScale(CENTER_CROP)
        ): Builder = apply {
            definedOptionsBuilder.resize(size, precision, scale)
        }

        /**
         * Set how to resize image
         *
         * @param size Expected Bitmap size
         * @param precision precision of size, default is [Precision.EXACTLY]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        open fun resize(
            size: Size,
            precision: Precision = EXACTLY,
            scale: Scale = CENTER_CROP
        ): Builder = apply {
            definedOptionsBuilder.resize(size, precision, scale)
        }

        /**
         * Set how to resize image. precision is [Precision.EXACTLY], scale is [Scale.CENTER_CROP]
         */
        open fun resize(size: Size): Builder = apply {
            definedOptionsBuilder.resize(size)
        }

        /**
         * Set how to resize image
         *
         * @param width Expected Bitmap width
         * @param height Expected Bitmap height
         * @param precision precision of size, default is [Precision.EXACTLY]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        open fun resize(
            @Px width: Int,
            @Px height: Int,
            precision: PrecisionDecider = fixedPrecision(EXACTLY),
            scale: ScaleDecider = fixedScale(CENTER_CROP)
        ): Builder = apply {
            definedOptionsBuilder.resize(width, height, precision, scale)
        }

        /**
         * Set how to resize image
         *
         * @param width Expected Bitmap width
         * @param height Expected Bitmap height
         * @param precision precision of size, default is [Precision.EXACTLY]
         * @param scale Which part of the original image to keep when [precision] is
         * [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO], default is [Scale.CENTER_CROP]
         */
        open fun resize(
            @Px width: Int,
            @Px height: Int,
            precision: Precision = EXACTLY,
            scale: Scale = CENTER_CROP
        ): Builder = apply {
            definedOptionsBuilder.resize(width, height, precision, scale)
        }

        /**
         * Set how to resize image. precision is [Precision.EXACTLY], scale is [Scale.CENTER_CROP]
         *
         * @param width Expected Bitmap width
         * @param height Expected Bitmap height
         */
        open fun resize(@Px width: Int, @Px height: Int): Builder = apply {
            definedOptionsBuilder.resize(width, height)
        }

        /**
         * Set the resize size
         */
        open fun resizeSize(size: Size?): Builder = apply {
            definedOptionsBuilder.resizeSize(size)
        }

        /**
         * Set the resize size
         */
        open fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            definedOptionsBuilder.resizeSize(width, height)
        }

        /**
         * Set the [SizeResolver] to lazy resolve the requested size.
         */
        open fun resizeSizeResolver(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
        }

        /**
         * Set the resize precision
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
         */
        open fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            definedOptionsBuilder.resizeScale(scaleDecider)
        }

        /**
         * Set the resize scale
         */
        open fun resizeScale(scale: Scale): Builder = apply {
            definedOptionsBuilder.resizeScale(scale)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        open fun transformations(transformations: List<Transformation>?): Builder = apply {
            definedOptionsBuilder.transformations(transformations)
        }

        /**
         * Set the list of [Transformation]s to be applied to this request.
         */
        open fun transformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.transformations(transformations.toList())
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        open fun addTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations)
        }

        /**
         * Append the list of [Transformation]s to be applied to this request.
         */
        open fun addTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.addTransformations(transformations.toList())
        }

        /**
         * Bulk remove from current [Transformation] list
         */
        open fun removeTransformations(transformations: List<Transformation>): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations)
        }

        /**
         * Bulk remove from current [Transformation] list
         */
        open fun removeTransformations(vararg transformations: Transformation): Builder = apply {
            definedOptionsBuilder.removeTransformations(transformations.toList())
        }

        /**
         * Set disallow the use of [BitmapFactory.Options.inBitmap] to reuse Bitmap
         */
        open fun disallowReuseBitmap(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowReuseBitmap(disabled)
        }

        /**
         * Set ignore Orientation property in file Exif info
         */
        open fun ignoreExifOrientation(ignore: Boolean? = true): Builder = apply {
            definedOptionsBuilder.ignoreExifOrientation(ignore)
        }

        /**
         * Set disk caching policy for Bitmaps affected by [resizeSize] or [transformations]
         */
        open fun resultCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.resultCachePolicy(cachePolicy)
        }


        /**
         * Set placeholder image when loading
         */
        open fun placeholder(stateImage: StateImage?): Builder = apply {
            definedOptionsBuilder.placeholder(stateImage)
        }

        /**
         * Set Drawable placeholder image when loading
         */
        open fun placeholder(drawable: Drawable): Builder = apply {
            definedOptionsBuilder.placeholder(drawable)
        }

        /**
         * Set Drawable res placeholder image when loading
         */
        open fun placeholder(@DrawableRes drawableResId: Int): Builder = apply {
            definedOptionsBuilder.placeholder(drawableResId)
        }

        /**
         * Set image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
         */
        open fun error(
            defaultStateImage: StateImage?, configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(defaultStateImage, configBlock)
        }

        /**
         * Set Drawable image to display when loading fails.
         *
         * You can also set image of different error types via the trailing lambda function
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
         */
        open fun error(
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            definedOptionsBuilder.error(configBlock)
        }

        /**
         * Set the transition between the current image and the new image
         */
        open fun transition(transition: Transition.Factory?): Builder = apply {
            definedOptionsBuilder.transition(transition)
        }

        /**
         * Sets the transition that crossfade
         */
        open fun crossfade(
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            preferExactIntrinsicSize: Boolean = false,
            alwaysUse: Boolean = false,
        ): Builder = apply {
            definedOptionsBuilder.crossfade(durationMillis, preferExactIntrinsicSize, alwaysUse)
        }

        /**
         * Set disallow decode animation image, animations such as gif will only decode their first frame and return BitmapDrawable
         */
        open fun disallowAnimatedImage(disabled: Boolean? = true): Builder = apply {
            definedOptionsBuilder.disallowAnimatedImage(disabled)
        }

        /**
         * Set wrap the final [Drawable] use [ResizeDrawable] and resize, the size of [ResizeDrawable] is the same as [resizeSize]
         */
        open fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean? = true): Builder = apply {
            definedOptionsBuilder.resizeApplyToDrawable(resizeApplyToDrawable)
        }

        /**
         * Set bitmap memory caching policy
         */
        open fun memoryCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            definedOptionsBuilder.memoryCachePolicy(cachePolicy)
        }


        /**
         * Merge the specified [ImageOptions] into the current [Builder]. Currently [Builder] takes precedence
         */
        open fun merge(options: ImageOptions?): Builder = apply {
            definedOptionsBuilder.merge(options)
        }

        /**
         * Set a final [ImageOptions] to complement properties not set
         */
        open fun default(options: ImageOptions?): Builder = apply {
            this.defaultOptions = options
        }


        @SuppressLint("NewApi")
        open fun build(): ImageRequest {
            val listener = combinationListener()
            val progressListener = combinationProgressListener()
            val lifecycle = lifecycle ?: resolveLifecycle() ?: GlobalLifecycle
            definedOptionsBuilder.merge(viewTargetOptions)
            val definedOptions = definedOptionsBuilder.build()
            val finalOptions = definedOptionsBuilder.merge(defaultOptions).build()
            val depth = finalOptions.depth ?: NETWORK
            val parameters = finalOptions.parameters
            val httpHeaders = finalOptions.httpHeaders
            val downloadCachePolicy = finalOptions.downloadCachePolicy ?: ENABLED
            val resultCachePolicy = finalOptions.resultCachePolicy ?: ENABLED
            val bitmapConfig = finalOptions.bitmapConfig
            val colorSpace =
                if (VERSION.SDK_INT >= VERSION_CODES.O) finalOptions.colorSpace else null
            @Suppress("DEPRECATION") val preferQualityOverSpeed =
                finalOptions.preferQualityOverSpeed ?: false
            val resizeSize = finalOptions.resizeSize
            val resizeSizeResolver = resizeSizeResolver ?: if (resizeSize == null) {
                DefaultSizeResolver(resolveResizeSizeResolver())
            } else {
                null
            }
            val resizePrecisionDecider = finalOptions.resizePrecisionDecider
                ?: if (resizeSize == null || resizeSizeResolver is DefaultSizeResolver) {
                    fixedPrecision(LESS_PIXELS)
                } else {
                    fixedPrecision(EXACTLY)
                }
            val resizeScaleDecider =
                finalOptions.resizeScaleDecider ?: fixedScale(resolveResizeScale())
            val transformations = finalOptions.transformations
            val disallowReuseBitmap = finalOptions.disallowReuseBitmap ?: false
            val ignoreExifOrientation = finalOptions.ignoreExifOrientation ?: false
            val placeholder = finalOptions.placeholder
            val error = finalOptions.error
            val transition = finalOptions.transition
            val disallowAnimatedImage = finalOptions.disallowAnimatedImage ?: false
            val resizeApplyToDrawable = finalOptions.resizeApplyToDrawable ?: false
            val memoryCachePolicy = finalOptions.memoryCachePolicy ?: ENABLED

            return when (this@Builder) {
                is DisplayRequest.Builder -> {
                    DisplayRequest.DisplayRequestImpl(
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
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
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disallowReuseBitmap = disallowReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        placeholder = placeholder,
                        error = error,
                        transition = transition,
                        disallowAnimatedImage = disallowAnimatedImage,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                        memoryCachePolicy = memoryCachePolicy,
                    )
                }
                is LoadRequest.Builder -> {
                    LoadRequest.LoadRequestImpl(
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
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
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disallowReuseBitmap = disallowReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        placeholder = placeholder,
                        error = error,
                        transition = transition,
                        disallowAnimatedImage = disallowAnimatedImage,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                        memoryCachePolicy = memoryCachePolicy,
                    )
                }
                is DownloadRequest.Builder -> {
                    DownloadRequest.DownloadRequestImpl(
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
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
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disallowReuseBitmap = disallowReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        placeholder = placeholder,
                        error = error,
                        transition = transition,
                        disallowAnimatedImage = disallowAnimatedImage,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                        memoryCachePolicy = memoryCachePolicy,
                    )
                }
                else -> throw UnsupportedOperationException("Unsupported ImageRequest.Builder: ${this@Builder::class.java}")
            }
        }

        private fun resolveResizeSizeResolver(): SizeResolver {
            val target = target
            return if (target is ViewDisplayTarget<*>) {
                ViewSizeResolver(target.view)
            } else {
                DisplaySizeResolver(context)
            }
        }


        private fun resolveLifecycle(): Lifecycle? =
            (target.asOrNull<ViewDisplayTarget<*>>()?.view?.context ?: context).getLifecycle()

        private fun resolveResizeScale(): Scale =
            target.asOrNull<ViewDisplayTarget<*>>()
                ?.view?.asOrNull<ImageView>()
                ?.scaleType?.let {
                    when (it) {
                        ScaleType.FIT_START -> START_CROP
                        ScaleType.FIT_CENTER -> CENTER_CROP
                        ScaleType.FIT_END -> END_CROP
                        ScaleType.CENTER_INSIDE -> CENTER_CROP
                        ScaleType.CENTER -> CENTER_CROP
                        ScaleType.CENTER_CROP -> CENTER_CROP
                        else -> Scale.FILL
                    }
                } ?: CENTER_CROP

        private fun combinationListener(): Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? {
            val target = target
            val listener = listener
            val viewDisplayListenerProvider =
                target.asOrNull<ViewDisplayTarget<*>>()?.view?.asOrNull<DisplayListenerProvider>()
            @Suppress("UNCHECKED_CAST") val viewListener =
                viewDisplayListenerProvider?.getDisplayListener() as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
            return if (listener != null && viewListener != null && listener !== viewListener) {
                CombinedListener(viewListener, listener)
            } else {
                listener ?: viewListener
            }
        }

        private fun combinationProgressListener(): ProgressListener<ImageRequest>? {
            val target = target
            val progressListener = progressListener
            val viewDisplayListenerProvider =
                target.asOrNull<ViewDisplayTarget<*>>()?.view?.asOrNull<DisplayListenerProvider>()
            @Suppress("UNCHECKED_CAST") val viewProgressListener =
                viewDisplayListenerProvider?.getDisplayProgressListener() as ProgressListener<ImageRequest>?
            return if (progressListener != null && viewProgressListener != null && progressListener != viewProgressListener) {
                CombinedProgressListener(viewProgressListener, progressListener)
            } else {
                progressListener ?: viewProgressListener
            }
        }
    }
}