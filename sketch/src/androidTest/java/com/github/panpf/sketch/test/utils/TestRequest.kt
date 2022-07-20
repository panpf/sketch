package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.ColorSpace
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.http.HttpHeaders
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.BaseImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.resize.fixedPrecision
import com.github.panpf.sketch.resize.fixedScale
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition.Factory
import com.github.panpf.sketch.util.Size

class TestRequest(
    override val context: Context,
    override val uriString: String,
    override val listener: Listener<ImageRequest, Success, Error>?,
    override val progressListener: ProgressListener<ImageRequest>?,
    override val target: Target?,
    override val lifecycle: Lifecycle,
    override val definedOptions: ImageOptions,
    override val defaultOptions: ImageOptions?,
    override val depth: Depth,
    override val parameters: Parameters?,
    override val httpHeaders: HttpHeaders?,
    override val downloadCachePolicy: CachePolicy,
    override val bitmapConfig: BitmapConfig?,
    override val colorSpace: ColorSpace?,
    override val preferQualityOverSpeed: Boolean,
    override val resizeSize: Size?,
    override val resizeSizeResolver: SizeResolver?,
    override val resizePrecisionDecider: PrecisionDecider,
    override val resizeScaleDecider: ScaleDecider,
    override val transformations: List<Transformation>?,
    override val disallowReuseBitmap: Boolean,
    override val ignoreExifOrientation: Boolean,
    override val resultCachePolicy: CachePolicy,
    override val placeholder: StateImage?,
    override val error: ErrorStateImage?,
    override val transition: Factory?,
    override val disallowAnimatedImage: Boolean,
    override val resizeApplyToDrawable: Boolean,
    override val memoryCachePolicy: CachePolicy
) : BaseImageRequest() {

    constructor(
        context: Context,
        uriString: String,
    ) : this(
        context = context,
        uriString = uriString,
        listener = null,
        progressListener = null,
        target = null,
        lifecycle = GlobalLifecycle,
        definedOptions = ImageOptions(),
        defaultOptions = null,
        depth = NETWORK,
        parameters = null,
        httpHeaders = null,
        downloadCachePolicy = ENABLED,
        bitmapConfig = null,
        colorSpace = null,
        preferQualityOverSpeed = true,
        resizeSize = null,
        resizeSizeResolver = null,
        resizePrecisionDecider = fixedPrecision(EXACTLY),
        resizeScaleDecider = fixedScale(FILL),
        transformations = null,
        disallowReuseBitmap = true,
        ignoreExifOrientation = true,
        resultCachePolicy = ENABLED,
        placeholder = null,
        error = null,
        transition = null,
        disallowAnimatedImage = true,
        resizeApplyToDrawable = true,
        memoryCachePolicy = ENABLED
    )

    override fun newBuilder(configBlock: (Builder.() -> Unit)?): Builder {
        TODO("Not yet implemented")
    }

    override fun newRequest(configBlock: (Builder.() -> Unit)?): ImageRequest {
        TODO("Not yet implemented")
    }
}