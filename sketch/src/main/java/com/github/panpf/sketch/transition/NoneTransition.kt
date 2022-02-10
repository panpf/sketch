package com.github.panpf.sketch.transition

import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.internal.ImageResult

/**
 * A transition that applies the [ImageResult] on the [TransitionTarget] without animating.
 */
internal class NoneTransition(
    private val target: TransitionTarget,
    private val result: DisplayResult
) : Transition {

    override fun transition() {
        when (result) {
            is DisplayResult.Success -> target.onSuccess(result.drawable)
            is DisplayResult.Error -> target.onError(result.drawable)
        }
    }

    class Factory : Transition.Factory {

        override fun create(target: TransitionTarget, result: DisplayResult): Transition =
            NoneTransition(target, result)

        override fun equals(other: Any?) = other is Factory

        override fun hashCode() = javaClass.hashCode()
    }
}