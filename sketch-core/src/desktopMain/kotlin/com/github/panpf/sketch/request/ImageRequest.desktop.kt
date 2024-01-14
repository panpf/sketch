package com.github.panpf.sketch.request



/**
 * Sets the transition that crossfade
 */
actual fun ImageRequest.Builder.crossfade(
    durationMillis: Int,
    fadeStart: Boolean,
    preferExactIntrinsicSize: Boolean,
    alwaysUse: Boolean,
): ImageRequest.Builder = apply {
    TODO()
//    transitionFactory(
//        CrossfadeTransition.Factory(
//            durationMillis = durationMillis,
//            fadeStart = fadeStart,
//            preferExactIntrinsicSize = preferExactIntrinsicSize,
//            alwaysUse = alwaysUse
//        )
//    )
}