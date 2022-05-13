package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget

class TestTransition : Transition {

    override fun transition() {

    }

    class Factory : Transition.Factory {
        override fun create(target: TransitionTarget, result: DisplayResult): Transition? {
            return TestTransition()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}