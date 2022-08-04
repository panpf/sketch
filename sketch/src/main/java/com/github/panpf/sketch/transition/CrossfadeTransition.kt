package com.github.panpf.sketch.transition

import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.internal.getCrossfadeEndDrawable
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.util.asOrNull

/**
 * A [Transition] that crossfades from the current drawable to a new one.
 *
 * @param durationMillis The duration of the animation in milliseconds.
 * @param preferExactIntrinsicSize See [CrossfadeDrawable.preferExactIntrinsicSize].
 */
class CrossfadeTransition @JvmOverloads constructor(
    private val target: TransitionDisplayTarget,
    private val result: DisplayResult,
    val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
    val preferExactIntrinsicSize: Boolean = false,
    val fitScale: Boolean = true,
) : Transition {

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun transition() {
        val startDrawable = target.drawable?.getCrossfadeEndDrawable()
        val endDrawable = result.drawable?.getCrossfadeEndDrawable()
        if (startDrawable === endDrawable) {
            return
        }

        val drawable = CrossfadeDrawable(
            start = startDrawable,
            end = endDrawable,
            fitScale = fitScale,
            durationMillis = durationMillis,
            fadeStart = true,
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is DisplayResult.Success -> target.onSuccess(drawable)
            is DisplayResult.Error -> target.onError(drawable)
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
        val preferExactIntrinsicSize: Boolean = false,
        val alwaysUse: Boolean = false,
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            target: TransitionDisplayTarget,
            result: DisplayResult,
            fitScale: Boolean
        ): Transition? {
            val fromMemoryCache = result.asOrNull<DisplayResult.Success>()?.dataFrom == MEMORY_CACHE
            return if (alwaysUse || !fromMemoryCache) {
                CrossfadeTransition(
                    target = target,
                    result = result,
                    durationMillis = durationMillis,
                    preferExactIntrinsicSize = preferExactIntrinsicSize,
                    fitScale = fitScale
                )
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
                    && durationMillis == other.durationMillis
                    && preferExactIntrinsicSize == other.preferExactIntrinsicSize
                    && alwaysUse == other.alwaysUse
        }

        override fun hashCode(): Int {
            var result = durationMillis
            result = 31 * result + preferExactIntrinsicSize.hashCode()
            result = 31 * result + alwaysUse.hashCode()
            return result
        }

        override fun toString(): String {
            return "CrossfadeTransition.Factory(durationMillis=$durationMillis, preferExactIntrinsicSize=$preferExactIntrinsicSize, alwaysUse=$alwaysUse)"
        }
    }
}
