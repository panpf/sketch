package com.github.panpf.sketch.compose.request

import com.github.panpf.sketch.compose.resize.ComposeSizeApplyToDrawHelper
import com.github.panpf.sketch.compose.transition.ComposeCrossfadeTransition
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.transition.Transition

/**
 * Sets the transition that crossfade
 */
fun ImageRequest.Builder.crossfade(
    durationMillis: Int = Transition.DEFAULT_DURATION,
    fadeStart: Boolean = true,
    preferExactIntrinsicSize: Boolean = false,
    alwaysUse: Boolean = false,
): ImageRequest.Builder = apply {
    transitionFactory(
        ComposeCrossfadeTransition.Factory(
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize,
            alwaysUse = alwaysUse
        )
    )
}

fun ImageRequest.Builder.sizeApplyToDraw(apply: Boolean = true): ImageRequest.Builder = apply {
    if (apply) {
        sizeApplyToDraw(ComposeSizeApplyToDrawHelper())
    } else {
        sizeApplyToDraw(null)
    }
}