package com.github.panpf.sketch.transition

import com.github.panpf.sketch.util.Key

data class Crossfade(
    val durationMillis: Int = DEFAULT_DURATION_MILLIS,
    val fadeStart: Boolean = DEFAULT_FADE_START,
    val preferExactIntrinsicSize: Boolean = DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
    val alwaysUse: Boolean = DEFAULT_ALWAYS_USE,
) : Key {

    companion object {
        const val DEFAULT_DURATION_MILLIS: Int = Transition.DEFAULT_DURATION
        const val DEFAULT_FADE_START: Boolean = true
        const val DEFAULT_PREFER_EXACT_INTRINSIC_SIZE: Boolean = false
        const val DEFAULT_ALWAYS_USE: Boolean = false
    }

    override val key: String =
        "Crossfade(durationMillis=$durationMillis,fadeStart=$fadeStart,preferExactIntrinsicSize=$preferExactIntrinsicSize,alwaysUse=$alwaysUse)"

    override fun toString(): String {
        return "Crossfade(durationMillis=$durationMillis, fadeStart=$fadeStart, preferExactIntrinsicSize=$preferExactIntrinsicSize, alwaysUse=$alwaysUse)"
    }
}