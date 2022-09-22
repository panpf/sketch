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
package com.github.panpf.sketch.zoom

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.Interpolator
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    private val zoomAbility = ZoomAbility()

    override var displayImageOptions: ImageOptions? = null

    var scrollBarEnabled: Boolean
        get() = zoomAbility.scrollBarEnabled
        set(value) {
            zoomAbility.scrollBarEnabled = value
        }
    var readModeEnabled: Boolean
        get() = zoomAbility.readModeEnabled
        set(value) {
            zoomAbility.readModeEnabled = value
        }
    var readModeDecider: ReadModeDecider?
        get() = zoomAbility.readModeDecider
        set(value) {
            zoomAbility.readModeDecider = value
        }
    var scaleStateFactory: ScaleState.Factory?
        get() = zoomAbility.scaleStateFactory
        set(value) {
            zoomAbility.scaleStateFactory = value
        }
    var showTileBounds: Boolean
        get() = zoomAbility.showTileBounds
        set(value) {
            zoomAbility.showTileBounds = value
        }
    var zoomAnimationDuration: Int
        get() = zoomAbility.zoomAnimationDuration
        set(value) {
            zoomAbility.zoomAnimationDuration = value
        }
    var zoomInterpolator: Interpolator?
        get() = zoomAbility.zoomInterpolator
        set(value) {
            zoomAbility.zoomInterpolator = value
        }
    var allowParentInterceptOnEdge: Boolean
        get() = zoomAbility.allowParentInterceptOnEdge
        set(value) {
            zoomAbility.allowParentInterceptOnEdge = value
        }
    var onViewLongPressListener: OnViewLongPressListener?
        get() = zoomAbility.onViewLongPressListener
        set(value) {
            zoomAbility.onViewLongPressListener = value
        }
    var onViewTapListener: OnViewTapListener?
        get() = zoomAbility.onViewTapListener
        set(value) {
            zoomAbility.onViewTapListener = value
        }

    init {
        addViewAbility(zoomAbility)
    }


    /*************************************** Interaction ******************************************/

    /**
     * Locate to the location specified on the preview image. You don't have to worry about scaling and rotation
     *
     * @param x Preview the x coordinate on the diagram
     * @param y Preview the y-coordinate on the diagram
     */
    fun location(x: Float, y: Float, animate: Boolean = false) {
        zoomAbility.location(x, y, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     *
     * @param focalX  Scale the x coordinate of the center point on the preview image
     * @param focalY  Scale the y coordinate of the center point on the preview image
     */
    fun scale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        zoomAbility.scale(scale, focalX, focalY, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     */
    fun scale(scale: Float, animate: Boolean = false) {
        zoomAbility.scale(scale, animate)
    }

    /**
     * Rotate the image to the specified degrees
     *
     * @param degrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateTo(degrees: Int) {
        zoomAbility.rotateTo(degrees)
    }

    /**
     * Rotate an degrees based on the current rotation degrees
     *
     * @param addDegrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateBy(addDegrees: Int) {
        zoomAbility.rotateBy(addDegrees)
    }


    /***************************************** Information ****************************************/

    val rotateDegrees: Int
        get() = zoomAbility.rotateDegrees

    val horScrollEdge: Edge
        get() = zoomAbility.horScrollEdge

    val verScrollEdge: Edge
        get() = zoomAbility.verScrollEdge

    val scale: Float
        get() = zoomAbility.scale

    val baseScale: Float
        get() = zoomAbility.baseScale

    val supportScale: Float
        get() = zoomAbility.supportScale

    /** Zoom ratio that makes the image fully visible */
    val fullScale: Float
        get() = zoomAbility.fullScale

    /** Gets the zoom that fills the image with the ImageView display */
    val fillScale: Float
        get() = zoomAbility.fillScale

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originScale: Float
        get() = zoomAbility.originScale

    val minScale: Float
        get() = zoomAbility.minScale

    val maxScale: Float
        get() = zoomAbility.maxScale

    val stepScales: FloatArray?
        get() = zoomAbility.stepScales

    val isScaling: Boolean
        get() = zoomAbility.isScaling

    val tileList: List<Tile>?
        get() = zoomAbility.tileList

    val imageSize: Size?
        get() = zoomAbility.imageSize ?: zoomAbility.imageSize

    val previewSize: Size?
        get() = zoomAbility.previewSize

    fun getDrawMatrix(matrix: Matrix) = zoomAbility.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = zoomAbility.getDrawRect(rectF)

    /**
     * Gets the area that the user can see on the preview (not affected by rotation)
     */
    fun getVisibleRect(rect: Rect) = zoomAbility.getVisibleRect(rect)

    fun touchPointToDrawablePoint(touchPoint: PointF): Point? {
        return zoomAbility.touchPointToDrawablePoint(touchPoint)
    }

    fun eachTileList(action: (tile: Tile, load: Boolean) -> Unit) {
        zoomAbility.eachTileList(action)
    }

    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        zoomAbility.addOnMatrixChangeListener(listener)
    }

    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        return zoomAbility.removeOnMatrixChangeListener(listener)
    }

    fun addOnRotateChangeListener(listener: OnRotateChangeListener) {
        zoomAbility.addOnRotateChangeListener(listener)
    }

    fun removeOnRotateChangeListener(listener: OnRotateChangeListener): Boolean {
        return zoomAbility.removeOnRotateChangeListener(listener)
    }

    fun addOnDragFlingListener(listener: OnDragFlingListener) {
        zoomAbility.addOnDragFlingListener(listener)
    }

    fun removeOnDragFlingListener(listener: OnDragFlingListener): Boolean {
        return zoomAbility.removeOnDragFlingListener(listener)
    }

    fun addOnViewDragListener(listener: OnViewDragListener) {
        zoomAbility.addOnViewDragListener(listener)
    }

    fun removeOnViewDragListener(listener: OnViewDragListener): Boolean {
        return zoomAbility.removeOnViewDragListener(listener)
    }

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        zoomAbility.addOnScaleChangeListener(listener)
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        return zoomAbility.removeOnScaleChangeListener(listener)
    }

    fun addOnTileChangedListener(listener: OnTileChangedListener) {
        zoomAbility.addOnTileChangedListener(listener)
    }

    fun removeOnTileChangedListener(listener: OnTileChangedListener): Boolean {
        return zoomAbility.removeOnTileChangedListener(listener)
    }


    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueue(request)
    }

    override fun canScrollHorizontally(direction: Int): Boolean =
        zoomAbility.canScrollHorizontally(direction)

    override fun canScrollVertically(direction: Int): Boolean =
        zoomAbility.canScrollVertically(direction)
}