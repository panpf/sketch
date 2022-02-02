package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.MaxSize
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Resize.Precision
import com.github.panpf.sketch.decode.Resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.Resize.Scale
import com.github.panpf.sketch.decode.Resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.Resize.Scope
import com.github.panpf.sketch.decode.Resize.Scope.All
import com.github.panpf.sketch.decode.transform.Transformation
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.LoadOptions.Builder
import com.github.panpf.sketch.request.internal.ImageRequest

fun LoadOptions(
    configBlock: (Builder.() -> Unit)? = null
): LoadOptions = Builder().apply {
    configBlock?.invoke(this)
}.build()

fun LoadOptionsBuilder(
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder().apply {
    configBlock?.invoke(this)
}

interface LoadOptions : DownloadOptions {

    val maxSize: MaxSize?
    val bitmapConfig: BitmapConfig?

    @get:RequiresApi(VERSION_CODES.O)
    val colorSpace: ColorSpace?

    @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
    val preferQualityOverSpeed: Boolean?
    val resize: Resize?
    val transformations: List<Transformation>?
    val disabledBitmapPool: Boolean?
    val ignoreExifOrientation: Boolean?
    val bitmapResultDiskCachePolicy: CachePolicy?

    @Suppress("DEPRECATION")
    override fun isEmpty(): Boolean =
        super.isEmpty()
                && maxSize == null
                && bitmapConfig == null
                && (VERSION.SDK_INT < VERSION_CODES.O || colorSpace == null)
                && preferQualityOverSpeed == null
                && transformations == null
                && disabledBitmapPool == null
                && ignoreExifOrientation == null
                && bitmapResultDiskCachePolicy == null

    fun newLoadOptionsBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun newLoadOptions(
        configBlock: (Builder.() -> Unit)? = null
    ): LoadOptions = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeaders: HttpHeaders.Builder? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null

        private var maxSize: MaxSize? = null
        private var bitmapConfig: BitmapConfig? = null

        @RequiresApi(VERSION_CODES.O)
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resize: Resize? = null
        private var transformations: MutableSet<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null

        constructor()

        internal constructor(request: LoadOptions) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()

            this.httpHeaders = request.httpHeaders?.newBuilder()
            this.networkContentDiskCachePolicy = request.networkContentDiskCachePolicy

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
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy
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
            scope: Scope = All,
            scale: Scale = CENTER_CROP,
            precision: Precision = KEEP_ASPECT_RATIO,
        ): Builder = apply {
            this.resize = Resize(width, height, scope, scale, precision)
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

        fun ignoreExifOrientation(ignoreExifOrientation: Boolean? = true): Builder =
            apply {
                this.ignoreExifOrientation = ignoreExifOrientation
            }

        fun build(): LoadOptions = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LoadOptionsImpl(
                depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.build(),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                ignoreExifOrientation = ignoreExifOrientation,
            )
        } else {
            LoadOptionsImpl(
                depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.build(),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                ignoreExifOrientation = ignoreExifOrientation,
            )
        }
    }

    private class LoadOptionsImpl(
        override val depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy?,
        override val bitmapResultDiskCachePolicy: CachePolicy?,
        override val maxSize: MaxSize?,
        override val bitmapConfig: BitmapConfig?,
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean?,
        override val resize: Resize?,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean?,
        override val ignoreExifOrientation: Boolean?,
    ) : LoadOptions {

        @RequiresApi(VERSION_CODES.O)
        constructor(
            depth: RequestDepth?,
            parameters: Parameters?,
            httpHeaders: HttpHeaders?,
            networkContentDiskCachePolicy: CachePolicy?,
            bitmapResultDiskCachePolicy: CachePolicy?,
            maxSize: MaxSize?,
            bitmapConfig: BitmapConfig?,
            colorSpace: ColorSpace?,
            preferQualityOverSpeed: Boolean?,
            resize: Resize?,
            transformations: List<Transformation>?,
            disabledBitmapPool: Boolean?,
            ignoreExifOrientation: Boolean?,
        ) : this(
            depth,
            parameters,
            httpHeaders,
            networkContentDiskCachePolicy,
            bitmapResultDiskCachePolicy,
            maxSize,
            bitmapConfig,
            preferQualityOverSpeed,
            resize,
            transformations,
            disabledBitmapPool,
            ignoreExifOrientation,
        ) {
            _colorSpace = colorSpace
        }

        @RequiresApi(VERSION_CODES.O)
        private var _colorSpace: ColorSpace? = null

        @get:RequiresApi(VERSION_CODES.O)
        override val colorSpace: ColorSpace?
            get() = _colorSpace
    }
}