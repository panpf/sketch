package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.resize.NewSize
import com.github.panpf.sketch.decode.resize.Precision
import com.github.panpf.sketch.decode.resize.PrecisionDecider
import com.github.panpf.sketch.decode.resize.Resize
import com.github.panpf.sketch.decode.resize.Scale
import com.github.panpf.sketch.decode.transform.Transformation
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ViewBoundsSize
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition

fun DisplayOptions(
    configBlock: (DisplayOptions.Builder.() -> Unit)? = null
): DisplayOptions = DisplayOptions.Builder().apply {
    configBlock?.invoke(this)
}.build()

fun DisplayOptionsBuilder(
    configBlock: (DisplayOptions.Builder.() -> Unit)? = null
): DisplayOptions.Builder = DisplayOptions.Builder().apply {
    configBlock?.invoke(this)
}

interface DisplayOptions : LoadOptions {

    val disabledAnimationDrawable: Boolean?
    val bitmapMemoryCachePolicy: CachePolicy?
    val placeholderImage: StateImage?
    val errorImage: StateImage?
    val transition: Transition.Factory?

    override fun isEmpty(): Boolean =
        super.isEmpty()
                && disabledAnimationDrawable == null
                && bitmapMemoryCachePolicy == null
                && placeholderImage == null
                && errorImage == null
                && transition == null

