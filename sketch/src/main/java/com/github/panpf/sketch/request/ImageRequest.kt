package com.github.panpf.sketch.request

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.merge
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.internal.newKey
import com.github.panpf.sketch.resize.FixedPrecisionDecider
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
import com.github.panpf.sketch.resize.ScreenSizeResolver
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.ViewSizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.resize.fixedScale
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.stateimage.newErrorStateImage
import com.github.panpf.sketch.target.ListenerProvider
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.merge
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.getLifecycle

interface ImageRequest {

    companion object {
        const val REQUEST_DEPTH_FROM = "sketch#requestDepthFrom"
    }

    val sketch: Sketch
    val context: Context
    val uriString: String
    val listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
    val parameters: Parameters?
    val depth: RequestDepth
    val httpHeaders: HttpHeaders?
    val downloadDiskCachePolicy: CachePolicy
    val progressListener: ProgressListener<ImageRequest>?

    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    val bitmapConfig: BitmapConfig?

    val colorSpace: ColorSpace?

    /**
     * From Android N (API 24), this is ignored.  The output will always be high quality.
     *
     * In {@link android.os.Build.VERSION_CODES#M} and below, if
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
    val resizeSize: Size?
    val resizeSizeResolver: SizeResolver
    val resizePrecisionDecider: PrecisionDecider
    val resizeScaleDecider: ScaleDecider

    /** The list of [Transformation]s to be applied to this request. */
    val transformations: List<Transformation>?

    /** Disabled reuse of Bitmap from [BitmapPool] */
    val disabledReuseBitmap: Boolean

    /** Ignore exif orientation */
    val ignoreExifOrientation: Boolean

    /** @see com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheDecodeInterceptor */
    val bitmapResultDiskCachePolicy: CachePolicy
    val target: Target?
    val lifecycle: Lifecycle

    val disabledAnimatedImage: Boolean
    val bitmapMemoryCachePolicy: CachePolicy
    val placeholderImage: StateImage?
    val errorImage: StateImage?
    val transition: Transition.Factory?
    val resizeApplyToDrawable: Boolean?

    val definedOptions: ImageOptions
    val viewOptions: ImageOptions?
    val globalOptions: ImageOptions?

    val uri: Uri

    val key: String

    /** Used to cache bitmaps in memory and on disk */
    val cacheKey: String

    val depthFrom: String?
        get() = parameters?.value(REQUEST_DEPTH_FROM)

    val downloadDiskCacheKey: String
        get() = uriString

    val resize: Resize?

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

