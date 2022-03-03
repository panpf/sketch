package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

interface ViewObserver


interface AttachObserver : ViewObserver {
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
}

interface LayoutObserver : ViewObserver {
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
}

interface DrawObserver : ViewObserver {
    fun onDrawBefore(canvas: Canvas)
    fun onDraw(canvas: Canvas)
}

interface DrawForegroundObserver : ViewObserver {
    fun onDrawForegroundBefore(canvas: Canvas)
    fun onDrawForeground(canvas: Canvas)
}

interface SizeChangeObserver : ViewObserver {
    fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int)
}

interface VisibilityChangedObserver : ViewObserver {
    fun onVisibilityChanged(changedView: View, visibility: Int)
}


interface TouchEventObserver : ViewObserver {
    fun onTouchEvent(event: MotionEvent): Boolean
}

interface ClickObserver : ViewObserver {
    val canIntercept: Boolean
    fun onClick(v: View): Boolean
}

interface LongClickObserver : ViewObserver {
    val canIntercept: Boolean
    fun onLongClick(v: View): Boolean
}


interface DrawableObserver : ViewObserver {
    fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?)
}

interface ScaleTypeObserver : ViewObserver {
    fun setScaleType(scaleType: ScaleType): Boolean
    fun getScaleType(): ScaleType?
}


interface RequestListenerObserver : ViewObserver {
    fun onRequestStart(request: DisplayRequest)
    fun onRequestError(request: DisplayRequest, result: Error)
    fun onRequestSuccess(request: DisplayRequest, result: Success)
}

interface RequestProgressListenerObserver : ViewObserver {
    fun onUpdateRequestProgress(
        request: DisplayRequest,
        totalLength: Long,
        completedLength: Long
    )
}