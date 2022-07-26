package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.util.SketchException


/**
 * Set the error image when the save cellular traffic
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(): ErrorStateImage.Builder =
    apply {
        addMatcher(SaveCellularTrafficMatcher(null))
    }

/**
 * Set the error image when the save cellular traffic
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficImage: StateImage): ErrorStateImage.Builder =
    apply {
        addMatcher(SaveCellularTrafficMatcher(saveCellularTrafficImage))
    }

/**
 * Set the error image when the save cellular traffic
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficDrawable: Drawable): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(DrawableStateImage(saveCellularTrafficDrawable))
        )
    }

/**
 * Set the error image when the save cellular traffic
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(saveCellularTrafficImageResId: Int): ErrorStateImage.Builder =
    apply {
        addMatcher(
            SaveCellularTrafficMatcher(DrawableStateImage(saveCellularTrafficImageResId))
        )
    }

class SaveCellularTrafficMatcher(val stateImage: StateImage?) :
    ErrorStateImage.Matcher {

    override fun match(request: ImageRequest, exception: SketchException?): Boolean =
        isCausedBySaveCellularTraffic(request, exception)

    override fun getDrawable(
        sketch: Sketch, request: ImageRequest, throwable: SketchException?
    ): Drawable? = stateImage?.getDrawable(sketch, request, throwable)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaveCellularTrafficMatcher) return false
        if (stateImage != other.stateImage) return false
        return true
    }

    override fun hashCode(): Int {
        return stateImage.hashCode()
    }

    override fun toString(): String {
        return "SaveCellularTrafficMatcher($stateImage)"
    }
}