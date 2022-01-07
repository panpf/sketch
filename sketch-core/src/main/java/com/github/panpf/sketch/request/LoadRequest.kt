package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.transform.Transformation

interface LoadRequest : DownloadRequest {

    /**
     * What is resultDiskCache. To speed up image load, cache the final bitmap to disk if you set [maxSize], [resize], [transformations] parameters (see [newQualityKey]). So that it can be used directly after the next read
     */
    val resultDiskCacheKey: String?

    /**
     * resultDiskCache policy configuration
     * @see resultDiskCacheKey
     */
    val resultDiskCachePolicy: CachePolicy

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

    companion object {
        fun new(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadRequest = Builder(uriString).apply {
            configBlock?.invoke(this)
        }.build()

        fun new(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadRequest = Builder(uri).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(uriString).apply {
            configBlock?.invoke(this)
        }

        fun newBuilder(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(uri).apply {
            configBlock?.invoke(this)
        }

        fun newQualityKey(request: LoadRequest): String? = buildString {
            val parameters = request.parameters
            if (parameters != null) {
                if (length > 0) append("_")
                append(parameters.cacheKey)
            }

            val maxSize = request.maxSize
            if (maxSize != null) {
                if (length > 0) append("_")
                append(maxSize.cacheKey)
            }

            val bitmapConfig = request.bitmapConfig
            if (bitmapConfig != null) {
                if (length > 0) append("_")
                append(bitmapConfig.cacheKey)
            }

            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val colorSpace = request.colorSpace
                if (colorSpace != null) {
                    if (length > 0) append("_")
                    append("ColorSpace(${colorSpace.name.replace(" ", "")}")
                }
            }

            val preferQualityOverSpeed = request.preferQualityOverSpeed
            if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == true) {
                if (length > 0) append("_")
                append("PreferQualityOverSpeed")
            }

            val resize = request.resize
            if (resize != null) {
                if (length > 0) append("_")
                append(resize.cacheKey)
            }

            val transformations = request.transformations
            if (transformations?.isNotEmpty() == true) {
                if (length > 0) append("_")
                append("Transformations(${transformations.joinToString(separator = ",")})")
            }

            val disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            if (disabledCorrectExifOrientation != true) {
                if (length > 0) append("_")
                append("CorrectExifOrientation")
            }
        }.takeIf { it.isNotEmpty() }
    }

    class Builder(private val uri: Uri) {

        private var depth: RequestDepth? = null
        private var parameters: Parameters? = null
        private var httpHeaders: Map<String, String>? = null
        private var diskCacheKey: String? = null
        private var diskCachePolicy: CachePolicy? = null
        private var resultDiskCacheKey: String? = null
        private var resultDiskCachePolicy: CachePolicy? = null
        private var maxSize: MaxSize? = null
        private var bitmapConfig: BitmapConfig? = null
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resize: Resize? = null
        private var transformations: List<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var disabledCorrectExifOrientation: Boolean? = null
        private var listener: Listener<ImageRequest, ImageResult, ImageResult>? = null
        private var progressListener: ProgressListener<ImageRequest>? = null

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: LoadRequest) : this(request.uri) {
            this.depth = request.depth
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.resultDiskCacheKey = request.resultDiskCacheKey
            this.resultDiskCachePolicy = request.resultDiskCachePolicy
            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            this.listener = request.listener
            this.progressListener = request.progressListener
        }

        fun depth(depth: RequestDepth?): Builder = apply {
            this.depth = depth
        }

        fun parameters(parameters: Parameters?): Builder = apply {
            this.parameters = parameters
        }

        fun httpHeaders(httpHeaders: Map<String, String>?): Builder = apply {
            this.httpHeaders = httpHeaders
        }

        fun diskCacheKey(diskCacheKey: String?): Builder = apply {
            this.diskCacheKey = diskCacheKey
        }

        fun diskCachePolicy(diskCachePolicy: CachePolicy?): Builder = apply {
            this.diskCachePolicy = diskCachePolicy
        }

        fun resultDiskCacheKey(resultDiskCacheKey: String?): Builder = apply {
            this.resultDiskCacheKey = resultDiskCacheKey
        }

        fun resultDiskCachePolicy(resultDiskCachePolicy: CachePolicy?): Builder = apply {
            this.resultDiskCachePolicy = resultDiskCachePolicy
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

        @RequiresApi(26)
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
            this.transformations = transformations
        }

        fun transformations(vararg transformations: Transformation): Builder = apply {
            this.transformations = transformations.toList()
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
        ) = listener(object : Listener<LoadRequest, LoadResult.Success, LoadResult.Error> {
            override fun onStart(request: LoadRequest) = onStart(request)
            override fun onCancel(request: LoadRequest) = onCancel(request)
            override fun onError(request: LoadRequest, result: LoadResult.Error) =
                onError(request, result)

            override fun onSuccess(request: LoadRequest, result: LoadResult.Success) =
                onSuccess(request, result)
        })

        fun progressListener(progressListener: ProgressListener<LoadRequest>?): Builder =
            apply {
                @Suppress("UNCHECKED_CAST")
                this.progressListener =
                    progressListener as ProgressListener<ImageRequest>?
            }

        fun build(): LoadRequest = LoadRequestImpl(
            uri = uri,
            _depth = depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _diskCacheKey = diskCacheKey,
            _diskCachePolicy = diskCachePolicy,
            _resultDiskCacheKey = resultDiskCacheKey,
            _resultDiskCachePolicy = resultDiskCachePolicy,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            listener = listener,
            progressListener = progressListener,
        )
    }

    private class LoadRequestImpl(
        override val uri: Uri,
        _depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: Map<String, String>?,
        _diskCacheKey: String?,
        _diskCachePolicy: CachePolicy?,
        _resultDiskCacheKey: String?,
        _resultDiskCachePolicy: CachePolicy?,
        override val maxSize: MaxSize?,
        override val bitmapConfig: BitmapConfig?,
        override val colorSpace: ColorSpace?,
        override val preferQualityOverSpeed: Boolean?,
        override val resize: Resize?,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean?,
        override val disabledCorrectExifOrientation: Boolean?,
        override val listener: Listener<ImageRequest, ImageResult, ImageResult>?,
        override val progressListener: ProgressListener<ImageRequest>?,
    ) : LoadRequest {

        override val uriString: String by lazy { uri.toString() }

        override val depth: RequestDepth = _depth ?: NETWORK

        override val diskCacheKey: String = _diskCacheKey ?: uriString

        override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

        override val resultDiskCacheKey: String? by lazy {
            _resultDiskCacheKey ?: qualityKey?.let { "${uriString}_$it" }
        }

        override val resultDiskCachePolicy: CachePolicy =
            _resultDiskCachePolicy ?: CachePolicy.ENABLED

        private val qualityKey: String? by lazy {
            newQualityKey(this)
        }

        override val key: String by lazy {
            buildString {
                append("Load")
                append("_").append(uriString)
                qualityKey?.let {
                    append("_").append(it)
                }
                parameters?.let {
                    append("_").append(it.key)
                }
                append("_").append("diskCacheKey($diskCacheKey)")
                append("_").append("diskCachePolicy($diskCachePolicy)")
            }
        }
    }
}

fun LoadRequest.newDecodeOptionsByQualityParams(mimeType: String): BitmapFactory.Options =
    BitmapFactory.Options().apply {
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
