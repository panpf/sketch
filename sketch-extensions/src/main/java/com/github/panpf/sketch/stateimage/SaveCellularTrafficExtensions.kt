package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.util.SketchException


fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficImage: StateImage): ErrorStateImage.Builder =
    apply {
        addMatcher(SaveCellularTrafficMatcher(saveCellularTrafficImage))
    }

fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficDrawable: Drawable): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(DrawableStateImage(saveCellularTrafficDrawable))
        )
    }

fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficImageResId: Int): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(DrawableStateImage(saveCellularTrafficImageResId))
        )
    }

private class SaveCellularTrafficMatcher(val saveCellularTrafficImage: StateImage) :
    ErrorStateImage.Matcher {

    override fun match(request: ImageRequest, exception: SketchException?): Boolean =
        exception?.isCausedBySaveCellularTraffic == true

    override fun getDrawable(request: ImageRequest, throwable: SketchException?): Drawable? =
        saveCellularTrafficImage.getDrawable(request, throwable)
}