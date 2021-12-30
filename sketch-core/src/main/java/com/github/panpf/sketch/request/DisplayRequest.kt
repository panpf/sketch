package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.internal.DisplayableRequest
import com.github.panpf.sketch.request.internal.LoadableRequest
import com.github.panpf.sketch.transform.Transformation

class DisplayRequest(
    override val uri: Uri,
    override val parameters: Parameters?,
    override val httpHeaders: Map<String, String>?,
    diskCacheKey: String?,
    override val diskCachePolicy: CachePolicy,
    override val maxSize: MaxSize?,
    override val bitmapConfig: BitmapConfig?,
    override val colorSpace: ColorSpace?,
    override val preferQualityOverSpeed: Boolean?,
    override val resize: Resize?,
    override val transformations: List<Transformation>?,
    override val disabledBitmapPool: Boolean?,
    override val disabledCacheResultInDisk: Boolean?,
    override val disabledCorrectExifOrientation: Boolean?,
    memoryCacheKey: String?,
    override val memoryCachePolicy: CachePolicy,
    override val disabledAnimationDrawable: Boolean?,
    override val placeholderDrawable: Drawable?,
    override val errorDrawable: Drawable?,
    override val emptyDrawable: Drawable?,
) : DisplayableRequest {

    override val qualityKey: String? by lazy {
        LoadableRequest.newQualityKey(this)
    }

    override val diskCacheKey: String by lazy {
        diskCacheKey ?: uri.toString()
    }

    override val memoryCacheKey: String by lazy {
        memoryCacheKey ?: "${uri}${qualityKey?.let { "_$it" } ?: ""}"
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

    fun toLoadRequest(): LoadRequest = LoadRequest(
        uri,
        parameters,
        httpHeaders,
        diskCacheKey,
        diskCachePolicy,
        maxSize,
        bitmapConfig,
        colorSpace,
        preferQualityOverSpeed,
        resize,
        transformations,
        disabledBitmapPool,
        disabledCacheResultInDisk,
        disabledCorrectExifOrientation
    )

    companion object {
        fun new(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): DisplayRequest = Builder(uri).apply {
            configBlock?.invoke(this)
        }.build()

        fun new(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): DisplayRequest = Builder(uriString).apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {

        private val uri: Uri
        private var parameters: Parameters?
        private var httpHeaders: Map<String, String>?
        private var diskCacheKey: String?
        private var diskCachePolicy: CachePolicy?
        private var maxSize: MaxSize?
        private var bitmapConfig: BitmapConfig?
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean?
        private var resize: Resize?
        private var transformations: List<Transformation>?
        private var disabledBitmapPool: Boolean?
        private var disabledCacheResultInDisk: Boolean?
        private var disabledCorrectExifOrientation: Boolean?
        private var memoryCacheKey: String?
        private var memoryCachePolicy: CachePolicy?
        private var disabledAnimationDrawable: Boolean?
        private var placeholderDrawable: Drawable?
        private var errorDrawable: Drawable?
        private var emptyDrawable: Drawable?

        constructor(uri: Uri) {
            this.uri = uri
            this.parameters = null
            this.httpHeaders = null
            this.diskCacheKey = null
            this.diskCachePolicy = null
            this.maxSize = MaxSize.SCREEN_SIZE
            this.bitmapConfig = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.colorSpace = null
            }
            this.preferQualityOverSpeed = null
            this.resize = null
            this.transformations = null
            this.disabledBitmapPool = null
            this.disabledCacheResultInDisk = null
            this.disabledCorrectExifOrientation = null
            this.memoryCacheKey = null
            this.memoryCachePolicy = null
            this.disabledAnimationDrawable = null
            this.placeholderDrawable = null
            this.errorDrawable = null
            this.emptyDrawable = null
        }

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: DisplayRequest) {
            this.uri = request.uri
            this.parameters = request.parameters
            this.httpHeaders = request.httpHeaders
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCacheResultInDisk = request.disabledCacheResultInDisk
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
            this.memoryCacheKey = request.memoryCacheKey
            this.memoryCachePolicy = request.memoryCachePolicy
            this.disabledAnimationDrawable = request.disabledAnimationDrawable
            this.placeholderDrawable = request.placeholderDrawable
            this.errorDrawable = request.errorDrawable
            this.emptyDrawable = request.emptyDrawable
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

        fun maxSize(maxSize: MaxSize?): Builder = apply {
            this.maxSize = maxSize
        }

        fun maxSize(width: Int, height: Int): Builder = apply {
            this.maxSize = MaxSize(width, height)
        }

        fun maxSizeByViewFixedSize(): Builder = apply {
            TODO("Not yet implementation")
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

        fun resizeByViewFixedSize(): Builder = apply {
            TODO("Not yet implementation")
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

        fun disabledCacheResultInDisk(cacheResultInDisk: Boolean? = true): Builder = apply {
            this.disabledCacheResultInDisk = cacheResultInDisk
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

        fun placeholderDrawable(placeholderDrawable: Drawable?): Builder = apply {
            this.placeholderDrawable = placeholderDrawable
        }

        fun errorDrawable(errorDrawable: Drawable?): Builder = apply {
            this.errorDrawable = errorDrawable
        }

        fun emptyDrawable(emptyDrawable: Drawable): Builder = apply {
            this.emptyDrawable = emptyDrawable
        }

        fun build(): DisplayRequest = DisplayRequest(
            uri = uri,
            parameters = parameters,
            httpHeaders = httpHeaders,
            diskCacheKey = diskCacheKey,
            diskCachePolicy = diskCachePolicy ?: CachePolicy.ENABLED,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            colorSpace = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCacheResultInDisk = disabledCacheResultInDisk,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            memoryCacheKey = memoryCacheKey,
            memoryCachePolicy = memoryCachePolicy ?: CachePolicy.ENABLED,
            disabledAnimationDrawable = disabledAnimationDrawable,
            placeholderDrawable = placeholderDrawable,
            errorDrawable = errorDrawable,
            emptyDrawable = emptyDrawable,
        )
    }
}