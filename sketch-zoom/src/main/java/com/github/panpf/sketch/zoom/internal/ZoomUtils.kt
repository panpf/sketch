package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.util.Size
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

internal fun requiredWorkThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This method must be executed in the work thread"
    }
}

internal fun Context?.getLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}

internal val View.isAttachedToWindowCompat: Boolean
    get() = ViewCompat.isAttachedToWindow(this)

internal fun getPointerIndex(action: Int): Int {
    return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
}

internal fun Float.format(newScale: Int): Float =
    BigDecimal(toDouble()).setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()

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

internal fun reverseRotateRect(rect: Rect, rotateDegrees: Int, drawableSize: Size) {
    require(rotateDegrees % 90 == 0) {
        "rotateDegrees must be an integer multiple of 90"
    }
    when (rotateDegrees) {
        90 -> {
            val bottom = rect.bottom
            rect.bottom = rect.left
            rect.left = rect.top
            rect.top = rect.right
            rect.right = bottom
            rect.top = drawableSize.height - rect.top
            rect.bottom = drawableSize.height - rect.bottom
        }
        180 -> {
            var right = rect.right
            rect.right = rect.left
            rect.left = right
            right = rect.bottom
            rect.bottom = rect.top
            rect.top = right
            rect.top = drawableSize.height - rect.top
            rect.bottom = drawableSize.height - rect.bottom
            rect.left = drawableSize.width - rect.left
            rect.right = drawableSize.width - rect.right
        }
        270 -> {
            val bottom = rect.bottom
            rect.bottom = rect.right
            rect.right = rect.top
            rect.top = rect.left
            rect.left = bottom
            rect.left = drawableSize.width - rect.left
            rect.right = drawableSize.width - rect.right
        }
    }
}

internal fun rotatePoint(point: PointF, rotateDegrees: Int, drawableSize: Size) {
    require(rotateDegrees % 90 == 0) {
        "rotateDegrees must be an integer multiple of 90"
    }
    when (rotateDegrees) {
        90 -> {
            point.x = drawableSize.height - point.y
            point.y = point.x
        }
        180 -> {
            point.x = drawableSize.width - point.x
            point.y = drawableSize.height - point.y
        }
        270 -> {
            point.x = point.y
            point.y = drawableSize.width - point.x
        }
    }
}