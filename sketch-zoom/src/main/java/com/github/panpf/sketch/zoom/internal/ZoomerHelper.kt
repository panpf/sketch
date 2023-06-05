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
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.DefaultScaleStateFactory
import com.github.panpf.sketch.zoom.Edge
import com.github.panpf.sketch.zoom.LongImageReadModeDecider
import com.github.panpf.sketch.zoom.OnDragFlingListener
import com.github.panpf.sketch.zoom.OnMatrixChangeListener
import com.github.panpf.sketch.zoom.OnRotateChangeListener
import com.github.panpf.sketch.zoom.OnScaleChangeListener
import com.github.panpf.sketch.zoom.OnViewDragListener
import com.github.panpf.sketch.zoom.OnViewLongPressListener
import com.github.panpf.sketch.zoom.OnViewTapListener
import com.github.panpf.sketch.zoom.ReadModeDecider
import com.github.panpf.sketch.zoom.ScaleState
import com.github.panpf.sketch.zoom.ScaleState.Factory

/**
 * Based https://github.com/Baseflow/PhotoView git 565505d5 20210120
 */
internal class ZoomerHelper constructor(
    private val context: Context,
    private val logger: Logger,
    val view: View,
    scaleType: ScaleType,
) {

    companion object {
        const val MODULE = "ZoomerHelper"
    }

    private val tapHelper = TapHelper(context, this)
    private val scaleDragHelper = ScaleDragHelper(
        context = context,
        logger = logger,
        zoomerHelper = this,
        onUpdateMatrix = {
            scrollBarHelper?.onMatrixChanged()
            onMatrixChangeListenerList?.forEach { listener ->
                listener.onMatrixChanged()
            }
        },
        onViewDrag = { dx: Float, dy: Float ->
            onViewDragListenerList?.forEach {
                it.onDrag(dx, dy)
            }
        },
        onDragFling = { startX: Float, startY: Float, velocityX: Float, velocityY: Float ->
            onDragFlingListenerList?.forEach {
                it.onFling(startX, startY, velocityX, velocityY)
            }
        },
        onScaleChanged = { scaleFactor: Float, focusX: Float, focusY: Float ->
            onScaleChangeListenerList?.forEach {
                it.onScaleChanged(scaleFactor, focusX, focusY)
            }
        }
    )
    private var scrollBarHelper: ScrollBarHelper? = ScrollBarHelper(context, this)
    private var _rotateDegrees = 0

    private var onMatrixChangeListenerList: MutableSet<OnMatrixChangeListener>? = null
    private var onRotateChangeListenerList: MutableSet<OnRotateChangeListener>? = null
    private var onDragFlingListenerList: MutableSet<OnDragFlingListener>? = null
    private var onViewDragListenerList: MutableSet<OnViewDragListener>? = null
    private var onScaleChangeListenerList: MutableSet<OnScaleChangeListener>? = null

    /** Allows the parent ViewGroup to intercept events while sliding to an edge */
    var allowParentInterceptOnEdge: Boolean = true
    var scaleAnimationInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    var onViewLongPressListener: OnViewLongPressListener? = null
    var onViewTapListener: OnViewTapListener? = null
    var viewSize = Size.Empty
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var imageSize = Size.Empty
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var drawableSize = Size.Empty
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var scaleType: ScaleType = scaleType
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var readModeEnabled: Boolean = false
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var readModeDecider: ReadModeDecider? = null
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    private val finalReadModeDecider: ReadModeDecider?
        get() = if (readModeEnabled) readModeDecider ?: LongImageReadModeDecider() else null
    var scaleStateFactory: Factory = DefaultScaleStateFactory()
        internal set(value) {
            if (field != value) {
                field = value
                reset()
            }
        }
    var scrollBarEnabled: Boolean
        get() = scrollBarHelper != null
        internal set(value) {
            val enabled = scrollBarHelper != null
            if (enabled != value) {
                scrollBarHelper = if (value) {
                    ScrollBarHelper(context, this).apply { reset() }
                } else {
                    null
                }
            }
        }
    var scaleAnimationDuration: Int = 200
        internal set(value) {
            if (value > 0 && field != value) {
                field = value
            }
        }
    var scaleState: ScaleState = ScaleState.EMPTY
        private set


    /**************************************** Internal ********************************************/

    init {
        reset()
    }

    private fun reset() {
        scaleState = scaleStateFactory.create(
            viewSize = viewSize,
            imageSize = imageSize,
            drawableSize = drawableSize,
            rotateDegrees = _rotateDegrees,
            scaleType = scaleType,
            readModeDecider = finalReadModeDecider
        )
        scaleDragHelper.reset()
        logger.d(MODULE) {
            "reset. viewSize=$viewSize, " +
                    "imageSize=$imageSize, " +
                    "drawableSize=$drawableSize, " +
                    "rotateDegrees=$rotateDegrees, " +
                    "scaleType=$scaleType, " +
                    "finalReadModeDecider=$finalReadModeDecider, " +
                    "scaleState=$scaleState"
        }
    }

    internal fun clean() {
        scaleDragHelper.clean()
    }

    internal fun onDraw(canvas: Canvas) {
        scrollBarHelper?.onDraw(canvas)
    }

    internal fun onTouchEvent(event: MotionEvent): Boolean {
        if (drawableSize.isEmpty) return false
        val scaleAndDragConsumed = scaleDragHelper.onTouchEvent(event)
        val tapConsumed = tapHelper.onTouchEvent(event)
        return scaleAndDragConsumed || tapConsumed
    }


    /*************************************** Interaction ******************************************/

    /**
     * Locate to the location specified on the drawable image. You don't have to worry about scaling and rotation
     *
     * @param x Drawable the x coordinate on the diagram
     * @param y Drawable the y-coordinate on the diagram
     */
    fun location(x: Float, y: Float, animate: Boolean = false) {
        scaleDragHelper.location(x, y, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     *
     * @param focalX  Scale the x coordinate of the center point on the view
     * @param focalY  Scale the y coordinate of the center point on the view
     */
    fun scale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        val finalScale = scale
            .coerceAtLeast(scaleState.min)
            .coerceAtMost(scaleState.max)
        scaleDragHelper.scale(finalScale, focalX, focalY, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     */
    fun scale(scale: Float, animate: Boolean = false) {
        scale(scale, (view.right / 2).toFloat(), (view.bottom / 2).toFloat(), animate)
    }

    /**
     * Rotate the image to the specified degrees
     *
     * @param degrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateTo(degrees: Int) {
        require(degrees % 90 == 0) { "degrees must be in multiples of 90: $degrees" }
        if (_rotateDegrees == degrees) return

        var newDegrees = degrees % 360
        if (newDegrees <= 0) {
            newDegrees = 360 - newDegrees
        }
        _rotateDegrees = newDegrees
        reset()
        onRotateChangeListenerList?.forEach {
            it.onRotateChanged(newDegrees)
        }
    }

    /**
     * Rotate an degrees based on the current rotation degrees
     *
     * @param addDegrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateBy(addDegrees: Int) {
        return rotateTo(_rotateDegrees + addDegrees)
    }


    /***************************************** Information ****************************************/

    val rotateDegrees: Int
        get() = _rotateDegrees

    fun canScrollHorizontally(direction: Int): Boolean =
        scaleDragHelper.canScrollHorizontally(direction)

    fun canScrollVertically(direction: Int): Boolean =
        scaleDragHelper.canScrollVertically(direction)

    val horScrollEdge: Edge
        get() = scaleDragHelper.horScrollEdge

    val verScrollEdge: Edge
        get() = scaleDragHelper.verScrollEdge

    val scale: Float
        get() = scaleDragHelper.scale

    val baseScale: Float
        get() = scaleDragHelper.baseScale

    val supportScale: Float
        get() = scaleDragHelper.supportScale

    /** Zoom ratio that makes the image fully visible */
    val fullScale: Float
        get() = scaleState.full

    /** Gets the zoom that fills the image with the ImageView display */
    val fillScale: Float
        get() = scaleState.fill

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originScale: Float
        get() = scaleState.origin

    val minScale: Float
        get() = scaleState.min

    val maxScale: Float
        get() = scaleState.max

    val stepScales: FloatArray
        get() = scaleState.doubleClickSteps

    val isScaling: Boolean
        get() = scaleDragHelper.isScaling

    fun getDrawMatrix(matrix: Matrix) = scaleDragHelper.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = scaleDragHelper.getDrawRect(rectF)

    /** Gets the area that the user can see on the drawable (not affected by rotation) */
    fun getVisibleRect(rect: Rect) = scaleDragHelper.getVisibleRect(rect)

    fun touchPointToDrawablePoint(touchPoint: PointF): Point? {
        return scaleDragHelper.touchPointToDrawablePoint(touchPoint)
    }

    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        this.onMatrixChangeListenerList = (onMatrixChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        return onMatrixChangeListenerList?.remove(listener) == true
    }

    fun addOnRotateChangeListener(listener: OnRotateChangeListener) {
        this.onRotateChangeListenerList = (onRotateChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnRotateChangeListener(listener: OnRotateChangeListener): Boolean {
        return onRotateChangeListenerList?.remove(listener) == true
    }

    fun addOnDragFlingListener(listener: OnDragFlingListener) {
        this.onDragFlingListenerList = (onDragFlingListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnDragFlingListener(listener: OnDragFlingListener): Boolean {
        return onDragFlingListenerList?.remove(listener) == true
    }

    fun addOnViewDragListener(listener: OnViewDragListener) {
        this.onViewDragListenerList = (onViewDragListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnViewDragListener(listener: OnViewDragListener): Boolean {
        return onViewDragListenerList?.remove(listener) == true
    }

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        this.onScaleChangeListenerList = (onScaleChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        return onScaleChangeListenerList?.remove(listener) == true
    }
}