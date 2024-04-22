package com.github.panpf.sketch

import com.github.panpf.sketch.resize.ComposeResizeOnDrawHelper
import com.github.panpf.sketch.transition.ComposeCrossfadeTransition
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.crossfade
import com.github.panpf.sketch.request.resizeOnDraw
import com.github.panpf.sketch.resize.AndroidResizeOnDrawHelper
import com.github.panpf.sketch.transition.CrossfadeTransition

actual fun updateRequestBuilder(request: ImageRequest, builder: Builder) {
    val transitionFactory = request.transitionFactory
    val crossfade = request.crossfade
    if (transitionFactory is CrossfadeTransition.Factory) {
        builder.transitionFactory(
            ComposeCrossfadeTransition.Factory(
                durationMillis = transitionFactory.durationMillis,
                fadeStart = transitionFactory.fadeStart,
                preferExactIntrinsicSize = transitionFactory.preferExactIntrinsicSize,
                alwaysUse = transitionFactory.alwaysUse,
            )
        )
    } else if (transitionFactory == null && crossfade != null) {
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
    if (resizeOnDrawHelper is AndroidResizeOnDrawHelper) {
        builder.resizeOnDraw(ComposeResizeOnDrawHelper)
    } else if (resizeOnDrawHelper == null && resizeOnDraw == true) {
        builder.resizeOnDraw(ComposeResizeOnDrawHelper)
    }
}