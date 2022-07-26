package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling
import com.github.panpf.sketch.util.SketchException

/**
 * Set the error image when the loading is paused in the scrolling of the list
 */
fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(null))
}

/**
 * Set the error image when the loading is paused in the scrolling of the list
 */
fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    stateImage: StateImage
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(stateImage))
}

/**
 * Set the error image when the loading is paused in the scrolling of the list
 */
fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    drawable: Drawable
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(DrawableStateImage(drawable)))
}

/**
 * Set the error image when the loading is paused in the scrolling of the list
 */
fun ErrorStateImage.Builder.pauseLoadWhenScrollingError(
    resId: Int
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(DrawableStateImage(resId)))
}

class PauseLoadWhenScrollingMatcher(val stateImage: StateImage?) :
    ErrorStateImage.Matcher {

    override fun match(request: ImageRequest, exception: SketchException?): Boolean =
        isCausedByPauseLoadWhenScrolling(request, exception)

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable? = stateImage?.getDrawable(sketch, request, throwable)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PauseLoadWhenScrollingMatcher) return false
        if (stateImage != other.stateImage) return false
        return true
    }

    override fun hashCode(): Int {
        return stateImage?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "PauseLoadWhenScrollingMatcher($stateImage)"
    }
}