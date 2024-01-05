package com.github.panpf.sketch.stateimage.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.stateimage.StateImage

interface CompositeStateImage : StateImage {

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