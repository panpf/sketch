package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget

class FakeTransition : Transition {

    override fun transition() {

    }

    class Factory : Transition.Factory {
        override fun create(
            requestContext: RequestContext,
            target: TransitionTarget,
            result: ImageResult
        ): Transition = FakeTransition()

        override val key: String = "FakeTransition.Factory"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            return true
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "FakeTransition"
        }
    }
}