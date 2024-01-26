package com.github.panpf.sketch.compose.request

import com.github.panpf.sketch.compose.resize.ComposeSizeApplyToDrawHelper
import com.github.panpf.sketch.compose.transition.ComposeCrossfadeTransition
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.transition.Transition


/**
 * Sets the transition that crossfade
 */
fun ImageOptions.Builder.crossfade(
    durationMillis: Int = Transition.DEFAULT_DURATION,
    fadeStart: Boolean = true,
    preferExactIntrinsicSize: Boolean = false,
    alwaysUse: Boolean = false,
): ImageOptions.Builder = apply {
    transitionFactory(
        ComposeCrossfadeTransition.Factory(
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize,
            alwaysUse = alwaysUse
        )
    )
}

fun ImageOptions.Builder.sizeApplyToDraw(apply: Boolean = true): ImageOptions.Builder = apply {
    if (apply) {
        sizeApplyToDraw(ComposeSizeApplyToDrawHelper())
    } else {
        sizeApplyToDraw(null)
    }
}