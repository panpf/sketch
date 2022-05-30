package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

/**
 * Mark as need to observe View
 */
interface ViewObserver


/**
 * Observe View's attach event
 */
interface AttachObserver : ViewObserver {
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
}

/**
 * Observe View's layout event
 */
interface LayoutObserver : ViewObserver {
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
}

/**
 * Observe View's draw event
 */
interface DrawObserver : ViewObserver {
    fun onDrawBefore(canvas: Canvas)
    fun onDraw(canvas: Canvas)
}

/**
 * Observe View's draw foreground event
 */
interface DrawForegroundObserver : ViewObserver {
    fun onDrawForegroundBefore(canvas: Canvas)
    fun onDrawForeground(canvas: Canvas)
}

/**
 * Observe View's size changed event
 */
interface SizeChangeObserver : ViewObserver {
    fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int)
}

/**
 * Observe View's visibility changed event
 */
interface VisibilityChangedObserver : ViewObserver {
    fun onVisibilityChanged(changedView: View, visibility: Int)
}


/**
 * Observe View's touch event
 */
interface TouchEventObserver : ViewObserver {
    fun onTouchEvent(event: MotionEvent): Boolean
}

/**
 * Observe View's click event
 */
interface ClickObserver : ViewObserver {
    val canIntercept: Boolean
    fun onClick(v: View): Boolean
}

/**
 * Observe View's long click event
 */
interface LongClickObserver : ViewObserver {
    val canIntercept: Boolean
    fun onLongClick(v: View): Boolean
}


/**
 * Observe View's drawable changed event
 */
interface DrawableObserver : ViewObserver {
    fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?)
}

/**
 * Observe View's scaleType property
 */
interface ScaleTypeObserver : ViewObserver {
    fun setScaleType(scaleType: ScaleType): Boolean
    fun getScaleType(): ScaleType?
}

/**
 * Observe View's imageMatrix property
 */
interface ImageMatrixObserver : ViewObserver {
    fun setImageMatrix(imageMatrix: Matrix?): Boolean
    fun getImageMatrix(): Matrix?
}


/**
 * Observe request event
 */
interface RequestListenerObserver : ViewObserver {
    fun onRequestStart(request: DisplayRequest)
    fun onRequestError(request: DisplayRequest, result: Error)
    fun onRequestSuccess(request: DisplayRequest, result: Success)
}

/**
 * Observe request progress event
 */
interface RequestProgressListenerObserver : ViewObserver {
    fun onUpdateRequestProgress(
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    )
}