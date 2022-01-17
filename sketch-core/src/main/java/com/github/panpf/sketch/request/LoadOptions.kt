package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.MaxSize
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Resize.Precision.KEEP_ASPECT_RATIO
import com.github.panpf.sketch.decode.Resize.Scale
import com.github.panpf.sketch.decode.Resize.Scale.CENTER_CROP
import com.github.panpf.sketch.decode.Resize.Scope
import com.github.panpf.sketch.decode.Resize.Scope.All
import com.github.panpf.sketch.decode.transform.Transformation
import com.github.panpf.sketch.request.internal.ImageRequest

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
    val disabledCorrectExifOrientation: Boolean?
    val bitmapResultDiskCachePolicy: CachePolicy?

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

    companion object {
        fun new(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): LoadOptions = Builder().apply {
            configBlock?.invoke(this)
        }.build()

        fun newBuilder(
            uri: Uri,
            configBlock: (Builder.() -> Unit)? = null
        ): Builder = Builder().apply {
            configBlock?.invoke(this)
        }
    }

    class Builder {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeaders: MutableMap<String, String>? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null

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

        constructor()

        internal constructor(request: LoadOptions) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()

            this.httpHeaders = request.httpHeaders?.toMutableMap()
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
            this.disabledCorrectExifOrientation = request.disabledCorrectExifOrientation
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
            precision: Resize.Precision = KEEP_ASPECT_RATIO,
            scale: Scale = CENTER_CROP,
            scope: Scope = All,
        ): Builder = apply {
            this.resize = Resize(width, height, precision, scale, scope)
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

        fun build(): LoadOptions = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LoadOptionsImpl(
                depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.toMap(),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            )
        } else {
            LoadOptionsImpl(
                depth = depth,
                parameters = parametersBuilder?.build(),
                httpHeaders = httpHeaders?.toMap(),
                networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                maxSize = maxSize,
                bitmapConfig = bitmapConfig,
                preferQualityOverSpeed = preferQualityOverSpeed,
                resize = resize,
                transformations = transformations?.toList(),
                disabledBitmapPool = disabledBitmapPool,
                disabledCorrectExifOrientation = disabledCorrectExifOrientation,
            )
        }
    }

    private class LoadOptionsImpl(
        override val depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: Map<String, String>?,
        override val networkContentDiskCachePolicy: CachePolicy?,
        override val bitmapResultDiskCachePolicy: CachePolicy?,
        override val maxSize: MaxSize?,
        override val bitmapConfig: BitmapConfig?,
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean?,
        override val resize: Resize?,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean?,
        override val disabledCorrectExifOrientation: Boolean?,
    ) : LoadOptions {

        @RequiresApi(VERSION_CODES.O)
        constructor(
            depth: RequestDepth?,
            parameters: Parameters?,
            httpHeaders: Map<String, String>?,
            networkContentDiskCachePolicy: CachePolicy?,
            bitmapResultDiskCachePolicy: CachePolicy?,
            maxSize: MaxSize?,
            bitmapConfig: BitmapConfig?,
            colorSpace: ColorSpace?,
            preferQualityOverSpeed: Boolean?,
            resize: Resize?,
            transformations: List<Transformation>?,
            disabledBitmapPool: Boolean?,
            disabledCorrectExifOrientation: Boolean?,
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
            disabledCorrectExifOrientation,
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