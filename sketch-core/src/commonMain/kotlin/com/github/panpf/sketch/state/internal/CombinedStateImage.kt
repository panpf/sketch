package com.github.panpf.sketch.state.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.StateImage

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface CombinedStateImage : StateImage {

    val stateList: List<Pair<Condition, StateImage?>>

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? = stateList
        .find { it.first.accept(request, throwable) }
        ?.second?.getImage(sketch, request, throwable)

    interface Condition {

        fun accept(
            request: ImageRequest,
            throwable: Throwable?
        ): Boolean
    }
}