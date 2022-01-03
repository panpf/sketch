package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.os.Build
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.RequestDepth.NETWORK
import com.github.panpf.sketch.request.internal.DisplayableRequest
import com.github.panpf.sketch.request.internal.ListenerRequest
import com.github.panpf.sketch.request.internal.LoadableRequest
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation

class DisplayRequest(
    override val url: String,
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
    _memoryCacheKey: String?,
    _memoryCachePolicy: CachePolicy?,
    override val disabledAnimationDrawable: Boolean?,
    override val loadingImage: StateImage?,
    override val errorImage: StateImage?,
    override val emptyImage: StateImage?,
    override val target: Target?,
    override val listener: Listener<DisplayRequest, DisplayResult>?,
    override val networkProgressListener: ProgressListener<DisplayRequest>?,
) : DisplayableRequest, ListenerRequest<DisplayRequest, DisplayResult> {

    override val depth: RequestDepth = _depth ?: NETWORK

    override val diskCacheKey: String = _diskCacheKey ?: url

    override val diskCachePolicy: CachePolicy = _diskCachePolicy ?: CachePolicy.ENABLED

    override val resultDiskCacheKey: String? by lazy {
        _resultDiskCacheKey ?: qualityKey?.let { "${url}_$it" }
    }

    override val resultDiskCachePolicy: CachePolicy = _resultDiskCachePolicy ?: CachePolicy.ENABLED

    override val memoryCachePolicy: CachePolicy = _memoryCachePolicy ?: CachePolicy.ENABLED

    override val memoryCacheKey: String by lazy {
        if (_memoryCacheKey != null) {
            _memoryCacheKey
        } else {
            val qualityKeyPart = qualityKey?.let { "_$it" } ?: ""
            val animationDrawablePart =
                if (disabledAnimationDrawable != true) "_AnimationDrawable" else ""
            "${url}${qualityKeyPart}${animationDrawablePart}"
        }
    }

    override val qualityKey: String? by lazy {
        LoadableRequest.newQualityKey(this)
    }

    override val key: String by lazy {
        val parametersInfo = parameters?.let { "_${it.key}" } ?: ""
        val qualityKey = qualityKey?.let { "_${it}" } ?: ""
        val animationDrawablePart =
            if (disabledAnimationDrawable != true) "_AnimationDrawable" else ""
        "Display_${url}${parametersInfo})_diskCacheKey($diskCacheKey)_diskCachePolicy($diskCachePolicy)" +
                "${qualityKey}_memoryCacheKey($memoryCacheKey)_memoryCachePolicy($memoryCachePolicy)${animationDrawablePart}"
    }

    override fun newDecodeOptionsByQualityParams(mimeType: String): BitmapFactory.Options =
        LoadableRequest.newDecodeOptionsByQualityParams(this, mimeType)

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): DisplayRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    fun toLoadRequest(): LoadRequest = LoadRequest.new(url) {
        depth(depth)
        parameters(parameters)
        httpHeaders(httpHeaders)
        diskCacheKey(diskCacheKey)
        diskCachePolicy(diskCachePolicy)
        resultDiskCacheKey(resultDiskCacheKey)
        resultDiskCachePolicy(resultDiskCachePolicy)
        maxSize(maxSize)
        bitmapConfig(bitmapConfig)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            colorSpace(colorSpace)
        }
        preferQualityOverSpeed(preferQualityOverSpeed)
        resize(resize)
        transformations(transformations)
        disabledBitmapPool(disabledBitmapPool)
        disabledCorrectExifOrientation(disabledCorrectExifOrientation)
        networkProgressListener?.let {
            networkProgressListener { _, totalLength, completedLength ->
                it.onUpdateProgress(this@DisplayRequest, totalLength, completedLength)
            }
        }
    }

    companion object {
        internal const val SIZE_BY_VIEW_FIXED_SIZE: Int = -214238643

        fun new(
            url: String?,
            configBlock: (Builder.() -> Unit)? = null
        ): DisplayRequest = Builder(url).apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            url: String?,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder(url).apply {
            configBlock?.invoke(this)
        }
    }

    class Builder {

        private val url: String
        private var depth: RequestDepth?
        private var parameters: Parameters?
        private var httpHeaders: Map<String, String>?
        private var diskCacheKey: String?
        private var diskCachePolicy: CachePolicy?
        private var resultDiskCacheKey: String?
        private var resultDiskCachePolicy: CachePolicy?
        private var maxSize: MaxSize?
        private var bitmapConfig: BitmapConfig?
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean?
        private var resize: Resize?
        private var transformations: List<Transformation>?
        private var disabledBitmapPool: Boolean?
        private var disabledCorrectExifOrientation: Boolean?
        private var memoryCacheKey: String?
        private var memoryCachePolicy: CachePolicy?
        private var disabledAnimationDrawable: Boolean?
        private var loadingImage: StateImage?
        private var errorImage: StateImage?
        private var emptyImage: StateImage?
        private var target: Target?
        private var listener: Listener<DisplayRequest, DisplayResult>? = null
        private var networkProgressListener: ProgressListener<DisplayRequest>? = null

        constructor(url: String?) {
            this.url = url ?: ""
            this.depth = null
            this.parameters = null
            this.httpHeaders = null
            this.diskCacheKey = null
            this.diskCachePolicy = null
            this.resultDiskCacheKey = null
            this.resultDiskCachePolicy = null
            this.maxSize = MaxSize.SCREEN_SIZE
            this.bitmapConfig = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.colorSpace = null
            }
            this.preferQualityOverSpeed = null
            this.resize = null
            this.transformations = null
            this.disabledBitmapPool = null
            this.disabledCorrectExifOrientation = null
            this.memoryCacheKey = null
            this.memoryCachePolicy = null
            this.disabledAnimationDrawable = null
            this.loadingImage = null
            this.errorImage = null
            this.emptyImage = null
            this.target = null
        }

        internal constructor(request: DisplayRequest) {
            this.url = request.url
            this.depth = request.depth
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.resultDiskCacheKey = request.resultDiskCacheKey
            this.resultDiskCachePolicy = request.resultDiskCachePolicy
            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            this.memoryCacheKey = request.memoryCacheKey
            this.memoryCachePolicy = request.memoryCachePolicy
            this.disabledAnimationDrawable = request.disabledAnimationDrawable
            this.loadingImage = request.loadingImage
            this.errorImage = request.errorImage
            this.emptyImage = request.emptyImage
            this.target = request.target
            this.listener = request.listener
            this.networkProgressListener = request.networkProgressListener
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

        fun maxSizeByViewFixedSize(): Builder = apply {
            this.maxSize = MaxSize(SIZE_BY_VIEW_FIXED_SIZE, SIZE_BY_VIEW_FIXED_SIZE)
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
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

        fun resizeByViewFixedSize(
            mode: Resize.Mode = Resize.DEFAULT_MODE,
            scaleType: ScaleType = Resize.DEFAULT_SCALE_TYPE,
            minAspectRatio: Float = Resize.DEFAULT_MIN_ASPECT_RATIO
        ): Builder = apply {
            this.resize = Resize(
                SIZE_BY_VIEW_FIXED_SIZE,
                SIZE_BY_VIEW_FIXED_SIZE,
                mode,
                scaleType,
                minAspectRatio
            )
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

        fun memoryCacheKey(memoryCacheKey: String?): Builder = apply {
            this.memoryCacheKey = memoryCacheKey
        }

        fun memoryCachePolicy(memoryCachePolicy: CachePolicy?): Builder = apply {
            this.memoryCachePolicy = memoryCachePolicy
        }

        fun disabledAnimationDrawable(disabledAnimationDrawable: Boolean?): Builder = apply {
            this.disabledAnimationDrawable = disabledAnimationDrawable
        }

        fun loadingImage(loadingImage: StateImage?): Builder = apply {
            this.loadingImage = loadingImage
        }

        fun errorImage(errorImage: StateImage?): Builder = apply {
            this.errorImage = errorImage
        }

        fun emptyImage(emptyImage: StateImage?): Builder = apply {
            this.emptyImage = emptyImage
        }

        fun target(target: Target?): Builder = apply {
            this.target = target
        }

        fun target(imageView: ImageView): Builder = apply {
            this.target = ImageViewTarget(imageView)
        }

        fun listener(listener: Listener<DisplayRequest, DisplayResult>?): Builder = apply {
            this.listener = listener
        }

        fun networkProgressListener(networkProgressListener: ProgressListener<DisplayRequest>?): Builder =
            apply {
                this.networkProgressListener = networkProgressListener
            }

        fun build(): DisplayRequest = DisplayRequest(
            url = url,
            _depth = depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            _diskCacheKey = diskCacheKey,
            _diskCachePolicy = diskCachePolicy,
            _resultDiskCacheKey = resultDiskCacheKey,
            _resultDiskCachePolicy = resultDiskCachePolicy,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            colorSpace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            _memoryCacheKey = memoryCacheKey,
            _memoryCachePolicy = memoryCachePolicy,
            disabledAnimationDrawable = disabledAnimationDrawable,
            loadingImage = loadingImage,
            errorImage = errorImage,
            emptyImage = emptyImage,
            target = target,
            listener = listener,
            networkProgressListener = networkProgressListener,
        )
    }
}