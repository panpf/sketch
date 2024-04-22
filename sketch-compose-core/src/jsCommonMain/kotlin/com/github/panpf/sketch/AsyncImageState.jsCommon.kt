package com.github.panpf.sketch

import com.github.panpf.sketch.request.internal.SkiaBitmapToComposeBitmapRequestInterceptor
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.crossfade
import com.github.panpf.sketch.request.resizeOnDraw
import com.github.panpf.sketch.resize.ComposeResizeOnDrawHelper
import com.github.panpf.sketch.transition.ComposeCrossfadeTransition

actual fun updateRequestBuilder(request: ImageRequest, builder: Builder) {
    val transitionFactory = request.transitionFactory
    val crossfade = request.crossfade
    if (transitionFactory == null && crossfade != null) {
        builder.transitionFactory(
            ComposeCrossfadeTransition.Factory(
                durationMillis = crossfade.durationMillis,
                fadeStart = crossfade.fadeStart,
                preferExactIntrinsicSize = crossfade.preferExactIntrinsicSize,
                alwaysUse = crossfade.alwaysUse,
            )
        )
    }

    val resizeOnDrawHelper = request.resizeOnDrawHelper
    val resizeOnDraw = request.resizeOnDraw
    if (resizeOnDrawHelper == null && resizeOnDraw == true) {
        builder.resizeOnDraw(ComposeResizeOnDrawHelper)
    }

    builder.mergeComponents {
        addRequestInterceptor(SkiaBitmapToComposeBitmapRequestInterceptor())
    }
}