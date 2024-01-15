package com.github.panpf.sketch.compose

import com.github.panpf.sketch.compose.transition.CrossfadeComposeTransition
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.transition.CrossfadeTransition

actual fun updateRequestBuilder(request: ImageRequest, builder: Builder) {
    val transitionFactory = request.transitionFactory
    if (transitionFactory is CrossfadeTransition.Factory) {
        builder.transitionFactory(
            CrossfadeComposeTransition.Factory(
                durationMillis = transitionFactory.durationMillis,
                fadeStart = transitionFactory.fadeStart,
                preferExactIntrinsicSize = transitionFactory.preferExactIntrinsicSize,
                alwaysUse = transitionFactory.alwaysUse,
            )
        )
    }
}