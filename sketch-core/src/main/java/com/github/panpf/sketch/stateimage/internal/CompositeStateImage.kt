package com.github.panpf.sketch.stateimage.internal

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.stateimage.StateImage

interface CompositeStateImage : StateImage {

    val stateList: List<Pair<Condition, StateImage?>>

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Drawable? = stateList
        .find { it.first.accept(request, throwable) }
        ?.second?.getDrawable(sketch, request, throwable)

    interface Condition {

        fun accept(
            request: ImageRequest,
            throwable: Throwable?
        ): Boolean
    }
}