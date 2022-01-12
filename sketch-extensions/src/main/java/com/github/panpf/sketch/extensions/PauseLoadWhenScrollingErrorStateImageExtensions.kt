package com.github.panpf.sketch.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.util.SketchException

fun ErrorStateImage.Builder.pauseLoadWhenScrollingErrorImage(): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(null))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingErrorImage(
    stateImage: StateImage
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(stateImage))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingErrorImage(
    drawable: Drawable
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(StateImage.drawable(drawable)))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollingErrorImage(
    resId: Int
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollingMatcher(StateImage.drawableRes(resId)))
}

private class PauseLoadWhenScrollingMatcher(val pauseLoadWhenScrollingImage: StateImage?) :
    ErrorStateImage.Matcher {
    override fun match(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Boolean = throwable?.isCausedByPauseLoadWhenScrolling == true

    override fun getDrawable(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Drawable? = pauseLoadWhenScrollingImage?.getDrawable(context, sketch, request, throwable)
}