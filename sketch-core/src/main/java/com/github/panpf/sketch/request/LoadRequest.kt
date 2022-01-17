package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.MaxSize
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.transform.Transformation
import com.github.panpf.sketch.request.LoadRequest.Builder
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

fun LoadRequest(
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): LoadRequest = Builder(uriString).apply {
    configBlock?.invoke(this)
}.build()

fun LoadRequestBuilder(
    uriString: String,
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder(uriString).apply {
    configBlock?.invoke(this)
}

interface LoadRequest : DownloadRequest {

    /**
     * Used to cache bitmaps in memory and on disk
     */
    val cacheKey: String


    /**
     * Limit the maximum size of the bitmap on decode, default value is [MaxSize.SCREEN_SIZE]
     *
     * Applied to [android.graphics.BitmapFactory.Options.inSampleSize]
     */
    val maxSize: MaxSize?

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
    val preferQualityOverSpeed: Boolean?

    /**
     * The size of the desired bitmap
     */
    val resize: Resize?

    /**
     * The list of [Transformation]s to be applied to this request.
     */
    val transformations: List<Transformation>?

    /**
     * Disabled reuse of Bitmap from [BitmapPool]
     */
    val disabledBitmapPool: Boolean?

    /**
     * Disabled correcting the image orientation based on 'exifOrientation'
     */
    val disabledCorrectExifOrientation: Boolean?

    /**
     * @see com.github.panpf.sketch.decode.internal.BitmapResultCacheInterceptor
     */
    val bitmapResultDiskCachePolicy: CachePolicy

    fun newLoadRequestBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun newLoadRequest(
        configBlock: (Builder.() -> Unit)? = null
    ): LoadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder(private val uriString: String) {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null
        private var listener: Listener<ImageRequest, ImageResult, ImageResult>? = null

        private var httpHeaders: MutableMap<String, String>? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null
        private var progressListener: ProgressListener<ImageRequest>? = null

        private var maxSize: MaxSize? = null
        private var bitmapConfig: BitmapConfig? = null

        @RequiresApi(VERSION_CODES.O)
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resize: Resize? = null
        private var transformations: MutableSet<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var disabledCorrectExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null

        internal constructor(request: LoadRequest) : this(request.uriString) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()
            this.listener = request.listener

            this.httpHeaders = request.httpHeaders?.toMutableMap()
            this.networkContentDiskCachePolicy = request.networkContentDiskCachePolicy
            this.progressListener = request.progressListener

            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            @Suppress("DEPRECATION")
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations?.toMutableSet()
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy
        }

        fun options(options: LoadOptions): Builder = apply {
            options.depth?.let {
                this.depth = it
            }
            options.parameters?.let {
                it.forEach { entry ->
                    setParameter(entry.first, entry.second.value, entry.second.cacheKey)
                }
            }
            options.httpHeaders?.let {
                it.forEach { entry ->
                    setHttpHeader(entry.key, entry.value)
                }
            }
            options.networkContentDiskCachePolicy?.let {
                this.networkContentDiskCachePolicy = it
            }

            options.maxSize?.let {
                this.maxSize = it
            }
            options.bitmapConfig?.let {
                this.bitmapConfig = it
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                options.colorSpace?.let {
                    this.colorSpace = it
                }
            }
            @Suppress("DEPRECATION")
            options.preferQualityOverSpeed?.let {
                this.preferQualityOverSpeed = it
            }
            options.resize?.let {
                this.resize = it
            }
            options.transformations?.let {
                transformations(it)
            }
            options.disabledBitmapPool?.let {
                this.disabledBitmapPool = it
            }
            options.bitmapResultDiskCachePolicy?.let {
                this.bitmapResultDiskCachePolicy = it
            }
            options.bitmapResultDiskCachePolicy?.let {
                this.bitmapResultDiskCachePolicy = it
            }
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

        fun httpHeaders(httpHeaders: Map<String, String>?): Builder = apply {
            this.httpHeaders = httpHeaders?.toMutableMap()
        }

        /**
         * Add a header for any network operations performed by this request.
         */
        fun addHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HashMap()).apply {
                put(name, value)
            }
        }

        /**
         * Set a header for any network operations performed by this request.
         */
        fun setHttpHeader(name: String, value: String): Builder = apply {
            this.httpHeaders = (this.httpHeaders ?: HashMap()).apply {
                set(name, value)
            }
        }

        /**
         * Remove all network headers with the key [name].
         */
        fun removeHttpHeader(name: String): Builder = apply {
            this.httpHeaders?.remove(name)
        }

        fun networkContentDiskCachePolicy(networkContentDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.networkContentDiskCachePolicy = networkContentDiskCachePolicy
            }

        fun bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
            }

        fun maxSize(maxSize: MaxSize?): Builder = apply {
            this.maxSize = maxSize
        }

        fun maxSize(width: Int, height: Int): Builder = apply {
            this.maxSize = MaxSize(width, height)
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

        fun resize(resize: Resize?): Builder = apply {
            this.resize = resize
        }

        fun resize(
            @Px width: Int,
            @Px height: Int,
            mode: Resize.Mode = Resize.Mode.EXACTLY_SAME
        ): Builder = apply {
            this.resize = Resize(width, height, mode)
        }

        fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = (this.transformations ?: LinkedHashSet()).apply {
                transformations?.forEach {
                    add(it)
                }
            }
        }

        fun transformations(vararg transformations: Transformation): Builder = apply {
            transformations(transformations.toList())
        }

        fun disabledBitmapPool(disabledBitmapPool: Boolean? = true): Builder = apply {
            this.disabledBitmapPool = disabledBitmapPool
        }

        fun disabledCorrectExifOrientation(disabledCorrectExifOrientation: Boolean? = true): Builder =
            apply {
                this.disabledCorrectExifOrientation = disabledCorrectExifOrientation
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

        fun build(): LoadRequest = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LoadRequestImpl(
                uriString = uriString,
                _depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.toMap(),
                _networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                _bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                disabledCorrectExifOrientation = disabledCorrectExifOrientation,
                listener = listener,
                progressListener = progressListener,
            )
        } else {
            LoadRequestImpl(
                uriString = uriString,
                _depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.toMap(),
                _networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                _bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                disabledCorrectExifOrientation = disabledCorrectExifOrientation,
                listener = listener,
                progressListener = progressListener,
            )
        }
    }

    private class LoadRequestImpl(
        override val uriString: String,
        _depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: Map<String, String>?,
        _networkContentDiskCachePolicy: CachePolicy?,
        _bitmapResultDiskCachePolicy: CachePolicy?,
        override val maxSize: MaxSize?,
        override val bitmapConfig: BitmapConfig?,
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean?,
        override val resize: Resize?,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean?,
        override val disabledCorrectExifOrientation: Boolean?,
        override val listener: Listener<ImageRequest, ImageResult, ImageResult>?,
        override val progressListener: ProgressListener<ImageRequest>?,
    ) : LoadRequest {

        @RequiresApi(VERSION_CODES.O)
        constructor(
            uriString: String,
            _depth: RequestDepth?,
            parameters: Parameters?,
            httpHeaders: Map<String, String>?,
            _networkContentDiskCachePolicy: CachePolicy?,
            _bitmapResultDiskCachePolicy: CachePolicy?,
            maxSize: MaxSize?,
            bitmapConfig: BitmapConfig?,
            colorSpace: ColorSpace?,
            preferQualityOverSpeed: Boolean?,
            resize: Resize?,
            transformations: List<Transformation>?,
            disabledBitmapPool: Boolean?,
            disabledCorrectExifOrientation: Boolean?,
            listener: Listener<ImageRequest, ImageResult, ImageResult>?,
            progressListener: ProgressListener<ImageRequest>?
        ) : this(
            uriString = uriString,
            _depth = _depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _networkContentDiskCachePolicy = _networkContentDiskCachePolicy,
            _bitmapResultDiskCachePolicy = _bitmapResultDiskCachePolicy,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            listener = listener,
            progressListener = progressListener,
        ) {
            _colorSpace = colorSpace
        }

        @RequiresApi(VERSION_CODES.O)
        private var _colorSpace: ColorSpace? = null

        @get:RequiresApi(VERSION_CODES.O)
        override val colorSpace: ColorSpace?
            get() = _colorSpace

        override val uri: Uri by lazy { Uri.parse(uriString) }

        override val depth: RequestDepth = _depth ?: NETWORK

        override val networkContentDiskCacheKey: String = uriString

        override val networkContentDiskCachePolicy: CachePolicy =
            _networkContentDiskCachePolicy ?: CachePolicy.ENABLED

        override val cacheKey: String by lazy {
            buildString {
                append(uriString)
                qualityKey?.let {
                    append("_").append(it)
                }
            }
        }

        override val bitmapResultDiskCachePolicy: CachePolicy =
            _bitmapResultDiskCachePolicy ?: CachePolicy.ENABLED

        private val qualityKey: String? by lazy { newQualityKey() }

        override val key: String by lazy {
            buildString {
                append("Load")
                append("_").append(uriString)
                parameters?.key?.takeIf { it.isNotEmpty() }?.let {
                    append("_").append(it)
                }
                httpHeaders?.takeIf { it.isNotEmpty() }?.let {
                    append("_").append("httpHeaders(").append(it.toString()).append(")")
                }
                if (networkContentDiskCachePolicy != CachePolicy.ENABLED) {
                    append("_").append("networkContentDiskCachePolicy($networkContentDiskCachePolicy)")
                }
                maxSize?.let {
                    append("_").append(it.cacheKey)
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
                if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == true) {
                    append("_").append("preferQualityOverSpeed")
                }
                resize?.let {
                    append("_").append(it.cacheKey)
                }
                transformations?.takeIf { it.isNotEmpty() }?.let { list ->
                    append("_").append("transformations(${list.joinToString(separator = ",") { it.cacheKey }})")
                }
                if (disabledBitmapPool == true) {
                    append("_").append("disabledBitmapPool")
                }
                if (disabledCorrectExifOrientation == true) {
                    append("_").append("disabledCorrectExifOrientation")
                }
                if (bitmapResultDiskCachePolicy != CachePolicy.ENABLED) {
                    append("_").append("bitmapResultDiskCachePolicy($bitmapResultDiskCachePolicy)")
                }
            }
        }
    }
}

