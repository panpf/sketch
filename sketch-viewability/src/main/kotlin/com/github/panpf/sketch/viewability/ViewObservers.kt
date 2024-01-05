/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.Progress

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
    fun onRequestStart(request: ImageRequest)
    fun onRequestError(request: ImageRequest, error: ImageResult.Error)
    fun onRequestSuccess(request: ImageRequest, result: ImageResult.Success)
}

/**
 * Observe request progress event
 */
interface RequestProgressListenerObserver : ViewObserver {
    fun onUpdateRequestProgress(request: ImageRequest, progress: Progress)
}

/**
 * Observe save and restore instance state
 */
interface InstanceStateObserver : ViewObserver {

    fun onSaveInstanceState(): Bundle?

    fun onRestoreInstanceState(state: Bundle?)
}