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
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

/**
 * Store and manage ViewAbility
 */
interface ViewAbilityManager {

    /**
     * ViewAbility List
     */
    val viewAbilityList: List<ViewAbility>

    /**
     * Add a ViewAbility and run it
     */
    fun addViewAbility(viewAbility: ViewAbility): ViewAbilityManager

    /**
     * Delete a ViewAbility
     */
    fun removeViewAbility(viewAbility: ViewAbility): ViewAbilityManager

    /**
     * Handling the AttachedToWindow event
     */
    fun onAttachedToWindow()

    /**
     * Handling the DetachedToWindow event
     */
    fun onDetachedFromWindow()

    /**
     * Handling the Layout event
     */
    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    /**
     * Handling the SizeChanged event
     */
    fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int)

    /**
     * Handling the VisibilityChanged event
     */
    fun onVisibilityChanged(changedView: View, visibility: Int)

    /**
     * Handling the DrawBefore event
     */
    fun onDrawBefore(canvas: Canvas)

    /**
     * Handling the Draw event
     */
    fun onDraw(canvas: Canvas)

    /**
     * Handling the DrawForegroundBefore event
     */
    fun onDrawForegroundBefore(canvas: Canvas)

    /**
     * Handling the DrawForeground event
     */
    fun onDrawForeground(canvas: Canvas)

    /**
     * Handling the DrawableChanged event
     */
    fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?)

    /**
     * Handling the TouchEvent event
     */
    fun onTouchEvent(event: MotionEvent): Boolean

    /**
     * Proxy setOnClickListener()
     */
    fun setOnClickListener(l: OnClickListener?)

    /**
     * Proxy setOnLongClickListener()
     */
    fun setOnLongClickListener(l: OnLongClickListener?)

    /**
     * Proxy setScaleType()
     */
    fun setScaleType(scaleType: ScaleType): Boolean

    /**
     * Proxy getScaleType()
     */
    fun getScaleType(): ScaleType?

    /**
     * Proxy setImageMatrix()
     */
    fun setImageMatrix(imageMatrix: Matrix?): Boolean

    /**
     * Proxy getImageMatrix()
     */
    fun getImageMatrix(): Matrix?

    /**
     * Get request Listener
     */
    fun getRequestListener(): Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error>?

    /**
     * Get request progress Listener
     */
    fun getRequestProgressListener(): ProgressListener<DisplayRequest>?

    fun onSaveInstanceState(): Bundle?

    fun onRestoreInstanceState(state: Bundle?)
}