        override fun toString(): String = key
    }

    fun newBuilder(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): Builder

    fun newRequest(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): ImageRequest

    abstract class Builder {
        private val sketch: Sketch
        private val context: Context
        private val uriString: String
        private var listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? =
            null
        private var progressListener: ProgressListener<ImageRequest>? = null

        private var target: Target? = null
        private var lifecycle: Lifecycle? = null

        private var viewOptions: ImageOptions? = null
        private var globalOptions: ImageOptions? = null
        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null
        private var httpHeaders: HttpHeaders.Builder? = null
        private var downloadDiskCachePolicy: CachePolicy? = null
        private var bitmapConfig: BitmapConfig? = null
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resizeSize: Size? = null
        private var resizeSizeResolver: SizeResolver? = null
        private var resizePrecisionDecider: PrecisionDecider? = null
        private var resizeScaleDecider: ScaleDecider? = null
        private var transformations: MutableSet<Transformation>? = null
        private var disabledReuseBitmap: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null
        private var bitmapMemoryCachePolicy: CachePolicy? = null
        private var disabledAnimatedImage: Boolean? = null
        private var placeholderImage: StateImage? = null
        private var errorImage: StateImage? = null
        private var transition: Transition.Factory? = null
        private var resizeApplyToDrawable: Boolean? = null

        private var resolvedLifecycle: Lifecycle? = null
        private var resolvedResizeSizeResolver: SizeResolver? = null
        private var resolvedResizeScaleDecider: ScaleDecider? = null

        constructor(context: Context, uriString: String?) {
            this.context = context
            this.sketch = context.sketch
            this.uriString = uriString.orEmpty()
            this.globalOptions = sketch.globalImageOptions
        }

        internal constructor(context: Context, request: ImageRequest) {
            this.context = context
            this.sketch = request.sketch
            this.uriString = request.uriString
            this.listener =
                request.listener.asOrNull<CombinedListener<ImageRequest, ImageResult.Success, ImageResult.Error>>()?.fromBuilderListener
                    ?: request.listener
            this.progressListener =
                request.progressListener.asOrNull<CombinedProgressListener<ImageRequest>>()?.fromBuilderProgressListener
                    ?: request.progressListener

            this.target = request.target
            this.lifecycle = request.lifecycle

            this.viewOptions = request.viewOptions
            this.globalOptions = request.globalOptions
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()
            this.httpHeaders = request.httpHeaders?.newBuilder()
            this.downloadDiskCachePolicy = request.downloadDiskCachePolicy
            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) this.colorSpace = request.colorSpace
            @Suppress("DEPRECATION")
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resizeSize = request.resizeSize
            this.resizeSizeResolver = request.resizeSizeResolver
            this.resizePrecisionDecider = request.resizePrecisionDecider
            this.resizeScaleDecider = request.resizeScaleDecider
            this.transformations = request.transformations?.toMutableSet()
            this.disabledReuseBitmap = request.disabledReuseBitmap
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy
            this.bitmapMemoryCachePolicy = request.bitmapMemoryCachePolicy
            this.disabledAnimatedImage = request.disabledAnimatedImage
            this.placeholderImage = request.placeholderImage
            this.errorImage = request.errorImage
            this.transition = request.transition
            this.resizeApplyToDrawable = request.resizeApplyToDrawable

            // If the context changes, recompute the resolved values.
            if (request.context === context) {
                resolvedLifecycle = request.lifecycle
                resolvedResizeSizeResolver = request.resizeSizeResolver
                resolvedResizeScaleDecider = request.resizeScaleDecider
            } else {
                resolvedLifecycle = null
                resolvedResizeSizeResolver = null
                resolvedResizeScaleDecider = null
            }
        }

        internal fun listener(listener: Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?): Builder =
            apply {
                this.listener = listener
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        internal inline fun listener(
            crossinline onStart: (request: ImageRequest) -> Unit = {},
            crossinline onCancel: (request: ImageRequest) -> Unit = {},
            crossinline onError: (request: ImageRequest, result: ImageResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: ImageRequest, result: ImageResult.Success) -> Unit = { _, _ -> }
        ): Builder =
            listener(object : Listener<ImageRequest, ImageResult.Success, ImageResult.Error> {
                override fun onStart(request: ImageRequest) = onStart(request)
                override fun onCancel(request: ImageRequest) = onCancel(request)
                override fun onError(request: ImageRequest, result: ImageResult.Error) =
                    onError(request, result)

                override fun onSuccess(request: ImageRequest, result: ImageResult.Success) =
                    onSuccess(request, result)
            })

        internal fun progressListener(progressListener: ProgressListener<ImageRequest>?): Builder =
            apply {
                this.progressListener = progressListener
            }

        open fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            this.lifecycle = lifecycle
        }

        internal fun target(target: Target?): Builder = apply {
            this.target = target
            this.viewOptions = target.asOrNull<ViewTarget<*>>()
                ?.view.asOrNull<ImageOptionsProvider>()
                ?.displayImageOptions
        }

        open fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        open fun depthFrom(from: String?): Builder = apply {
            if (from != null) {
                setParameter(REQUEST_DEPTH_FROM, from, null)
            } else {
                removeParameter(REQUEST_DEPTH_FROM)
            }
        }

        open fun parameters(parameters: Parameters?): Builder = apply {
            this.parametersBuilder = parameters?.newBuilder()
        }

        /**
         * Set a parameter for this request.
         *
         * @see Parameters.Builder.set
         */
        @JvmOverloads
        open fun setParameter(
            key: String,
            value: Any?,
            cacheKey: String? = value?.toString()
        ): Builder =
            apply {
                this.parametersBuilder = (this.parametersBuilder ?: Parameters.Builder()).apply {
                    set(key, value, cacheKey)
                }
            }

        /**
         * Remove a parameter from this request.
         *
         * @see Parameters.Builder.remove
         */
        open fun removeParameter(key: String): Builder = apply {
            this.parametersBuilder?.remove(key)
        }

        open fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            this.httpHeaders = httpHeaders?.newBuilder()
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        open fun addHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                add(name, value)
            }
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        open fun setHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                set(name, value)
            }
        }

        /**
         * Remove all network headers with the key [name].
         */
        open fun removeHttpHeader(name: String): Builder = apply {
            this.httpHeaders?.removeAll(name)
        }

        open fun downloadDiskCachePolicy(downloadDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.downloadDiskCachePolicy = downloadDiskCachePolicy
            }

        open fun bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
            }

        open fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        open fun bitmapConfig(bitmapConfig: Bitmap.Config?): Builder = apply {
            this.bitmapConfig = if (bitmapConfig != null) BitmapConfig(bitmapConfig) else null
        }

        open fun lowQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.LOW_QUALITY
        }

        open fun middenQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.MIDDEN_QUALITY
        }

        open fun highQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.HIGH_QUALITY
        }

        @RequiresApi(VERSION_CODES.O)
        open fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
            this.colorSpace = colorSpace
        }

        /**
         * From Android N (API 24), this is ignored.  The output will always be high quality.
         *
         * In {@link android.os.Build.VERSION_CODES#M} and below, if
         * inPreferQualityOverSpeed is set to true, the decoder will try to
         * decode the reconstructed image to a higher quality even at the
         * expense of the decoding speed. Currently the field only affects JPEG
         * decode, in the case of which a more accurate, but slightly slower,
         * IDCT method will be used instead.
         *
         * Applied to [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
         */
        @Deprecated("From Android N (API 24), this is ignored.  The output will always be high quality.")
        open fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            if (VERSION.SDK_INT < VERSION_CODES.N) {
                this.preferQualityOverSpeed = inPreferQualityOverSpeed
            }
        }

        open fun resizeSize(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
            resetResolvedValues()
        }

        open fun resizeSize(size: Size?): Builder = apply {
            this.resizeSize = size
        }

        open fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            this.resizeSize = Size(width, height)
        }

        open fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            this.resizePrecisionDecider = precisionDecider
        }

        open fun resizePrecision(precision: Precision): Builder = apply {
            this.resizePrecisionDecider = FixedPrecisionDecider(precision)
        }

        open fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            this.resizeScaleDecider = scaleDecider
        }

        open fun resizeScale(scale: Scale): Builder = apply {
            this.resizeScaleDecider = fixedScale(scale)
        }

        open fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = transformations?.toMutableSet()
        }

        open fun transformations(vararg transformations: Transformation): Builder = apply {
            this.transformations = transformations.toMutableSet()
        }

        open fun addTransformations(transformations: List<Transformation>): Builder = apply {
            val newTransformations = transformations.filter { newTransformation ->
                this.transformations?.find { it.key == newTransformation.key } == null
            }
            this.transformations = (this.transformations ?: HashSet()).apply {
                addAll(newTransformations)
            }
        }

        open fun addTransformations(vararg transformations: Transformation): Builder = apply {
            addTransformations(transformations.toList())
        }

        open fun removeTransformations(removeTransformations: List<Transformation>): Builder =
            apply {
                this.transformations = this.transformations?.filter { oldTransformation ->
                    removeTransformations.find { it.key == oldTransformation.key } == null
                }?.toMutableSet()
            }

        open fun removeTransformations(vararg removeTransformations: Transformation): Builder =
            apply {
                removeTransformations(removeTransformations.toList())
            }

        open fun disabledReuseBitmap(disabledReuseBitmap: Boolean? = true): Builder = apply {
            this.disabledReuseBitmap = disabledReuseBitmap
        }

        open fun ignoreExifOrientation(ignoreExifOrientation: Boolean? = true): Builder =
            apply {
                this.ignoreExifOrientation = ignoreExifOrientation
            }

        open fun bitmapMemoryCachePolicy(bitmapMemoryCachePolicy: CachePolicy?): Builder = apply {
            this.bitmapMemoryCachePolicy = bitmapMemoryCachePolicy
        }

        open fun disabledAnimatedImage(disabledAnimatedImage: Boolean? = true): Builder =
            apply {
                this.disabledAnimatedImage = disabledAnimatedImage
            }

        open fun placeholder(placeholderImage: StateImage?): Builder = apply {
            this.placeholderImage = placeholderImage
        }

        open fun placeholder(placeholderDrawable: Drawable?): Builder = apply {
            this.placeholderImage =
                if (placeholderDrawable != null) DrawableStateImage(placeholderDrawable) else null
        }

        open fun placeholder(@DrawableRes placeholderDrawableResId: Int?): Builder = apply {
            this.placeholderImage = if (placeholderDrawableResId != null) {
                DrawableStateImage(placeholderDrawableResId)
            } else null
        }

        open fun error(
            errorImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorImage?.let {
                if (configBlock != null) {
                    newErrorStateImage(it, configBlock)
                } else {
                    it
                }
            }
        }

        open fun error(
            errorDrawable: Drawable?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorDrawable?.let {
                if (configBlock != null) {
                    newErrorStateImage(DrawableStateImage(it), configBlock)
                } else {
                    DrawableStateImage(it)
                }
            }
        }

        open fun error(
            errorDrawableResId: Int?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorDrawableResId?.let {
                if (configBlock != null) {
                    newErrorStateImage(DrawableStateImage(it), configBlock)
                } else {
                    DrawableStateImage(it)
                }
            }
        }

        open fun transition(transition: Transition.Factory?): Builder = apply {
            this.transition = transition
        }

        open fun crossfade(
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            preferExactIntrinsicSize: Boolean = false
        ): Builder = apply {
            transition(CrossfadeTransition.Factory(durationMillis, preferExactIntrinsicSize))
        }

        open fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean? = true): Builder = apply {
            this.resizeApplyToDrawable = resizeApplyToDrawable
        }

        @SuppressLint("NewApi")
        open fun build(): ImageRequest {
            val listener = combinationListener()
            val progressListener = combinationProgressListener()
            val lifecycle = lifecycle ?: resolvedLifecycle ?: resolveLifecycle() ?: GlobalLifecycle
            val definedOptions = ImageOptions {
                depth(depth)
                parameters(parametersBuilder?.build())
                httpHeaders(httpHeaders?.build())
                downloadDiskCachePolicy(downloadDiskCachePolicy)
                bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy)
                bitmapConfig(bitmapConfig)
                if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace(colorSpace)
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(preferQualityOverSpeed)
                resizeSize(resizeSize)
                resizeSize(resizeSizeResolver)
                resizePrecision(resizePrecisionDecider)
                resizeScale(resizeScaleDecider)
                transformations(transformations?.toList())
                disabledReuseBitmap(disabledReuseBitmap)
                ignoreExifOrientation(ignoreExifOrientation)
                bitmapMemoryCachePolicy(bitmapMemoryCachePolicy)
                disabledAnimatedImage(disabledAnimatedImage)
                placeholder(placeholderImage)
                error(errorImage)
                transition(transition)
                resizeApplyToDrawable(resizeApplyToDrawable)
            }
            val depth = depth
                ?: viewOptions?.depth
                ?: globalOptions?.depth
                ?: NETWORK
            val parameters = parametersBuilder?.build()
                .merge(viewOptions?.parameters)
                .merge(globalOptions?.parameters)
            val httpHeaders = httpHeaders?.build()
                .merge(viewOptions?.httpHeaders)
                .merge(globalOptions?.httpHeaders)
            val downloadDiskCachePolicy = downloadDiskCachePolicy
                ?: viewOptions?.downloadDiskCachePolicy
                ?: globalOptions?.downloadDiskCachePolicy
                ?: ENABLED
            val bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
                ?: viewOptions?.bitmapResultDiskCachePolicy
                ?: globalOptions?.bitmapResultDiskCachePolicy
                ?: ENABLED
            val bitmapConfig = bitmapConfig
                ?: viewOptions?.bitmapConfig
                ?: globalOptions?.bitmapConfig
            val colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O)
                colorSpace ?: viewOptions?.colorSpace
                ?: globalOptions?.colorSpace else null

            @Suppress("DEPRECATION")
            val preferQualityOverSpeed = preferQualityOverSpeed
                ?: viewOptions?.preferQualityOverSpeed
                ?: globalOptions?.preferQualityOverSpeed ?: false
            val resizeSize = resizeSize
                ?: viewOptions?.resizeSize
                ?: globalOptions?.resizeSize
            var resolvedResizeSize = false
            val resizeSizeResolver = resizeSizeResolver
                ?: resolvedResizeSizeResolver
                ?: viewOptions?.resizeSizeResolver
                ?: globalOptions?.resizeSizeResolver
                ?: (resolveResizeSizeResolver().apply {
                    resolvedResizeSize = true
                })
            val resizePrecisionDecider = resizePrecisionDecider
                ?: viewOptions?.resizePrecisionDecider
                ?: globalOptions?.resizePrecisionDecider
                ?: fixedPrecision(if (resizeSize != null || !resolvedResizeSize) EXACTLY else LESS_PIXELS)
            val resizeScaleDecider = resizeScaleDecider
                ?: resolvedResizeScaleDecider
                ?: viewOptions?.resizeScaleDecider
                ?: globalOptions?.resizeScaleDecider
                ?: fixedScale(resolveResizeScale())
            val transformations = transformations?.toList()
                .merge(viewOptions?.transformations)
                .merge(globalOptions?.transformations)
            val disabledReuseBitmap = disabledReuseBitmap
                ?: viewOptions?.disabledReuseBitmap
                ?: globalOptions?.disabledReuseBitmap
                ?: false
            val ignoreExifOrientation = ignoreExifOrientation
                ?: viewOptions?.ignoreExifOrientation
                ?: globalOptions?.ignoreExifOrientation
                ?: false
            val bitmapMemoryCachePolicy = bitmapMemoryCachePolicy
                ?: viewOptions?.bitmapMemoryCachePolicy
                ?: globalOptions?.bitmapMemoryCachePolicy
                ?: ENABLED
            val disabledAnimatedImage = disabledAnimatedImage
                ?: viewOptions?.disabledAnimatedImage
                ?: globalOptions?.disabledAnimatedImage
                ?: false
            val placeholderImage = placeholderImage
                ?: viewOptions?.placeholderImage
                ?: globalOptions?.placeholderImage
            val errorImage = errorImage
                ?: viewOptions?.errorImage
                ?: globalOptions?.errorImage
            val transition = transition
                ?: viewOptions?.transition
                ?: globalOptions?.transition
            val resizeApplyToDrawable = resizeApplyToDrawable
                ?: viewOptions?.resizeApplyToDrawable
                ?: globalOptions?.resizeApplyToDrawable

            return when (this@Builder) {
                is DisplayRequest.Builder -> {
                    DisplayRequest.DisplayRequestImpl(
                        sketch = sketch,
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
                        viewOptions = viewOptions,
                        globalOptions = globalOptions,
                        definedOptions = definedOptions,
                        depth = depth,
                        parameters = parameters,
                        httpHeaders = httpHeaders,
                        downloadDiskCachePolicy = downloadDiskCachePolicy,
                        bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                        bitmapConfig = bitmapConfig,
                        colorSpace = colorSpace,
                        preferQualityOverSpeed = preferQualityOverSpeed,
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disabledReuseBitmap = disabledReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
                        disabledAnimatedImage = disabledAnimatedImage,
                        placeholderImage = placeholderImage,
                        errorImage = errorImage,
                        transition = transition,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                    )
                }
                is LoadRequest.Builder -> {
                    LoadRequest.LoadRequestImpl(
                        sketch = sketch,
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
                        viewOptions = viewOptions,
                        globalOptions = globalOptions,
                        definedOptions = definedOptions,
                        depth = depth,
                        parameters = parameters,
                        httpHeaders = httpHeaders,
                        downloadDiskCachePolicy = downloadDiskCachePolicy,
                        bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                        bitmapConfig = bitmapConfig,
                        colorSpace = colorSpace,
                        preferQualityOverSpeed = preferQualityOverSpeed,
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disabledReuseBitmap = disabledReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
                        disabledAnimatedImage = disabledAnimatedImage,
                        placeholderImage = placeholderImage,
                        errorImage = errorImage,
                        transition = transition,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                    )
                }
                is DownloadRequest.Builder -> {
                    DownloadRequest.DownloadRequestImpl(
                        sketch = sketch,
                        context = context,
                        uriString = uriString,
                        listener = listener,
                        progressListener = progressListener,
                        target = target,
                        lifecycle = lifecycle,
                        viewOptions = viewOptions,
                        globalOptions = globalOptions,
                        definedOptions = definedOptions,
                        depth = depth,
                        parameters = parameters,
                        httpHeaders = httpHeaders,
                        downloadDiskCachePolicy = downloadDiskCachePolicy,
                        bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                        bitmapConfig = bitmapConfig,
                        colorSpace = colorSpace,
                        preferQualityOverSpeed = preferQualityOverSpeed,
                        resizeSize = resizeSize,
                        resizeSizeResolver = resizeSizeResolver,
                        resizePrecisionDecider = resizePrecisionDecider,
                        resizeScaleDecider = resizeScaleDecider,
                        transformations = transformations,
                        disabledReuseBitmap = disabledReuseBitmap,
                        ignoreExifOrientation = ignoreExifOrientation,
                        bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
                        disabledAnimatedImage = disabledAnimatedImage,
                        placeholderImage = placeholderImage,
                        errorImage = errorImage,
                        transition = transition,
                        resizeApplyToDrawable = resizeApplyToDrawable,
                    )
                }
                else -> throw UnsupportedOperationException("Unsupported ImageRequest.Builder: ${this@Builder::class.java}")
            }
        }

        /** Ensure these values will be recomputed when [build] is called. */
        private fun resetResolvedValues() {
            resolvedLifecycle = null
            resolvedResizeSizeResolver = null
            resolvedResizeScaleDecider = null
        }

        private fun resolveResizeSizeResolver(): SizeResolver {
            val target = target
            return if (target is ViewTarget<*>) {
                ViewSizeResolver(target.view)
            } else {
                ScreenSizeResolver(context)
            }
        }


        private fun resolveLifecycle(): Lifecycle? {
            val target = target
            val context = if (target is ViewTarget<*>) target.view.context else null
            return context.getLifecycle()
        }

        private fun resolveResizeScale(): Scale {
            val sizeResolver = resizeSizeResolver
            if (sizeResolver is ViewSizeResolver<*>) {
                val view = sizeResolver.view
                if (view is ImageView) return view.scale
            }

            val target = target
            if (target is ViewTarget<*>) {
                val view = target.view
                if (view is ImageView) return view.scale
            }

            return CENTER_CROP
        }

        private val ImageView.scale: Scale
            get() = when (scaleType) {
                ScaleType.FIT_START -> START_CROP
                ScaleType.FIT_CENTER -> CENTER_CROP
                ScaleType.FIT_END -> END_CROP
                ScaleType.CENTER_INSIDE -> CENTER_CROP
                ScaleType.CENTER -> CENTER_CROP
                ScaleType.CENTER_CROP -> CENTER_CROP
                else -> Scale.FILL
            }

        private fun combinationListener(): Listener<ImageRequest, ImageResult.Success, ImageResult.Error>? {
            val target = target
            val listener = listener
            val viewListenerProvider =
                target.asOrNull<ViewTarget<*>>()?.view?.asOrNull<ListenerProvider>()
            @Suppress("UNCHECKED_CAST") val viewListener =
                viewListenerProvider?.getListener() as Listener<ImageRequest, ImageResult.Success, ImageResult.Error>?
            return if (listener != null && viewListener != null && listener !== viewListener) {
                CombinedListener(viewListener, listener)
            } else {
                listener ?: viewListener
            }
        }

        private fun combinationProgressListener(): ProgressListener<ImageRequest>? {
            val target = target
            val progressListener = progressListener
            val viewListenerProvider =
                target.asOrNull<ViewTarget<*>>()?.view?.asOrNull<ListenerProvider>()
            @Suppress("UNCHECKED_CAST") val viewProgressListener =
                viewListenerProvider?.getProgressListener() as ProgressListener<ImageRequest>?
            return if (progressListener != null && viewProgressListener != null && progressListener != viewProgressListener) {
                CombinedProgressListener(viewProgressListener, progressListener)
            } else {
                progressListener ?: viewProgressListener
            }
        }
    }
}