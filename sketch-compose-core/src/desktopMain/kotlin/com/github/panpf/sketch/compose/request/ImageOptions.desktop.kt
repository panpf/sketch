package com.github.panpf.sketch.compose.request

import com.github.panpf.sketch.compose.transition.CrossfadeComposeTransition
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
        CrossfadeComposeTransition.Factory(
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize,
            alwaysUse = alwaysUse
        )
    )
}