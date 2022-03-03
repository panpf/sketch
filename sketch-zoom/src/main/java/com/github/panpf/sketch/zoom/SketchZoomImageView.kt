/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.Interpolator
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle) {

    private val zoomViewAbility = ZoomViewAbility()

    init {
        addViewAbility(zoomViewAbility)
    }

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueueDisplay(request)
    }

    var scrollBarEnabled: Boolean
        get() = zoomViewAbility.scrollBarEnabled
        set(value) {
            zoomViewAbility.scrollBarEnabled = value
        }

    var readModeEnabled: Boolean
        get() = zoomViewAbility.readModeEnabled
        set(value) {
            zoomViewAbility.readModeEnabled = value
        }
    var readModeDecider: ReadModeDecider
        get() = zoomViewAbility.readModeDecider
        set(value) {
            zoomViewAbility.readModeDecider = value
        }

    var zoomScales: ZoomScales
        get() = zoomViewAbility.zoomScales
        set(value) {
            zoomViewAbility.zoomScales = value
        }
    var zoomAnimationDuration: Int
        get() = zoomViewAbility.zoomAnimationDuration
        set(value) {
            zoomViewAbility.zoomAnimationDuration = value
        }
    var zoomInterpolator: Interpolator
        get() = zoomViewAbility.zoomInterpolator
        set(value) {
            zoomViewAbility.zoomInterpolator = value
        }

    var allowParentInterceptOnEdge: Boolean
        get() = zoomViewAbility.allowParentInterceptOnEdge
        set(value) {
            zoomViewAbility.allowParentInterceptOnEdge = value
        }

    var onViewLongPressListener: OnViewLongPressListener?
        get() = zoomViewAbility.onViewLongPressListener
        set(value) {
            zoomViewAbility.onViewLongPressListener = value
        }

    var onViewTapListener: OnViewTapListener?
        get() = zoomViewAbility.onViewTapListener
        set(value) {
            zoomViewAbility.onViewTapListener = value
        }

    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        zoomViewAbility.addOnMatrixChangeListener(listener)
    }

    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        return zoomViewAbility.removeOnMatrixChangeListener(listener)
    }

    fun addOnRotateChangeListener(listener: OnRotateChangeListener) {
        zoomViewAbility.addOnRotateChangeListener(listener)
    }

    fun removeOnRotateChangeListener(listener: OnRotateChangeListener): Boolean {
        return zoomViewAbility.removeOnRotateChangeListener(listener)
    }

    fun addOnDragFlingListener(listener: OnDragFlingListener) {
        zoomViewAbility.addOnDragFlingListener(listener)
    }

    fun removeOnDragFlingListener(listener: OnDragFlingListener): Boolean {
        return zoomViewAbility.removeOnDragFlingListener(listener)
    }

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        zoomViewAbility.addOnScaleChangeListener(listener)
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        return zoomViewAbility.removeOnScaleChangeListener(listener)
    }

    val rotateDegrees: Int
        get() = zoomViewAbility.rotateDegrees

    override fun canScrollHorizontally(direction: Int): Boolean =
        zoomViewAbility.canScrollHorizontally(direction)

    override fun canScrollVertically(direction: Int): Boolean =
        zoomViewAbility.canScrollVertically(direction)

    val horScrollEdge: Int
        get() = zoomViewAbility.horScrollEdge

    val verScrollEdge: Int
        get() = zoomViewAbility.verScrollEdge

    val zoomScale: Float
        get() = zoomViewAbility.zoomScale

    val baseZoomScale: Float
        get() = zoomViewAbility.baseZoomScale

    val supportZoomScale: Float
        get() = zoomViewAbility.supportZoomScale

    /** Zoom ratio that makes the image fully visible */
    val fullZoomScale: Float
        get() = zoomViewAbility.fullZoomScale

    /** Gets the zoom that fills the image with the ImageView display */
    val fillZoomScale: Float
        get() = zoomViewAbility.fillZoomScale

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originZoomScale: Float
        get() = zoomViewAbility.originZoomScale

    val minZoomScale: Float
        get() = zoomViewAbility.minZoomScale

    val maxZoomScale: Float
        get() = zoomViewAbility.maxZoomScale

    val doubleClickZoomScales: FloatArray?
        get() = zoomViewAbility.doubleClickZoomScales

    val isZooming: Boolean
        get() = zoomViewAbility.isZooming

    fun getDrawMatrix(matrix: Matrix) = zoomViewAbility.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = zoomViewAbility.getDrawRect(rectF)

    /** Gets the area that the user can see on the preview (not affected by rotation) */
    fun getVisibleRect(rect: Rect) = zoomViewAbility.getVisibleRect(rect)
}