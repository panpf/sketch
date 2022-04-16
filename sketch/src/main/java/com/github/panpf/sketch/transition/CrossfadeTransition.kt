package com.github.panpf.sketch.transition

import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.DisplayResult

/**
 * A [Transition] that crossfades from the current drawable to a new one.
 *
 * @param durationMillis The duration of the animation in milliseconds.
 * @param preferExactIntrinsicSize See [CrossfadeDrawable.preferExactIntrinsicSize].
 */
class CrossfadeTransition @JvmOverloads constructor(
    private val target: TransitionTarget,
    private val result: DisplayResult,
    val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
    val preferExactIntrinsicSize: Boolean = false
) : Transition {

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun transition() {
        val drawable = CrossfadeDrawable(
            start = target.drawable,
            end = result.drawable,
            fitScale = (target.view as? ImageView)?.fitScale ?: true,
            durationMillis = durationMillis,
            fadeStart = target.drawable !is SketchCountBitmapDrawable,    // If the start drawable is a placeholder drawn from the memory cache, the fade in effect is not used
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is DisplayResult.Success -> target.onSuccess(drawable)
            is DisplayResult.Error -> target.onError(drawable)
        }
    }

    private val ImageView.fitScale: Boolean
        get() = when (scaleType) {
            ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER_INSIDE -> true
            else -> false
        }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
        val preferExactIntrinsicSize: Boolean = false
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(target: TransitionTarget, result: DisplayResult): Transition? {
            // Only animate successful requests.
            if (result !is DisplayResult.Success) {
                return null
            }

            // Don't animate if the request was fulfilled by the memory cache.
            if (result.dataFrom == MEMORY_CACHE) {
                return null
            }

            return CrossfadeTransition(target, result, durationMillis, preferExactIntrinsicSize)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory &&
                    durationMillis == other.durationMillis &&
                    preferExactIntrinsicSize == other.preferExactIntrinsicSize
        }

        override fun hashCode(): Int {
            var result = durationMillis
            result = 31 * result + preferExactIntrinsicSize.hashCode()
            return result
        }
    }
}
