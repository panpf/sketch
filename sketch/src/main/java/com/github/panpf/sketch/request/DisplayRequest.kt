package com.github.panpf.sketch.request

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
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.merge
import com.github.panpf.sketch.request.DisplayRequest.Builder
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.CombinedListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.ScreenSizeResolver
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.ViewSizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.ImageViewTarget
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

fun DisplayRequest(
    context: Context,
    uriString: String?,
    target: Target,
    configBlock: (Builder.() -> Unit)? = null
): DisplayRequest = Builder(context, uriString, target).apply {
    configBlock?.invoke(this)
}.build()

fun DisplayRequestBuilder(
    context: Context,
    uriString: String?,
    target: Target,
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder(context, uriString, target).apply {
    configBlock?.invoke(this)
}

fun DisplayRequest(
    uriString: String?,
    imageView: ImageView,
    configBlock: (Builder.() -> Unit)? = null
): DisplayRequest = Builder(imageView.context, uriString, ImageViewTarget(imageView)).apply {
    configBlock?.invoke(this)
}.build()

fun DisplayRequestBuilder(
    uriString: String?,
    imageView: ImageView,
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder(imageView.context, uriString, ImageViewTarget(imageView)).apply {
    configBlock?.invoke(this)
}

interface DisplayRequest : LoadRequest {

    val target: Target
    val lifecycle: Lifecycle?

    val disabledAnimationDrawable: Boolean
    val bitmapMemoryCachePolicy: CachePolicy
    val placeholderImage: StateImage?
    val errorImage: StateImage?
    val transition: Transition.Factory?

    val viewOptions: DisplayOptions?
    override val globalOptions: DisplayOptions?
    override val definedOptions: DisplayOptions

    fun newDisplayRequestBuilder(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(context, this).apply {
        configBlock?.invoke(this)
    }

    fun newDisplayRequest(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): DisplayRequest = Builder(context, this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder {
        private val context: Context
        private val uriString: String
        private var listener: Listener<ImageRequest, ImageResult, ImageResult>? = null
        private var progressListener: ProgressListener<ImageRequest>? = null

        private val target: Target
        private var lifecycle: Lifecycle? = null

        private var viewOptions: DisplayOptions? = null
        private var globalOptions: DisplayOptions? = null
        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null
        private var httpHeaders: HttpHeaders.Builder? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null
        private var bitmapConfig: BitmapConfig? = null
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resizeSize: Size? = null
        private var resizeSizeResolver: SizeResolver? = null
        private var resizePrecisionDecider: PrecisionDecider? = null
        private var resizeScale: Scale? = null
        private var transformations: MutableSet<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null
        private var bitmapMemoryCachePolicy: CachePolicy? = null
        private var disabledAnimationDrawable: Boolean? = null
        private var placeholderImage: StateImage? = null
        private var errorImage: StateImage? = null
        private var transition: Transition.Factory? = null

        private var resolvedLifecycle: Lifecycle? = null
        private var resolvedResizeSizeResolver: SizeResolver? = null
        private var resolvedResizeScale: Scale? = null

        constructor(context: Context, uriString: String?, target: Target) {
            this.context = context
            this.uriString = uriString.orEmpty()

            this.target = target

            this.viewOptions = target.asOrNull<ViewTarget<*>>()
                ?.view.asOrNull<DisplayOptionsProvider>()
                ?.displayOptions
            this.globalOptions = context.sketch.globalDisplayOptions
        }

        internal constructor(context: Context, request: DisplayRequest) {
            this.context = context
            this.uriString = request.uriString
            this.listener =
                request.listener.asOrNull<CombinedListener<ImageRequest, ImageResult, ImageResult>>()?.fromBuilderListener
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
            this.networkContentDiskCachePolicy = request.networkContentDiskCachePolicy
            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) this.colorSpace = request.colorSpace
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resizeSize = request.resizeSize
            this.resizeSizeResolver = request.resizeSizeResolver
            this.resizePrecisionDecider = request.resizePrecisionDecider
            this.resizeScale = request.resizeScale
            this.transformations = request.transformations?.toMutableSet()
            this.disabledBitmapPool = request.disabledBitmapPool
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy
            this.bitmapMemoryCachePolicy = request.bitmapMemoryCachePolicy
            this.disabledAnimationDrawable = request.disabledAnimationDrawable
            this.placeholderImage = request.placeholderImage
            this.errorImage = request.errorImage
            this.transition = request.transition

            // If the context changes, recompute the resolved values.
            if (request.context === context) {
                resolvedLifecycle = request.lifecycle
                resolvedResizeSizeResolver = request.resizeSizeResolver
                resolvedResizeScale = request.resizeScale
            } else {
                resolvedLifecycle = null
                resolvedResizeSizeResolver = null
                resolvedResizeScale = null
            }
        }

        fun listener(listener: Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.listener = listener as Listener<ImageRequest, ImageResult, ImageResult>?
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: DisplayRequest) -> Unit = {},
            crossinline onCancel: (request: DisplayRequest) -> Unit = {},
            crossinline onError: (request: DisplayRequest, result: DisplayResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: DisplayRequest, result: DisplayResult.Success) -> Unit = { _, _ -> }
        ): Builder =
            listener(object : Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error> {
                override fun onStart(request: DisplayRequest) = onStart(request)
                override fun onCancel(request: DisplayRequest) = onCancel(request)
                override fun onError(request: DisplayRequest, result: DisplayResult.Error) =
                    onError(request, result)

                override fun onSuccess(request: DisplayRequest, result: DisplayResult.Success) =
                    onSuccess(request, result)
            })

        fun progressListener(progressListener: ProgressListener<DisplayRequest>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.progressListener = progressListener as ProgressListener<ImageRequest>?
            }

        fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            this.lifecycle = lifecycle
        }

        fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        fun depthFrom(from: String?): Builder = apply {
            if (from != null) {
                setParameter(ImageRequest.REQUEST_DEPTH_FROM, from, null)
            } else {
                removeParameter(ImageRequest.REQUEST_DEPTH_FROM)
            }
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parametersBuilder = parameters?.newBuilder()
        }

        /**
         * Set a parameter for this request.
         *
         * @see Parameters.Builder.set
         */
        @JvmOverloads
        fun setParameter(key: String, value: Any?, cacheKey: String? = value?.toString()): Builder =
            apply {
                this.parametersBuilder = (this.parametersBuilder ?: Parameters.Builder()).apply {
                    set(
                        key,
                        value,
                        cacheKey
                    )
                }
            }

        /**
         * Remove a parameter from this request.
         *
         * @see Parameters.Builder.remove
         */
        fun removeParameter(key: String): Builder = apply {
            this.parametersBuilder?.remove(key)
        }

        fun httpHeaders(httpHeaders: HttpHeaders?): Builder = apply {
            this.httpHeaders = httpHeaders?.newBuilder()
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        fun addHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                add(name, value)
            }
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        fun setHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HttpHeaders.Builder()).apply {
                set(name, value)
            }
        }

        /**
         * Remove all network headers with the key [name].
         */
        fun removeHttpHeader(name: String): Builder = apply {
            this.httpHeaders?.removeAll(name)
        }

        fun networkContentDiskCachePolicy(networkContentDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.networkContentDiskCachePolicy = networkContentDiskCachePolicy
            }

        fun bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
            }

        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        fun bitmapConfig(bitmapConfig: Bitmap.Config?): Builder = apply {
            this.bitmapConfig = if (bitmapConfig != null) BitmapConfig(bitmapConfig) else null
        }

        fun lowQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.LOW_QUALITY
        }

        fun middenQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.MIDDEN_QUALITY
        }

        fun highQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.HIGH_QUALITY
        }

        @RequiresApi(VERSION_CODES.O)
        fun colorSpace(colorSpace: ColorSpace?): Builder = apply {
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
        fun preferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            if (VERSION.SDK_INT < VERSION_CODES.N) {
                this.preferQualityOverSpeed = inPreferQualityOverSpeed
            }
        }

        fun resizeSizeResolver(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
            resetResolvedValues()
        }

        fun resizeSize(size: Size?): Builder = apply {
            this.resizeSize = size
        }

        fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            this.resizeSize = Size(width, height)
        }

        fun resizePrecision(precisionDecider: PrecisionDecider): Builder = apply {
            this.resizePrecisionDecider = precisionDecider
        }

        fun resizePrecision(precision: Precision): Builder = apply {
            this.resizePrecisionDecider = FixedPrecisionDecider(precision)
        }

        fun resizeScale(scale: Scale): Builder = apply {
            this.resizeScale = scale
        }

        fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = transformations?.toMutableSet()
        }

        fun transformations(vararg transformations: Transformation): Builder = apply {
            this.transformations = transformations.toMutableSet()
        }

        fun addTransformations(transformations: List<Transformation>): Builder = apply {
            val newTransformations = transformations.filter { newTransformation ->
                this.transformations?.find { it.cacheKey == newTransformation.cacheKey } == null
            }
            this.transformations = (this.transformations ?: HashSet()).apply {
                addAll(newTransformations)
            }
        }

        fun addTransformations(vararg transformations: Transformation): Builder = apply {
            addTransformations(transformations.toList())
        }

        fun removeTransformations(removeTransformations: List<Transformation>): Builder = apply {
            this.transformations = this.transformations?.filter { oldTransformation ->
                removeTransformations.find { it.cacheKey == oldTransformation.cacheKey } == null
            }?.toMutableSet()
        }

        fun removeTransformations(vararg removeTransformations: Transformation): Builder = apply {
            removeTransformations(removeTransformations.toList())
        }

        fun disabledBitmapPool(disabledBitmapPool: Boolean? = true): Builder = apply {
            this.disabledBitmapPool = disabledBitmapPool
        }

        fun ignoreExifOrientation(ignoreExifOrientation: Boolean? = true): Builder =
            apply {
                this.ignoreExifOrientation = ignoreExifOrientation
            }

        fun bitmapMemoryCachePolicy(bitmapMemoryCachePolicy: CachePolicy?): Builder = apply {
            this.bitmapMemoryCachePolicy = bitmapMemoryCachePolicy
        }

        fun disabledAnimationDrawable(disabledAnimationDrawable: Boolean? = true): Builder = apply {
            this.disabledAnimationDrawable = disabledAnimationDrawable
        }

        fun placeholderImage(placeholderImage: StateImage?): Builder = apply {
            this.placeholderImage = placeholderImage
        }

        fun placeholderImage(placeholderDrawable: Drawable?): Builder = apply {
            this.placeholderImage =
                if (placeholderDrawable != null) StateImage.drawable(placeholderDrawable) else null
        }

        fun placeholderImage(@DrawableRes placeholderDrawableResId: Int?): Builder = apply {
            this.placeholderImage = if (placeholderDrawableResId != null) {
                StateImage.drawableRes(placeholderDrawableResId)
            } else null
        }

        fun errorImage(
            errorImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorImage?.let {
                if (configBlock != null) {
                    ErrorStateImage.new(it, configBlock)
                } else {
                    it
                }
            }
        }

        fun errorImage(
            errorDrawable: Drawable?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorDrawable?.let {
                if (configBlock != null) {
                    ErrorStateImage.new(StateImage.drawable(it), configBlock)
                } else {
                    StateImage.drawable(it)
                }
            }
        }

        fun errorImage(
            errorDrawableResId: Int?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorDrawableResId?.let {
                if (configBlock != null) {
                    ErrorStateImage.new(StateImage.drawableRes(it), configBlock)
                } else {
                    StateImage.drawableRes(it)
                }
            }
        }

        fun transition(transition: Transition.Factory?): Builder = apply {
            this.transition = transition
        }

        fun crossfadeTransition(
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            preferExactIntrinsicSize: Boolean = false
        ): Builder = apply {
            transition(CrossfadeTransition.Factory(durationMillis, preferExactIntrinsicSize))
        }

        fun options(options: DisplayOptions, requestFirst: Boolean = false): Builder = apply {
            if (!requestFirst || this.depth == null) {
                options.depth?.let {
                    this.depth = it
                }
            }
            options.parameters?.takeIf { it.isNotEmpty() }?.let {
                it.forEach { entry ->
                    if (!requestFirst || parametersBuilder?.exist(entry.first) != true) {
                        setParameter(entry.first, entry.second.value, entry.second.cacheKey)
                    }
                }
            }
            options.httpHeaders?.takeIf { !it.isEmpty() }?.let { headers ->
                headers.addList.forEach {
                    addHttpHeader(it.first, it.second)
                }
                headers.setList.forEach {
                    if (!requestFirst || httpHeaders?.setExist(it.first) != true) {
                        setHttpHeader(it.first, it.second)
                    }
                }
            }
            if (!requestFirst || this.networkContentDiskCachePolicy == null) {
                options.networkContentDiskCachePolicy?.let {
                    this.networkContentDiskCachePolicy = it
                }
            }

            if (!requestFirst || this.bitmapConfig == null) {
                options.bitmapConfig?.let {
                    this.bitmapConfig = it
                }
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                if (!requestFirst || this.colorSpace == null) {
                    options.colorSpace?.let {
                        this.colorSpace = it
                    }
                }
            }
            if (!requestFirst || this.preferQualityOverSpeed == null) {
                @Suppress("DEPRECATION")
                options.preferQualityOverSpeed?.let {
                    this.preferQualityOverSpeed = it
                }
            }
            if (!requestFirst || this.resizeSize == null) {
                options.resizeSize?.let {
                    this.resizeSize = it
                }
            }
            if (!requestFirst || this.resizePrecisionDecider == null) {
                options.resizePrecisionDecider?.let {
                    this.resizePrecisionDecider = it
                }
            }
            if (!requestFirst || this.resizeScale == null) {
                options.resizeScale?.let {
                    this.resizeScale = it
                }
            }
            options.transformations?.takeIf { it.isNotEmpty() }?.let {
                addTransformations(it)
            }
            if (!requestFirst || this.disabledBitmapPool == null) {
                options.disabledBitmapPool?.let {
                    this.disabledBitmapPool = it
                }
            }
            if (!requestFirst || this.bitmapResultDiskCachePolicy == null) {
                options.bitmapResultDiskCachePolicy?.let {
                    this.bitmapResultDiskCachePolicy = it
                }
            }

            if (!requestFirst || this.disabledAnimationDrawable == null) {
                options.disabledAnimationDrawable?.let {
                    this.disabledAnimationDrawable = it
                }
            }
            if (!requestFirst || this.bitmapMemoryCachePolicy == null) {
                options.bitmapMemoryCachePolicy?.let {
                    this.bitmapMemoryCachePolicy = it
                }
            }
            if (!requestFirst || this.placeholderImage == null) {
                options.placeholderImage?.let {
                    this.placeholderImage = it
                }
            }
            if (!requestFirst || this.errorImage == null) {
                options.errorImage?.let {
                    this.errorImage = it
                }
            }
            if (!requestFirst || this.transition == null) {
                options.transition?.let {
                    this.transition = it
                }
            }
        }

        fun build(): DisplayRequest =
            DisplayRequestImpl(
                context = context,
                uriString = uriString,
                listener = combinationListener(),
                progressListener = combinationProgressListener(),
                target = target,
                lifecycle = lifecycle ?: resolvedLifecycle ?: resolveLifecycle(),
                viewOptions = viewOptions,
                globalOptions = globalOptions,
                definedOptions = DisplayOptions {
                    depth(depth)
                    parameters(parametersBuilder?.build())
                    httpHeaders(httpHeaders?.build())
                    networkContentDiskCachePolicy(networkContentDiskCachePolicy)
                    bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy)
                    bitmapConfig(bitmapConfig)
                    if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace(colorSpace)
                    preferQualityOverSpeed(preferQualityOverSpeed)
                    resizeSize(resizeSize)
                    resizeSizeResolver(resizeSizeResolver)
                    resizePrecision(resizePrecisionDecider)
                    resizeScale(resizeScale)
                    transformations(transformations?.toList())
                    disabledBitmapPool(disabledBitmapPool)
                    ignoreExifOrientation(ignoreExifOrientation)
                    bitmapMemoryCachePolicy(bitmapMemoryCachePolicy)
                    disabledAnimationDrawable(disabledAnimationDrawable)
                    placeholderImage(placeholderImage)
                    errorImage(errorImage)
                    transition(transition)
                },
                depth = depth
                    ?: viewOptions?.depth
                    ?: globalOptions?.depth
                    ?: NETWORK,
                parameters = parametersBuilder?.build()
                    .merge(viewOptions?.parameters)
                    .merge(globalOptions?.parameters),
                httpHeaders = httpHeaders?.build()
                    .merge(viewOptions?.httpHeaders)
                    .merge(globalOptions?.httpHeaders),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy
                    ?: viewOptions?.networkContentDiskCachePolicy
                    ?: globalOptions?.networkContentDiskCachePolicy
                    ?: ENABLED,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
                    ?: viewOptions?.bitmapResultDiskCachePolicy
                    ?: globalOptions?.bitmapResultDiskCachePolicy
                    ?: ENABLED,
                bitmapConfig = bitmapConfig
                    ?: viewOptions?.bitmapConfig
                    ?: globalOptions?.bitmapConfig,
                colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O)
                    colorSpace ?: viewOptions?.colorSpace
                    ?: globalOptions?.colorSpace else null,
                preferQualityOverSpeed = preferQualityOverSpeed
                    ?: viewOptions?.preferQualityOverSpeed
                    ?: globalOptions?.preferQualityOverSpeed ?: false,
                resizeSize = resizeSize
                    ?: viewOptions?.resizeSize
                    ?: globalOptions?.resizeSize,
                resizeSizeResolver = resizeSizeResolver
                    ?: resolvedResizeSizeResolver
                    ?: viewOptions?.resizeSizeResolver
                    ?: globalOptions?.resizeSizeResolver
                    ?: resolveResizeSizeResolver(),
                resizePrecisionDecider = resizePrecisionDecider
                    ?: viewOptions?.resizePrecisionDecider
                    ?: globalOptions?.resizePrecisionDecider
                    ?: fixedPrecision(LESS_PIXELS),
                resizeScale = resizeScale
                    ?: resolvedResizeScale
                    ?: viewOptions?.resizeScale
                    ?: globalOptions?.resizeScale
                    ?: resolveResizeScale(),
                transformations = transformations?.toList()
                    .merge(viewOptions?.transformations)
                    .merge(globalOptions?.transformations),
                disabledBitmapPool = disabledBitmapPool
                    ?: viewOptions?.disabledBitmapPool
                    ?: globalOptions?.disabledBitmapPool
                    ?: false,
                ignoreExifOrientation = ignoreExifOrientation
                    ?: viewOptions?.ignoreExifOrientation
                    ?: globalOptions?.ignoreExifOrientation
                    ?: false,
                bitmapMemoryCachePolicy = bitmapMemoryCachePolicy
                    ?: viewOptions?.bitmapMemoryCachePolicy
                    ?: globalOptions?.bitmapMemoryCachePolicy
                    ?: ENABLED,
                disabledAnimationDrawable = disabledAnimationDrawable
                    ?: viewOptions?.disabledAnimationDrawable
                    ?: globalOptions?.disabledAnimationDrawable
                    ?: false,
                placeholderImage = placeholderImage
                    ?: viewOptions?.placeholderImage
                    ?: globalOptions?.placeholderImage,
                errorImage = errorImage
                    ?: viewOptions?.errorImage
                    ?: globalOptions?.errorImage,
                transition = transition
                    ?: viewOptions?.transition
                    ?: globalOptions?.transition,
            )

        /** Ensure these values will be recomputed when [build] is called. */
        private fun resetResolvedValues() {
            resolvedLifecycle = null
            resolvedResizeSizeResolver = null
            resolvedResizeScale = null
        }

        private fun resolveResizeSizeResolver(): SizeResolver =
            if (target is ViewTarget<*>) {
                ViewSizeResolver(target.view)
            } else {
                ScreenSizeResolver(context)
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

        private fun combinationListener(): Listener<ImageRequest, ImageResult, ImageResult>? {
            val target = target
            val listener = listener
            val viewListenerProvider =
                target.asOrNull<ViewTarget<*>>()?.view?.asOrNull<ListenerProvider>()
            @Suppress("UNCHECKED_CAST") val viewListener =
                viewListenerProvider?.getListener() as Listener<ImageRequest, ImageResult, ImageResult>?
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

    private class DisplayRequestImpl(
        override val context: Context,
        override val uriString: String,
        override val listener: Listener<ImageRequest, ImageResult, ImageResult>?,
        override val progressListener: ProgressListener<ImageRequest>?,
        override val target: Target,
        override val lifecycle: Lifecycle?,
        override val viewOptions: DisplayOptions?,
        override val globalOptions: DisplayOptions?,
        override val definedOptions: DisplayOptions,
        override val depth: RequestDepth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy,
        override val bitmapResultDiskCachePolicy: CachePolicy,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        override val preferQualityOverSpeed: Boolean,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScale: Scale,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean,
        override val ignoreExifOrientation: Boolean,
        override val bitmapMemoryCachePolicy: CachePolicy,
        override val disabledAnimationDrawable: Boolean,
        override val placeholderImage: StateImage?,
        override val errorImage: StateImage?,
        override val transition: Transition.Factory?,
    ) : DisplayRequest {

        override val uri: Uri by lazy { Uri.parse(uriString) }

        override val networkContentDiskCacheKey: String = uriString

        override val resize: Resize? by lazy {
            resizeSize?.takeIf { it.width > 0 && it.height > 0 }?.let {
                Resize(
                    width = it.width, height = it.height,
                    precisionDecider = resizePrecisionDecider,
                    scale = resizeScale
                )
            }
        }

        override val cacheKey: String by lazy {
            buildString {
                append(uriString)
                qualityKey?.let {
                    append("_").append(it)
                }
                if (disabledAnimationDrawable) {
                    append("_").append("DisabledAnimationDrawable")
                }
            }
        }

        private val qualityKey: String? by lazy { newQualityKey() }

        override val key: String by lazy {
            buildString {
                append("Display")
                append("_").append(uriString)
                depth.takeIf { it != NETWORK }?.let {
                    append("_").append("RequestDepth(${it})")
                }
                parameters?.key?.takeIf { it.isNotEmpty() }?.let {
                    append("_").append(it)
                }
                httpHeaders?.takeIf { !it.isEmpty() }?.let {
                    append("_").append(it)
                }
                networkContentDiskCachePolicy.takeIf { it == ENABLED }?.let {
                    append("_").append("networkContentDiskCachePolicy($it)")
                }
                bitmapConfig?.let {
                    append("_").append(it.cacheKey)
                }
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    colorSpace?.let {
                        append("_").append("colorSpace(${it.name.replace(" ", "")}")
                    }
                }
                @Suppress("DEPRECATION")
                if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed) {
                    append("_").append("preferQualityOverSpeed")
                }
                resize?.let {
                    append("_").append(it.cacheKey)
                }
                transformations?.takeIf { it.isNotEmpty() }?.let { list ->
                    append("_").append("transformations(${list.joinToString(separator = ",") { it.cacheKey }})")
                }
                if (disabledBitmapPool) {
                    append("_").append("disabledBitmapPool")
                }
                if (ignoreExifOrientation) {
                    append("_").append("ignoreExifOrientation")
                }
                bitmapResultDiskCachePolicy.takeIf { it == ENABLED }?.let {
                    append("_").append("bitmapResultDiskCachePolicy($it)")
                }
                if (disabledAnimationDrawable) {
                    append("_").append("disabledAnimationDrawable")
                }
                bitmapMemoryCachePolicy.takeIf { it == ENABLED }?.let {
                    append("_").append("bitmapMemoryCachePolicy($it)")
                }
            }
        }

        override fun toString(): String = key
    }
}