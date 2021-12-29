package com.github.panpf.sketch.load

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.github.panpf.sketch.common.LoadableRequest
import com.github.panpf.sketch.common.cache.CachePolicy
import com.github.panpf.sketch.load.transform.Transformation

class LoadRequest(
    override val uri: Uri,
    override val extras: Bundle?,
    override val diskCacheKey: String,
    override val diskCachePolicy: CachePolicy,
    override val maxSize: MaxSize?,
    override val bitmapConfig: BitmapConfig?,
    override val inPreferQualityOverSpeed: Boolean?,
    override val resize: Resize?,
    override val transformations: List<Transformation>?,
    override val disabledBitmapPool: Boolean?,
    override val disabledCacheResultInDisk: Boolean?,
    override val disabledCorrectExifOrientation: Boolean?,
) : LoadableRequest {

    val resultCacheKey: String? = buildString {
        if (maxSize != null) {
            if (length > 0) append("_")
            append(maxSize.cacheKey)
        }
        if (bitmapConfig != null) {
            if (length > 0) append("_")
            append(bitmapConfig.cacheKey)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && inPreferQualityOverSpeed == true) {
            if (length > 0) append("_")
            append("inPreferQualityOverSpeed")
        }
        if (resize != null) {
            if (length > 0) append("_")
            append(resize.cacheKey)
        }
        transformations?.forEach {
            if (length > 0) append("_")
            append(it.cacheKey)
        }
        if (disabledCorrectExifOrientation != true) {
            if (length > 0) append("_")
            append("CorrectExifOrientation")
        }
    }.takeIf { it.isNotEmpty() }

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): LoadRequest = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    companion object {
        fun new(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadRequest = Builder(uri).apply {
            configBlock?.invoke(this)
        }.build()

        fun new(
            uriString: String,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadRequest = Builder(uriString).apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder {
        private val uri: Uri
        private var extras: Bundle?
        private var diskCacheKey: String?
        private var diskCachePolicy: CachePolicy?
        private var maxSize: MaxSize?
        private var bitmapConfig: BitmapConfig?
        private var inPreferQualityOverSpeed: Boolean?
        private var resize: Resize?
        private var transformations: List<Transformation>?
        private var disabledBitmapPool: Boolean?
        private var disabledCacheResultInDisk: Boolean?
        private var disabledCorrectExifOrientation: Boolean?

        constructor(uri: Uri) {
            this.uri = uri
            this.extras = null
            this.diskCacheKey = null
            this.diskCachePolicy = null
            this.maxSize = MaxSize.SCREEN_SIZE
            this.bitmapConfig = null
            this.inPreferQualityOverSpeed = null
            this.resize = null
            this.transformations = null
            this.disabledBitmapPool = null
            this.disabledCacheResultInDisk = null
            this.disabledCorrectExifOrientation = null
        }

        constructor(uriString: String) : this(Uri.parse(uriString))

        internal constructor(request: LoadRequest) {
            this.uri = request.uri
            this.extras = request.extras
            this.diskCacheKey = request.diskCacheKey
            this.diskCachePolicy = request.diskCachePolicy
            this.maxSize = request.maxSize
            this.bitmapConfig = request.bitmapConfig
            this.inPreferQualityOverSpeed = request.inPreferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.disabledCacheResultInDisk = request.disabledCacheResultInDisk
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
        }

        fun extras(extras: Bundle?): Builder = apply {
            this.extras = extras
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

        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        fun bitmapConfig(bitmapConfig: Bitmap.Config?): Builder = apply {
            this.bitmapConfig = if (bitmapConfig != null) BitmapConfig(bitmapConfig) else null
        }

        fun lowQualityBitmapConfig(): Builder = apply {
            this.bitmapConfig = BitmapConfig.LOW_QUALITY
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
        fun inPreferQualityOverSpeed(inPreferQualityOverSpeed: Boolean?): Builder = apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                this.inPreferQualityOverSpeed = inPreferQualityOverSpeed
            }
        }

        fun resize(resize: Resize?): Builder = apply {
            this.resize = resize
        }

        fun resize(width: Int, height: Int): Builder = apply {
            this.resize = Resize(width, height)
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

        fun build(): LoadRequest = LoadRequest(
            uri = uri,
            extras = extras,
            diskCacheKey = diskCacheKey ?: uri.toString(),
            diskCachePolicy = diskCachePolicy ?: CachePolicy.ENABLED,
            maxSize = maxSize,
            bitmapConfig = bitmapConfig,
            inPreferQualityOverSpeed = inPreferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            disabledCacheResultInDisk = disabledCacheResultInDisk,
            disabledCorrectExifOrientation = disabledCorrectExifOrientation,
        )
    }
}