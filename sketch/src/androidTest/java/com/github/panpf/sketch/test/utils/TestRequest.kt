/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.ColorSpace
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.ComponentRegistry
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
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transition.Transition
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
    @Suppress("OVERRIDE_DEPRECATION") override val preferQualityOverSpeed: Boolean,
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
    override val transitionFactory: Transition.Factory?,
    override val disallowAnimatedImage: Boolean,
    override val resizeApplyToDrawable: Boolean,
    override val memoryCachePolicy: CachePolicy,
    override val componentRegistry: ComponentRegistry?,
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
        resizePrecisionDecider = FixedPrecisionDecider(EXACTLY),
        resizeScaleDecider = FixedScaleDecider(FILL),
        transformations = null,
        disallowReuseBitmap = true,
        ignoreExifOrientation = true,
        resultCachePolicy = ENABLED,
        placeholder = null,
        error = null,
        transitionFactory = null,
        disallowAnimatedImage = true,
        resizeApplyToDrawable = true,
        memoryCachePolicy = ENABLED,
        componentRegistry = null,
    )

    override fun newBuilder(configBlock: (Builder.() -> Unit)?): Builder {
        throw UnsupportedOperationException()
    }

    override fun newRequest(configBlock: (Builder.() -> Unit)?): ImageRequest {
        throw UnsupportedOperationException()
    }
}