package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success

interface ViewAbility {

    var host: Host?

    interface AttachObserver {
        fun onAttachedToWindow()
        fun onDetachedFromWindow()
    }

    interface VisibilityChangedObserver {
        fun onVisibilityChanged(changedView: View, visibility: Int)
    }

    interface LayoutObserver {
        fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    }

    interface DrawObserver {
        fun onDrawBefore(canvas: Canvas)
        fun onDraw(canvas: Canvas)
        fun onDrawForegroundBefore(canvas: Canvas)
        fun onDrawForeground(canvas: Canvas)
    }

    interface DrawableObserver {
        fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?)
    }

    interface ClickObserver {
        val canIntercept: Boolean
        fun onClick(v: View): Boolean
    }

    interface LongClickObserver {
        val canIntercept: Boolean
        fun onLongClick(v: View): Boolean
    }

    interface RequestListenerObserver {
        fun onRequestStart(request: DisplayRequest)
        fun onRequestError(request: DisplayRequest, result: Error)
        fun onRequestSuccess(request: DisplayRequest, result: Success)
    }

    interface RequestProgressListenerObserver {
        fun onUpdateRequestProgress(
            request: DisplayRequest,
            totalLength: Long,
            completedLength: Long
        )
    }
}