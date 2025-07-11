package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transition.Transition

class FakeTransition : Transition {

    override fun transition() {

    }

    class Factory : Transition.Factory {

        override val key: String = "FakeTransition.Factory"

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: Target,
            result: ImageResult
        ): Transition = FakeTransition()

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