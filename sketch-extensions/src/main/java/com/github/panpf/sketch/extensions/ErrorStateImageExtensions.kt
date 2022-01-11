package com.github.panpf.sketch.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage.Matcher
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.util.SketchException

fun ErrorStateImage.Builder.pauseLoadWhenScrollErrorImage(): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollMatcherImpl(null))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollErrorImage(
    stateImage: StateImage
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollMatcherImpl(stateImage))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollErrorImage(
    drawable: Drawable
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollMatcherImpl(StateImage.drawable(drawable)))
}

fun ErrorStateImage.Builder.pauseLoadWhenScrollErrorImage(
    resId: Int
): ErrorStateImage.Builder = apply {
    addMatcher(PauseLoadWhenScrollMatcherImpl(StateImage.drawableRes(resId)))
}

private class PauseLoadWhenScrollMatcherImpl(val pauseLoadWhenScrollImage: StateImage?) :
    Matcher {
    override fun match(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Boolean = throwable?.isCausedByPauseLoadWhenScroll == true

    override fun getDrawable(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Drawable? = pauseLoadWhenScrollImage?.getDrawable(context, sketch, request, throwable)
}