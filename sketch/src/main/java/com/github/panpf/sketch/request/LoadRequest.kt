package com.github.panpf.sketch.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.http.merge
import com.github.panpf.sketch.request.LoadRequest.Builder
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.ScreenSizeResolver
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.merge
import com.github.panpf.sketch.util.Size

fun LoadRequest(
    context: Context,
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): LoadRequest = Builder(context, uriString).apply {
    configBlock?.invoke(this)
}.build()

fun LoadRequestBuilder(
    context: Context,
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder(context, uriString).apply {
    configBlock?.invoke(this)
}

interface LoadRequest : DownloadRequest {

    /**
     * Used to cache bitmaps in memory and on disk
     */
    val cacheKey: String


    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     *
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    val bitmapConfig: BitmapConfig?

    @get:RequiresApi(26)
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

    /**
     * The size of the desired bitmap
     */
    val resizeSize: Size?
    val resizeSizeResolver: SizeResolver
    val resizePrecisionDecider: PrecisionDecider
    val resizeScale: Scale
    val resize: Resize?

    /**
     * The list of [Transformation]s to be applied to this request.
     */
    val transformations: List<Transformation>?

    /**
     * Disabled reuse of Bitmap from [BitmapPool]
     */
    val disabledBitmapPool: Boolean

    /**
     * Ignore exif orientation
     */
    val ignoreExifOrientation: Boolean

    /**
     * @see com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheInterceptor
     */
    val bitmapResultDiskCachePolicy: CachePolicy

    override val globalOptions: LoadOptions?
    override val definedOptions: LoadOptions

    fun newLoadRequestBuilder(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(context, this).apply {
        configBlock?.invoke(this)
    }

    fun newLoadRequest(
        context: Context = this.context,
        configBlock: (Builder.() -> Unit)? = null
    ): LoadRequest = Builder(context, this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder {

        private val context: Context
        private val uriString: String
        private var listener: Listener<ImageRequest, ImageResult, ImageResult>? = null
        private var progressListener: ProgressListener<ImageRequest>? = null

        private var globalOptions: LoadOptions? = null
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

        constructor(context: Context, uriString: String?) {
            this.context = context.applicationContext
            this.uriString = uriString.orEmpty()

            this.globalOptions = context.sketch.globalLoadOptions
        }

        internal constructor(context: Context, request: LoadRequest) {
            this.context = context.applicationContext
            this.uriString = request.uriString
            this.listener = request.listener
            this.progressListener = request.progressListener

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
        }

        fun listener(listener: Listener<LoadRequest, LoadResult.Success, LoadResult.Error>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.listener = listener as Listener<ImageRequest, ImageResult, ImageResult>?
            }

        /**
         * Convenience function to create and set the [Listener].
         */
        inline fun listener(
            crossinline onStart: (request: LoadRequest) -> Unit = {},
            crossinline onCancel: (request: LoadRequest) -> Unit = {},
            crossinline onError: (request: LoadRequest, result: LoadResult.Error) -> Unit = { _, _ -> },
            crossinline onSuccess: (request: LoadRequest, result: LoadResult.Success) -> Unit = { _, _ -> }
        ): Builder = listener(object : Listener<LoadRequest, LoadResult.Success, LoadResult.Error> {
            override fun onStart(request: LoadRequest) = onStart(request)
            override fun onCancel(request: LoadRequest) = onCancel(request)
            override fun onError(request: LoadRequest, result: LoadResult.Error) =
                onError(request, result)

            override fun onSuccess(request: LoadRequest, result: LoadResult.Success) =
                onSuccess(request, result)
        })

        fun progressListener(progressListener: ProgressListener<LoadRequest>?): Builder = apply {
            @Suppress("UNCHECKED_CAST")
            this.progressListener = progressListener as ProgressListener<ImageRequest>?
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
                    set(key, value, cacheKey)
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

        fun resizeSize(size: Size?): Builder = apply {
            this.resizeSize = size
        }

        fun resizeSize(@Px width: Int, @Px height: Int): Builder = apply {
            this.resizeSize = Size(width, height)
        }

        fun resizeSizeResolver(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
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
                this.transformations?.find { it.key == newTransformation.key } == null
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
                removeTransformations.find { it.key == oldTransformation.key } == null
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

        fun options(options: LoadOptions, requestFirst: Boolean = false): Builder = apply {
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
            if (!requestFirst || this.resizeSizeResolver == null) {
                options.resizeSizeResolver?.let {
                    this.resizeSizeResolver = it
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
        }

        fun build(): LoadRequest =
            LoadRequestImpl(
                context = context,
                uriString = uriString,
                listener = listener,
                progressListener = progressListener,
                globalOptions = globalOptions,
                definedOptions = LoadOptions {
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
                },
                depth = depth
                    ?: globalOptions?.depth
                    ?: NETWORK,
                parameters = parametersBuilder?.build()
                    .merge(globalOptions?.parameters),
                httpHeaders = httpHeaders?.build()
                    .merge(globalOptions?.httpHeaders),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy
                    ?: globalOptions?.networkContentDiskCachePolicy
                    ?: ENABLED,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
                    ?: globalOptions?.bitmapResultDiskCachePolicy
                    ?: ENABLED,
                bitmapConfig = bitmapConfig
                    ?: globalOptions?.bitmapConfig,
                colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O)
                    colorSpace ?: globalOptions?.colorSpace else null,
                preferQualityOverSpeed = preferQualityOverSpeed
                    ?: globalOptions?.preferQualityOverSpeed ?: false,
                resizeSize = resizeSize
                    ?: globalOptions?.resizeSize,
                resizeSizeResolver = resizeSizeResolver
                    ?: globalOptions?.resizeSizeResolver
                    ?: ScreenSizeResolver(context),
                resizePrecisionDecider = resizePrecisionDecider
                    ?: globalOptions?.resizePrecisionDecider ?: fixedPrecision(LESS_PIXELS),
                resizeScale = resizeScale
                    ?: globalOptions?.resizeScale
                    ?: CENTER_CROP,
                transformations = transformations?.toList()
                    .merge(globalOptions?.transformations),
                disabledBitmapPool = disabledBitmapPool
                    ?: globalOptions?.disabledBitmapPool
                    ?: false,
                ignoreExifOrientation = ignoreExifOrientation
                    ?: globalOptions?.ignoreExifOrientation
                    ?: false,
            )
    }


    private class LoadRequestImpl(
        override val context: Context,
        override val uriString: String,
        override val listener: Listener<ImageRequest, ImageResult, ImageResult>?,
        override val progressListener: ProgressListener<ImageRequest>?,
        override val globalOptions: LoadOptions?,
        override val definedOptions: LoadOptions,
        override val depth: RequestDepth,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy,
        override val bitmapResultDiskCachePolicy: CachePolicy,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver,
        override val resizePrecisionDecider: PrecisionDecider,
        override val resizeScale: Scale,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean,
        override val ignoreExifOrientation: Boolean,
    ) : LoadRequest {

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
            }
        }

        private val qualityKey: String? by lazy { newQualityKey() }

        override val key: String by lazy {
            buildString {
                append("Load")
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
                    append("_").append(it.key)
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
                    append("_").append(it.key)
                }
                transformations?.takeIf { it.isNotEmpty() }?.let { list ->
                    append("_").append("transformations(${list.joinToString(separator = ",") { 
                        it.key.replace("Transformation", "") 
                    }})")
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
            }
        }

        override fun toString(): String = key
    }
}

fun LoadRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed) {
            inPreferQualityOverSpeed = true
        }

        val newConfig = bitmapConfig?.getConfigByMimeType(mimeType)
        if (newConfig != null) {
            inPreferredConfig = newConfig
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != null) {
            inPreferredColorSpace = colorSpace
        }
    }

internal fun LoadRequest.newQualityKey(): String? {
    val fragmentList = buildList {
        parameters?.cacheKey?.let {
            add(it)
        }
        bitmapConfig?.let {
            add(it.key)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace?.let {
                add("colorSpace(${it.name.replace(" ", "")}")
            }
        }
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed) {
            add("preferQualityOverSpeed")
        }
        resize?.let {
            add(it.key)
        }
        transformations?.takeIf { it.isNotEmpty() }?.let { list ->
            add("transformations(${list.joinToString(separator = ",") { it.key }})")
        }
        if (ignoreExifOrientation) {
            add("ignoreExifOrientation")
        }
    }
    return fragmentList.takeIf { it.isNotEmpty() }
        ?.joinToString(separator = ",", prefix = "Quality(", postfix = ")")
}