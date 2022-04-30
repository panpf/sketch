package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
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

private class PauseLoadWhenScrollingMatcher(val pauseLoadWhenScrollingImage: StateImage?) :
    ErrorStateImage.Matcher {

    override fun match(request: ImageRequest, exception: SketchException?): Boolean =
        exception?.isCausedByPauseLoadWhenScrolling == true

    override fun getDrawable(request: ImageRequest, throwable: SketchException?): Drawable? =
        pauseLoadWhenScrollingImage?.getDrawable(request, throwable)
}