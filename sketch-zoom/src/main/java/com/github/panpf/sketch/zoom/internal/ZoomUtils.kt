package com.github.panpf.sketch.zoom.internal

import android.content.res.Resources
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent

internal val Float.dp2px: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

internal val Int.dp2px: Int
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

internal fun getPointerIndex(action: Int): Int {
    return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
}

private val MATRIX_VALUES = FloatArray(9)


internal fun Rect.isCross(rect2: Rect): Boolean {
    return this.left < rect2.right && rect2.left < this.right && this.top < rect2.bottom && rect2.top < this.bottom
}

/**
 * 获取矩阵中指定位置的值
 *
 * @param matrix     [Matrix]
 * @param whichValue 指定的位置，例如 [Matrix.MSCALE_X]
 */
internal fun Matrix.getValue(whichValue: Int): Float {
    // todo 锁的代价比较大，换成限制在主线程即可
    synchronized(MATRIX_VALUES) {
        getValues(MATRIX_VALUES)
        return MATRIX_VALUES[whichValue]
    }
}

/**
 * 从 [Matrix] 中获取缩放比例
 */
internal fun Matrix.getScale(): Float {
    synchronized(MATRIX_VALUES) {
        getValues(MATRIX_VALUES)
        val scaleX: Float =
            MATRIX_VALUES.get(Matrix.MSCALE_X)
        val skewY: Float =
            MATRIX_VALUES.get(Matrix.MSKEW_Y)
        return Math.sqrt(
            (Math.pow(scaleX.toDouble(), 2.0)
                .toFloat() + Math.pow(skewY.toDouble(), 2.0)
                .toFloat()).toDouble()
        ).toFloat()
    }
}

/**
 * 从 [Matrix] 中获取旋转角度
 */
internal fun Matrix.getRotateDegrees(): Int {
    synchronized(MATRIX_VALUES) {
        getValues(MATRIX_VALUES)
        val skewX: Float =
            MATRIX_VALUES.get(Matrix.MSKEW_X)
        val scaleX: Float =
            MATRIX_VALUES.get(Matrix.MSCALE_X)
        val degrees = Math.round(
            Math.atan2(
                skewX.toDouble(),
                scaleX.toDouble()
            ) * (180 / Math.PI)
        )
            .toInt()
        return if (degrees < 0) {
            Math.abs(degrees)
        } else if (degrees > 0) {
            360 - degrees
        } else {
            0
        }
    }
}

/**
 * 从 [Matrix] 中获取偏移位置
 */
internal fun Matrix.getTranslation(point: PointF) {
    synchronized(MATRIX_VALUES) {
        getValues(MATRIX_VALUES)
        point.x =
            MATRIX_VALUES[Matrix.MTRANS_X]
        point.y =
            MATRIX_VALUES[Matrix.MTRANS_Y]
    }
}