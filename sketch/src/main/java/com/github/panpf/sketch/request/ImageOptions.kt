package com.github.panpf.sketch.request

import android.annotation.SuppressLint
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
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.ImageOptions.Builder
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.resize.fixedScale
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.stateimage.newErrorStateImage
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.util.Size
import java.util.LinkedList

fun ImageOptions(
    configBlock: (Builder.() -> Unit)? = null
): ImageOptions = Builder().apply {
    configBlock?.invoke(this)
}.build()

fun ImageOptionsBuilder(
    configBlock: (Builder.() -> Unit)? = null
): Builder = Builder().apply {
    configBlock?.invoke(this)
}

interface ImageOptions {
    /* Base */
    val depth: RequestDepth?
    val parameters: Parameters?

    /* Download */
    val httpHeaders: HttpHeaders?
    val downloadDiskCachePolicy: CachePolicy?

    /* Load */
    val bitmapConfig: BitmapConfig?

    @get:RequiresApi(VERSION_CODES.O)
    val colorSpace: ColorSpace?

    @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
    val preferQualityOverSpeed: Boolean?
    val resizeSize: Size?
    val resizeSizeResolver: SizeResolver?
    val resizePrecisionDecider: PrecisionDecider?
    val resizeScaleDecider: ScaleDecider?
    val transformations: List<Transformation>?
    val disabledReuseBitmap: Boolean?
    val ignoreExifOrientation: Boolean?
    val bitmapResultDiskCachePolicy: CachePolicy?

    /* Display */
    val disabledAnimatedImage: Boolean?
    val placeholderImage: StateImage?
    val errorImage: StateImage?
    val transition: Transition.Factory?
    val resizeApplyToDrawable: Boolean?
    val bitmapMemoryCachePolicy: CachePolicy?

    val depthFrom: String?
        get() = parameters?.value(ImageRequest.REQUEST_DEPTH_FROM)

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun newOptions(
        configBlock: (Builder.() -> Unit)? = null
    ): ImageOptions = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    @Suppress("DEPRECATION")
    fun isEmpty(): Boolean = depth == null
            && parameters?.isEmpty() != false
            && httpHeaders == null
            && downloadDiskCachePolicy == null
            && bitmapConfig == null
            && (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace == null)
            && (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed == null)
            && resizeSize == null
            && resizeSizeResolver == null
            && resizePrecisionDecider == null
            && resizeScaleDecider == null
            && transformations == null
            && disabledReuseBitmap == null
            && ignoreExifOrientation == null
            && bitmapResultDiskCachePolicy == null
            && disabledAnimatedImage == null
            && bitmapMemoryCachePolicy == null
            && placeholderImage == null
            && errorImage == null
            && transition == null
            && resizeApplyToDrawable == null

    class Builder {

        private var depth: RequestDepth? = null
        private var parametersBuilder: Parameters.Builder? = null

        private var httpHeaders: HttpHeaders.Builder? = null
        private var downloadDiskCachePolicy: CachePolicy? = null

        private var bitmapConfig: BitmapConfig? = null

        private var colorSpace: ColorSpace? = null
        private var preferQualityOverSpeed: Boolean? = null
        private var resizeSize: Size? = null
        private var resizeSizeResolver: SizeResolver? = null
        private var resizePrecisionDecider: PrecisionDecider? = null
        private var resizeScaleDecider: ScaleDecider? = null
        private var transformations: MutableList<Transformation>? = null
        private var disabledReuseBitmap: Boolean? = null
        private var ignoreExifOrientation: Boolean? = null
        private var bitmapResultDiskCachePolicy: CachePolicy? = null

        private var bitmapMemoryCachePolicy: CachePolicy? = null
        private var disabledAnimatedImage: Boolean? = null
        private var placeholderImage: StateImage? = null
        private var errorImage: StateImage? = null
        private var transition: Transition.Factory? = null
        private var resizeApplyToDrawable: Boolean? = null

        constructor()

