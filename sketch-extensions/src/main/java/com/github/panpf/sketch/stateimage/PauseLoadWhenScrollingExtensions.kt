package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling
import com.github.panpf.sketch.util.SketchException

fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(null))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    stateImage: StateImage
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(stateImage))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    drawable: Drawable
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(DrawableStateImage(drawable)))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    resId: Int
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(DrawableStateImage(resId)))
}

private class PauseLoadWhenScrollingMatcher(val stateImage: StateImage?) :
    ErrorStateImage.Matcher {

    override fun match(request: ImageRequest, exception: SketchException?): Boolean =
        exception?.isCausedByPauseLoadWhenScrolling == true

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable? = stateImage?.getDrawable(sketch, request, throwable)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PauseLoadWhenScrollingMatcher

        if (stateImage != other.stateImage) return false

        return true
    }

    override fun hashCode(): Int {
        return stateImage?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "PauseLoadWhenScrollingMatcher(stateImage=$stateImage)"
    }
}