    fun newDisplayOptionsBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun newDisplayOptions(
        configBlock: (Builder.() -> Unit)? = null
    ): DisplayOptions = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    class Builder {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeaders: HttpHeaders.Builder? = null
        private var networkContentDiskCachePolicy: CachePolicy? = null

        private var bitmapConfig: BitmapConfig? = null

        @RequiresApi(VERSION_CODES.O)
        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resize: Resize? = null
        private var transformations: List<Transformation>? = null
        private var disabledBitmapPool: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null

        private var bitmapMemoryCachePolicy: CachePolicy? = null
        private var disabledAnimationDrawable: Boolean? = null
        private var placeholderImage: StateImage? = null
        private var errorImage: StateImage? = null
        private var transition: Transition.Factory? = null

        constructor()

        internal constructor(request: DisplayOptions) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()
            this.httpHeaders = request.httpHeaders?.newBuilder()
            this.networkContentDiskCachePolicy = request.networkContentDiskCachePolicy
            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            @Suppress("DEPRECATION")
            this.preferQualityOverSpeed = request.preferQualityOverSpeed
            this.resize = request.resize
            this.transformations = request.transformations
            this.disabledBitmapPool = request.disabledBitmapPool
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy
            this.bitmapMemoryCachePolicy = request.bitmapMemoryCachePolicy
            this.disabledAnimationDrawable = request.disabledAnimationDrawable
            this.placeholderImage = request.placeholderImage
            this.errorImage = request.errorImage
            this.transition = request.transition
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

        fun resize(resize: Resize?): Builder = apply {
            this.resize = resize
        }

        fun resize(
            newSize: NewSize,
            precision: Precision = Precision.LESS_PIXELS,
            scale: Scale = Scale.CENTER_CROP,
        ): Builder = apply {
            this.resize = Resize(newSize, precision, scale)
        }

        fun resize(
            @Px width: Int,
            @Px height: Int,
            precision: Precision = Precision.LESS_PIXELS,
            scale: Scale = Scale.CENTER_CROP,
        ): Builder = apply {
            this.resize = Resize(width, height, precision, scale)
        }

        fun resize(
            @Px width: Int,
            @Px height: Int,
            precisionDecider: PrecisionDecider,
            scale: Scale = Scale.CENTER_CROP,
        ): Builder = apply {
            this.resize = Resize(width, height, precisionDecider, scale)
        }

        fun resizeByViewBounds(
            precisionDecider: PrecisionDecider,
            scale: Scale = Scale.CENTER_CROP,
        ): Builder = apply {
            this.resize = Resize(ViewBoundsSize, precisionDecider, scale)
        }

        fun resizeByViewBounds(
            precision: Precision = Precision.LESS_PIXELS,
            scale: Scale = Scale.CENTER_CROP,
        ): Builder = apply {
            this.resize = Resize(ViewBoundsSize, precision, scale)
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

        fun crossfadeTransition(durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION, preferExactIntrinsicSize: Boolean = false): Builder = apply {
            transition(CrossfadeTransition.Factory(durationMillis, preferExactIntrinsicSize))
        }

        fun build(): DisplayOptions {
            return if (VERSION.SDK_INT >= VERSION_CODES.O) {
                DisplayOptionsImpl(
                    depth = depth,
                    parameters = parametersBuilder?.build(),
                    httpHeaders = httpHeaders?.build(),
                    networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                    bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                    bitmapConfig = bitmapConfig,
                    colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
                    preferQualityOverSpeed = preferQualityOverSpeed,
                    resize = resize,
                    transformations = transformations,
                    disabledBitmapPool = disabledBitmapPool,
                    ignoreExifOrientation = ignoreExifOrientation,
                    bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
                    disabledAnimationDrawable = disabledAnimationDrawable,
                    placeholderImage = placeholderImage,
                    errorImage = errorImage,
                    transition = transition,
                )
            } else {
                DisplayOptionsImpl(
                    depth = depth,
                    parameters = parametersBuilder?.build(),
                    httpHeaders = httpHeaders?.build(),
                    networkContentDiskCachePolicy = networkContentDiskCachePolicy,
                    bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
                    bitmapConfig = bitmapConfig,
                    preferQualityOverSpeed = preferQualityOverSpeed,
                    resize = resize,
                    transformations = transformations,
                    disabledBitmapPool = disabledBitmapPool,
                    ignoreExifOrientation = ignoreExifOrientation,
                    bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
                    disabledAnimationDrawable = disabledAnimationDrawable,
                    placeholderImage = placeholderImage,
                    errorImage = errorImage,
                    transition = transition,
                )
            }
        }
    }

    private class DisplayOptionsImpl(
        override val depth: RequestDepth?,
        override val parameters: Parameters?,
        override val httpHeaders: HttpHeaders?,
        override val networkContentDiskCachePolicy: CachePolicy?,
        override val bitmapResultDiskCachePolicy: CachePolicy?,
        override val bitmapConfig: BitmapConfig?,
        @Suppress("OverridingDeprecatedMember")
        override val preferQualityOverSpeed: Boolean?,
        override val resize: Resize?,
        override val transformations: List<Transformation>?,
        override val disabledBitmapPool: Boolean?,
        override val ignoreExifOrientation: Boolean?,
        override val bitmapMemoryCachePolicy: CachePolicy?,
        override val disabledAnimationDrawable: Boolean?,
        override val placeholderImage: StateImage?,
        override val errorImage: StateImage?,
        override val transition: Transition.Factory?,
    ) : DisplayOptions {

        @RequiresApi(VERSION_CODES.O)
        constructor(
            depth: RequestDepth?,
            parameters: Parameters?,
            httpHeaders: HttpHeaders?,
            networkContentDiskCachePolicy: CachePolicy?,
            bitmapResultDiskCachePolicy: CachePolicy?,
            bitmapConfig: BitmapConfig?,
            colorSpace: ColorSpace?,
            preferQualityOverSpeed: Boolean?,
            resize: Resize?,
            transformations: List<Transformation>?,
            disabledBitmapPool: Boolean?,
            ignoreExifOrientation: Boolean?,
            bitmapMemoryCachePolicy: CachePolicy?,
            disabledAnimationDrawable: Boolean?,
            placeholderImage: StateImage?,
            errorImage: StateImage?,
            transition: Transition.Factory?,
        ) : this(
            depth = depth,
            parameters = parameters,
            httpHeaders = httpHeaders,
            networkContentDiskCachePolicy = networkContentDiskCachePolicy,
            bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
            bitmapConfig = bitmapConfig,
            preferQualityOverSpeed = preferQualityOverSpeed,
            resize = resize,
            transformations = transformations,
            disabledBitmapPool = disabledBitmapPool,
            ignoreExifOrientation = ignoreExifOrientation,
            bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
            disabledAnimationDrawable = disabledAnimationDrawable,
            placeholderImage = placeholderImage,
            errorImage = errorImage,
            transition = transition,
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