        internal constructor(request: ImageOptions) {
            this.depth = request.depth
            this.parametersBuilder = request.parameters?.newBuilder()

            this.httpHeaders = request.httpHeaders?.newBuilder()
            this.downloadDiskCachePolicy = request.downloadDiskCachePolicy

            this.bitmapConfig = request.bitmapConfig
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                this.colorSpace = request.colorSpace
            }
            if (VERSION.SDK_INT < VERSION_CODES.N) {
                @Suppress("DEPRECATION")
                this.preferQualityOverSpeed = request.preferQualityOverSpeed
            }
            this.resizeSize = request.resizeSize
            this.resizeSizeResolver = request.resizeSizeResolver
            this.resizePrecisionDecider = request.resizePrecisionDecider
            this.resizeScaleDecider = request.resizeScaleDecider
            this.transformations = request.transformations?.toMutableList()
            this.disabledReuseBitmap = request.disabledReuseBitmap
            this.ignoreExifOrientation = request.ignoreExifOrientation
            this.bitmapResultDiskCachePolicy = request.bitmapResultDiskCachePolicy

            this.bitmapMemoryCachePolicy = request.bitmapMemoryCachePolicy
            this.disabledAnimatedImage = request.disabledAnimatedImage
            this.placeholderImage = request.placeholderImage
            this.errorImage = request.errorImage
            this.transition = request.transition
            this.resizeApplyToDrawable = request.resizeApplyToDrawable
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
        fun setParameter(
            key: String, value: Any?, cacheKey: String? = value?.toString()
        ): Builder = apply {
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

        fun downloadDiskCachePolicy(cachePolicy: CachePolicy?): Builder = apply {
            this.downloadDiskCachePolicy = cachePolicy
        }


        fun bitmapConfig(bitmapConfig: BitmapConfig?): Builder = apply {
            this.bitmapConfig = bitmapConfig
        }

        fun bitmapConfig(bitmapConfig: Bitmap.Config?): Builder =
            bitmapConfig(if (bitmapConfig != null) BitmapConfig(bitmapConfig) else null)

        fun lowQualityBitmapConfig(): Builder = bitmapConfig(BitmapConfig.LOW_QUALITY)

        fun middenQualityBitmapConfig(): Builder = bitmapConfig(BitmapConfig.MIDDEN_QUALITY)

        fun highQualityBitmapConfig(): Builder = bitmapConfig(BitmapConfig.HIGH_QUALITY)

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

        fun resize(
            size: Size,
            precision: PrecisionDecider? = null,
            scale: ScaleDecider? = null
        ): Builder = apply {
            this.resizeSize = size
            this.resizePrecisionDecider = precision
            this.resizeScaleDecider = scale
        }

        fun resize(
            size: Size,
            precision: Precision = EXACTLY,
            scale: Scale = CENTER_CROP
        ): Builder = resize(size, fixedPrecision(precision), fixedScale(scale))

        fun resize(size: Size): Builder =
            resize(size, fixedPrecision(EXACTLY), fixedScale(CENTER_CROP))

        fun resize(
            @Px width: Int, @Px height: Int,
            precision: PrecisionDecider? = null,
            scale: ScaleDecider? = null
        ): Builder = resize(Size(width, height), precision, scale)

        fun resize(
            @Px width: Int, @Px height: Int,
            precision: Precision = EXACTLY,
            scale: Scale = CENTER_CROP
        ): Builder = resize(Size(width, height), fixedPrecision(precision), fixedScale(scale))

        fun resize(@Px width: Int, @Px height: Int): Builder =
            resize(Size(width, height), fixedPrecision(EXACTLY), fixedScale(CENTER_CROP))

        fun resizeSize(resizeSize: Size?): Builder = apply {
            this.resizeSize = resizeSize
        }

        fun resizeSize(@Px width: Int, @Px height: Int): Builder =
            resizeSize(Size(width, height))

        fun resizeSize(sizeResolver: SizeResolver?): Builder = apply {
            this.resizeSizeResolver = sizeResolver
        }

        fun resizePrecision(precisionDecider: PrecisionDecider?): Builder = apply {
            this.resizePrecisionDecider = precisionDecider
        }

        fun resizePrecision(precision: Precision?): Builder =
            resizePrecision(precision?.let { fixedPrecision(it) })

        fun resizeScale(scaleDecider: ScaleDecider?): Builder = apply {
            this.resizeScaleDecider = scaleDecider
        }

        fun resizeScale(scale: Scale): Builder = resizeScale(fixedScale(scale))

        fun transformations(transformations: List<Transformation>?): Builder = apply {
            this.transformations = transformations?.toMutableList()
        }

        fun transformations(vararg transformations: Transformation): Builder =
            transformations(transformations.toList())

        fun addTransformations(transformations: List<Transformation>): Builder = apply {
            val filterTransformations = transformations.filter { newTransformation ->
                this.transformations?.find { it.key == newTransformation.key } == null
            }
            this.transformations = (this.transformations ?: LinkedList()).apply {
                addAll(filterTransformations)
            }
        }

        fun addTransformations(vararg transformations: Transformation): Builder =
            addTransformations(transformations.toList())

        fun removeTransformations(removeTransformations: List<Transformation>): Builder =
            apply {
                this.transformations = this.transformations?.filter { oldTransformation ->
                    removeTransformations.find { it.key == oldTransformation.key } == null
                }?.toMutableList()
            }

        fun removeTransformations(vararg removeTransformations: Transformation): Builder =
            removeTransformations(removeTransformations.toList())

        fun disabledReuseBitmap(disabledReuseBitmap: Boolean? = true): Builder = apply {
            this.disabledReuseBitmap = disabledReuseBitmap
        }

        fun ignoreExifOrientation(ignoreExifOrientation: Boolean? = true): Builder = apply {
            this.ignoreExifOrientation = ignoreExifOrientation
        }

        fun bitmapResultDiskCachePolicy(bitmapResultDiskCachePolicy: CachePolicy?): Builder =
            apply {
                this.bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy
            }


        fun disabledAnimatedImage(disabledAnimatedImage: Boolean? = true): Builder = apply {
            this.disabledAnimatedImage = disabledAnimatedImage
        }

        fun placeholder(placeholderImage: StateImage?): Builder = apply {
            this.placeholderImage = placeholderImage
        }

        fun placeholder(placeholderDrawable: Drawable?): Builder =
            placeholder(placeholderDrawable?.let { DrawableStateImage(it) })

        fun placeholder(@DrawableRes placeholderDrawableResId: Int?): Builder =
            placeholder(placeholderDrawableResId?.let { DrawableStateImage(it) })

        fun error(
            errorImage: StateImage?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = apply {
            this.errorImage = errorImage?.let {
                if (configBlock != null) newErrorStateImage(it, configBlock) else it
            }
        }

        fun error(
            errorDrawable: Drawable?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = error(errorDrawable?.let { DrawableStateImage(it) }, configBlock)

        fun error(
            errorDrawableResId: Int?,
            configBlock: (ErrorStateImage.Builder.() -> Unit)? = null
        ): Builder = error(errorDrawableResId?.let { DrawableStateImage(it) }, configBlock)

        fun transition(transition: Transition.Factory?): Builder = apply {
            this.transition = transition
        }

        fun crossfade(
            durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
            preferExactIntrinsicSize: Boolean = false
        ): Builder = apply {
            transition(CrossfadeTransition.Factory(durationMillis, preferExactIntrinsicSize))
        }

        fun resizeApplyToDrawable(resizeApplyToDrawable: Boolean? = true): Builder = apply {
            this.resizeApplyToDrawable = resizeApplyToDrawable
        }

        fun bitmapMemoryCachePolicy(bitmapMemoryCachePolicy: CachePolicy?): Builder = apply {
            this.bitmapMemoryCachePolicy = bitmapMemoryCachePolicy
        }


        fun merge(options: ImageOptions?): Builder = apply {
            if (options == null) return@apply
            if (this.depth == null) {
                this.depth = options.depth
            }
            options.parameters?.takeIf { it.isNotEmpty() }?.forEach { entry ->
                if (parametersBuilder?.exist(entry.first) != true) {
                    setParameter(entry.first, entry.second.value, entry.second.cacheKey)
                }
            }

            options.httpHeaders?.takeIf { !it.isEmpty() }?.let { headers ->
                headers.addList.forEach {
                    addHttpHeader(it.first, it.second)
                }
                headers.setList.forEach {
                    if (httpHeaders?.setExist(it.first) != true) {
                        setHttpHeader(it.first, it.second)
                    }
                }
            }
            if (this.downloadDiskCachePolicy == null) {
                this.downloadDiskCachePolicy = options.downloadDiskCachePolicy
            }

            if (this.bitmapConfig == null) {
                this.bitmapConfig = options.bitmapConfig
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O && this.colorSpace == null) {
                this.colorSpace = options.colorSpace
            }
            if (VERSION.SDK_INT < VERSION_CODES.N && this.preferQualityOverSpeed == null) {
                @Suppress("DEPRECATION")
                this.preferQualityOverSpeed = options.preferQualityOverSpeed
            }
            if (this.resizeSize == null) {
                this.resizeSize = options.resizeSize
            }
            if (this.resizeSizeResolver == null) {
                this.resizeSizeResolver = options.resizeSizeResolver
            }
            if (this.resizePrecisionDecider == null) {
                this.resizePrecisionDecider = options.resizePrecisionDecider
            }
            if (this.resizeScaleDecider == null) {
                this.resizeScaleDecider = options.resizeScaleDecider
            }
            options.transformations?.takeIf { it.isNotEmpty() }?.let {
                addTransformations(it)
            }
            if (this.disabledReuseBitmap == null) {
                this.disabledReuseBitmap = options.disabledReuseBitmap
            }
            if (this.ignoreExifOrientation == null) {
                this.ignoreExifOrientation = options.ignoreExifOrientation
            }
            if (this.bitmapResultDiskCachePolicy == null) {
                this.bitmapResultDiskCachePolicy = options.bitmapResultDiskCachePolicy
            }

            if (this.disabledAnimatedImage == null) {
                this.disabledAnimatedImage = options.disabledAnimatedImage
            }
            if (this.placeholderImage == null) {
                this.placeholderImage = options.placeholderImage
            }
            if (this.errorImage == null) {
                this.errorImage = options.errorImage
            }
            if (this.transition == null) {
                this.transition = options.transition
            }
            if (this.resizeApplyToDrawable == null) {
                this.resizeApplyToDrawable = options.resizeApplyToDrawable
            }
            if (this.bitmapMemoryCachePolicy == null) {
                this.bitmapMemoryCachePolicy = options.bitmapMemoryCachePolicy
            }
        }


        @SuppressLint("NewApi")
        fun build(): ImageOptions = ImageOptionsImpl(
            depth = depth,
            parameters = parametersBuilder?.build(),
            httpHeaders = httpHeaders?.build(),
            downloadDiskCachePolicy = downloadDiskCachePolicy,
            bitmapResultDiskCachePolicy = bitmapResultDiskCachePolicy,
            bitmapConfig = bitmapConfig,
            colorSpace = if (VERSION.SDK_INT >= VERSION_CODES.O) colorSpace else null,
            preferQualityOverSpeed = if (VERSION.SDK_INT < VERSION_CODES.N) preferQualityOverSpeed else null,
            resizeSize = resizeSize,
            resizeSizeResolver = resizeSizeResolver,
            resizePrecisionDecider = resizePrecisionDecider,
            resizeScaleDecider = resizeScaleDecider,
            transformations = transformations,
            disabledReuseBitmap = disabledReuseBitmap,
            ignoreExifOrientation = ignoreExifOrientation,
            bitmapMemoryCachePolicy = bitmapMemoryCachePolicy,
            disabledAnimatedImage = disabledAnimatedImage,
            placeholderImage = placeholderImage,
            errorImage = errorImage,
            transition = transition,
            resizeApplyToDrawable = resizeApplyToDrawable,
        )
    }

    class ImageOptionsImpl(
        override val depth: RequestDepth?,
        override val parameters: Parameters?,

        override val httpHeaders: HttpHeaders?,
        override val downloadDiskCachePolicy: CachePolicy?,

        override val bitmapConfig: BitmapConfig?,
        @get:RequiresApi(VERSION_CODES.O)
        override val colorSpace: ColorSpace?,
        @Deprecated("From Android N (API 24), this is ignored. The output will always be high quality.")
        override val preferQualityOverSpeed: Boolean?,
        override val resizeSize: Size?,
        override val resizeSizeResolver: SizeResolver?,
        override val resizePrecisionDecider: PrecisionDecider?,
        override val resizeScaleDecider: ScaleDecider?,
        override val transformations: List<Transformation>?,
        override val disabledReuseBitmap: Boolean?,
        override val ignoreExifOrientation: Boolean?,
        override val bitmapResultDiskCachePolicy: CachePolicy?,
        override val disabledAnimatedImage: Boolean?,
        override val bitmapMemoryCachePolicy: CachePolicy?,
        override val placeholderImage: StateImage?,
        override val errorImage: StateImage?,
        override val transition: Transition.Factory?,
        override val resizeApplyToDrawable: Boolean?,
    ) : ImageOptions {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ImageOptionsImpl

            if (depth != other.depth) return false
            if (parameters != other.parameters) return false
            if (httpHeaders != other.httpHeaders) return false
            if (downloadDiskCachePolicy != other.downloadDiskCachePolicy) return false
            if (bitmapConfig != other.bitmapConfig) return false
            if (VERSION.SDK_INT >= VERSION_CODES.O && colorSpace != other.colorSpace) return false
            @Suppress("DEPRECATION") if (VERSION.SDK_INT < VERSION_CODES.N && preferQualityOverSpeed != other.preferQualityOverSpeed) return false
            if (resizeSize != other.resizeSize) return false
            if (resizeSizeResolver != other.resizeSizeResolver) return false
            if (resizePrecisionDecider != other.resizePrecisionDecider) return false
            if (resizeScaleDecider != other.resizeScaleDecider) return false
            if (transformations != other.transformations) return false
            if (disabledReuseBitmap != other.disabledReuseBitmap) return false
            if (ignoreExifOrientation != other.ignoreExifOrientation) return false
            if (bitmapResultDiskCachePolicy != other.bitmapResultDiskCachePolicy) return false
            if (disabledAnimatedImage != other.disabledAnimatedImage) return false
            if (bitmapMemoryCachePolicy != other.bitmapMemoryCachePolicy) return false
            if (placeholderImage != other.placeholderImage) return false
            if (errorImage != other.errorImage) return false
            if (transition != other.transition) return false
            if (resizeApplyToDrawable != other.resizeApplyToDrawable) return false

            return true
        }

        override fun hashCode(): Int {
            var result = depth?.hashCode() ?: 0
            result = 31 * result + (parameters?.hashCode() ?: 0)
            result = 31 * result + (httpHeaders?.hashCode() ?: 0)
            result = 31 * result + (downloadDiskCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (bitmapConfig?.hashCode() ?: 0)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                result = 31 * result + (colorSpace?.hashCode() ?: 0)
            }
            if (VERSION.SDK_INT < VERSION_CODES.N) {
                @Suppress("DEPRECATION")
                result = 31 * result + (preferQualityOverSpeed?.hashCode() ?: 0)
            }
            result = 31 * result + (resizeSize?.hashCode() ?: 0)
            result = 31 * result + (resizeSizeResolver?.hashCode() ?: 0)
            result = 31 * result + (resizePrecisionDecider?.hashCode() ?: 0)
            result = 31 * result + (resizeScaleDecider?.hashCode() ?: 0)
            result = 31 * result + (transformations?.hashCode() ?: 0)
            result = 31 * result + (disabledReuseBitmap?.hashCode() ?: 0)
            result = 31 * result + (ignoreExifOrientation?.hashCode() ?: 0)
            result = 31 * result + (bitmapResultDiskCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (disabledAnimatedImage?.hashCode() ?: 0)
            result = 31 * result + (bitmapMemoryCachePolicy?.hashCode() ?: 0)
            result = 31 * result + (placeholderImage?.hashCode() ?: 0)
            result = 31 * result + (errorImage?.hashCode() ?: 0)
            result = 31 * result + (transition?.hashCode() ?: 0)
            result = 31 * result + (resizeApplyToDrawable?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return buildString {
                append("ImageOptionsImpl(")
                append("depth=$depth, ")
                append("parameters=$parameters, ")
                append("httpHeaders=$httpHeaders, ")
                append("downloadDiskCachePolicy=$downloadDiskCachePolicy, ")
                append("bitmapConfig=$bitmapConfig, ")
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    append("colorSpace=$colorSpace, ")
                }
                if (VERSION.SDK_INT < VERSION_CODES.N) {
                    @Suppress("DEPRECATION")
                    append("preferQualityOverSpeed=$preferQualityOverSpeed, ")
                }
                append("resizeSize=$resizeSize, ")
                append("resizeSizeResolver=$resizeSizeResolver, ")
                append("resizePrecisionDecider=$resizePrecisionDecider, ")
                append("resizeScaleDecider=$resizeScaleDecider, ")
                append("transformations=$transformations, ")
                append("disabledReuseBitmap=$disabledReuseBitmap, ")
                append("ignoreExifOrientation=$ignoreExifOrientation, ")
                append("bitmapResultDiskCachePolicy=$bitmapResultDiskCachePolicy, ")
                append("disabledAnimatedImage=$disabledAnimatedImage, ")
                append("bitmapMemoryCachePolicy=$bitmapMemoryCachePolicy, ")
                append("placeholderImage=$placeholderImage, ")
                append("errorImage=$errorImage, ")
                append("transition=$transition, ")
                append("resizeApplyToDrawable=$resizeApplyToDrawable")
                append(")")
            }
        }
    }
}