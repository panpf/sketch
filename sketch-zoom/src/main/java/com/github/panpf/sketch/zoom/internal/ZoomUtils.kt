package com.github.panpf.sketch.zoom.internal

import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent
import com.github.panpf.sketch.util.requiredMainThread
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun getPointerIndex(action: Int): Int {
    return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
}

private val MATRIX_VALUES = FloatArray(9)

/**
 * @param whichValue Example: [Matrix.MSCALE_X]
 */
internal fun Matrix.getValue(whichValue: Int): Float {
    requiredMainThread()
    getValues(MATRIX_VALUES)
    return MATRIX_VALUES[whichValue]
}

internal fun Matrix.getScale(): Float {
    requiredMainThread()
    getValues(MATRIX_VALUES)
    val scaleX: Float = MATRIX_VALUES[Matrix.MSCALE_X]
    val skewY: Float = MATRIX_VALUES[Matrix.MSKEW_Y]
    return sqrt(scaleX.toDouble().pow(2.0) + skewY.toDouble().pow(2.0)).toFloat()
}

internal fun Matrix.getRotateDegrees(): Int {
    requiredMainThread()
    getValues(MATRIX_VALUES)
    val skewX: Float = MATRIX_VALUES[Matrix.MSKEW_X]
    val scaleX: Float = MATRIX_VALUES[Matrix.MSCALE_X]
    val degrees = (atan2(skewX.toDouble(), scaleX.toDouble()) * (180 / Math.PI)).roundToInt()
    return when {
        degrees < 0 -> abs(degrees)
        degrees > 0 -> 360 - degrees
        else -> 0
    }
}

internal fun Matrix.getTranslation(point: PointF) {
    requiredMainThread()
    getValues(MATRIX_VALUES)
    point.x = MATRIX_VALUES[Matrix.MTRANS_X]
    point.y = MATRIX_VALUES[Matrix.MTRANS_Y]
}