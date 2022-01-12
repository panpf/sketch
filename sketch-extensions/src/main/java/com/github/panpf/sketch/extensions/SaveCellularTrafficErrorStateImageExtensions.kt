package com.github.panpf.sketch.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.sketch.util.SketchException


fun ErrorStateImage.Builder.saveCellularTrafficErrorImage(saveCellularTrafficImage: StateImage): ErrorStateImage.Builder =
    apply {
        addMatcher(SaveCellularTrafficMatcher(saveCellularTrafficImage))
    }

fun ErrorStateImage.Builder.saveCellularTrafficErrorImage(saveCellularTrafficDrawable: Drawable): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(StateImage.drawable(saveCellularTrafficDrawable))
        )
    }

fun ErrorStateImage.Builder.saveCellularTrafficErrorImage(saveCellularTrafficImageResId: Int): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(StateImage.drawableRes(saveCellularTrafficImageResId))
        )
    }

private class SaveCellularTrafficMatcher(val saveCellularTrafficImage: StateImage) :
    ErrorStateImage.Matcher {

    override fun match(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Boolean = throwable?.isCausedBySaveCellularTraffic == true

    override fun getDrawable(
        context: Context,
        sketch: Sketch,
        request: DisplayRequest,
        throwable: SketchException?
    ): Drawable? = saveCellularTrafficImage.getDrawable(context, sketch, request, throwable)
}