fun LoadRequest.newDecodeConfigByQualityParams(mimeType: String): DecodeConfig =
    DecodeConfig().apply {
        @Suppress("DEPRECATION")
        if (VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed == true) {
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

internal fun LoadRequest.newQualityKey(): String? = buildString {
    val prefix = "Quality("
    append(prefix)
    parameters?.cacheKey?.let {
        if (length > prefix.length) append(",")
        append(it)
    }
    maxSize?.let {
        if (length > prefix.length) append(",")
        append(it.cacheKey)
    }
    bitmapConfig?.let {
        if (length > prefix.length) append(",")
        append(it.cacheKey)
    }
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace?.let {
            if (length > prefix.length) append(",")
            append("colorSpace(${it.name.replace(" ", "")}")
        }
    }
    @Suppress("DEPRECATION")
    if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == true) {
        if (length > prefix.length) append(",")
        append("preferQualityOverSpeed")
    }
    resize?.let {
        if (length > prefix.length) append(",")
        append(it.cacheKey)
    }
    transformations?.takeIf { it.isNotEmpty() }?.let { list ->
        if (length > prefix.length) append(",")
        append("transformations(${list.joinToString(separator = ",") { it.cacheKey }})")
    }
    if (disabledCorrectExifOrientation == true) {
        if (length > prefix.length) append(",")
        append("disabledCorrectExifOrientation")
    }
    append(")")
}.takeIf { it.isNotEmpty() }