/*
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.VelocityTracker
import android.view.ViewConfiguration
import com.github.panpf.sketch.Sketch
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.abs
import kotlin.math.sqrt

open class ScaleDragGestureDetector constructor(context: Context, sketch: Sketch) {

    companion object {
        private const val NAME = "ScaleDragGestureDetector"
        private const val INVALID_POINTER_ID = -1
    }

    private val logger = sketch.logger
    private val touchSlop: Float
    private val minimumVelocity: Float
    private val scaleGestureDetector: ScaleGestureDetector
    private var scaleDragGestureListener: OnScaleDragGestureListener? = null
    private var actionListener: ActionListener? = null
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var velocityTracker: VelocityTracker? = null
    private var activePointerId: Int = INVALID_POINTER_ID
    private var activePointerIndex: Int = 0

    var isDragging = false
        private set

    val isScaling: Boolean
        get() = scaleGestureDetector.isInProgress

    init {
        val configuration = ViewConfiguration.get(context)
        minimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        touchSlop = configuration.scaledTouchSlop.toFloat()
        scaleGestureDetector = ScaleGestureDetector(context, object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor =
                    detector.scaleFactor.takeIf { !isNaN(it) && !isInfinite(it) } ?: return false
                scaleDragGestureListener?.onScale(scaleFactor, detector.focusX, detector.focusY)
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean =
                scaleDragGestureListener?.onScaleBegin() == true

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                scaleDragGestureListener?.onScaleEnd()
            }
        })
    }

    fun setOnGestureListener(listener: OnScaleDragGestureListener?) {
        scaleDragGestureListener = listener
    }

    fun setActionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    private fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(activePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float = try {
        ev.getY(activePointerIndex)
    } catch (e: Exception) {
        ev.y
    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            scaleGestureDetector.onTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            e.printStackTrace()
            return true
        }
        try {
            val action = ev.action
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> activePointerId = ev.getPointerId(0)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> activePointerId =
                    INVALID_POINTER_ID
                MotionEvent.ACTION_POINTER_UP -> {
                    // Ignore deprecation, ACTION_POINTER_ID_MASK and
                    // ACTION_POINTER_ID_SHIFT has same value and are deprecated
                    // You can have either deprecation or lint target api warning
                    val pointerIndex = getPointerIndex(ev.action)
                    val pointerId = ev.getPointerId(pointerIndex)
                    if (pointerId == activePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        activePointerId = ev.getPointerId(newPointerIndex)
                        lastTouchX = ev.getX(newPointerIndex)
                        lastTouchY = ev.getY(newPointerIndex)
                    }
                }
            }
            activePointerIndex = ev
                .findPointerIndex(if (activePointerId != INVALID_POINTER_ID) activePointerId else 0)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            e.printStackTrace()
            return true
        }
        return try {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    velocityTracker = VelocityTracker.obtain()
                    if (null != velocityTracker) {
                        velocityTracker!!.addMovement(ev)
                    } else {
                        logger.w(NAME, "Velocity tracker is null")
                    }
                    lastTouchX = getActiveX(ev)
                    lastTouchY = getActiveY(ev)
                    isDragging = false
                    if (actionListener != null) {
                        actionListener!!.onActionDown(ev)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = getActiveX(ev)
                    val y = getActiveY(ev)
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY
                    if (!isDragging) {
                        // Use Pythagoras to see if drag length is larger than
                        // touch slop
                        isDragging = sqrt((dx * dx + dy * dy).toDouble()) >= touchSlop
                    }
                    if (isDragging) {
                        scaleDragGestureListener!!.onDrag(dx, dy)
                        lastTouchX = x
                        lastTouchY = y
                        if (null != velocityTracker) {
                            velocityTracker!!.addMovement(ev)
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL -> {

                    // Recycle Velocity Tracker
                    if (null != velocityTracker) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                    if (actionListener != null) {
                        actionListener!!.onActionCancel(ev)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        if (null != velocityTracker) {
                            lastTouchX = getActiveX(ev)
                            lastTouchY = getActiveY(ev)

                            // Compute velocity within the last 1000ms
                            velocityTracker!!.addMovement(ev)
                            velocityTracker!!.computeCurrentVelocity(1000)
                            val vX = velocityTracker!!.xVelocity
                            val vY = velocityTracker!!
                                .yVelocity

                            // If the velocity is greater than minVelocity, call
                            // listener
                            if (abs(vX).coerceAtLeast(abs(vY)) >= minimumVelocity) {
                                scaleDragGestureListener!!.onFling(
                                    lastTouchX, lastTouchY, -vX,
                                    -vY
                                )
                            }
                        }
                    }

                    // Recycle Velocity Tracker
                    if (null != velocityTracker) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                    if (actionListener != null) {
                        actionListener!!.onActionUp(ev)
                    }
                }
            }
            true
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            true
        }
    }

    interface ActionListener {
        fun onActionDown(ev: MotionEvent)
        fun onActionUp(ev: MotionEvent)
        fun onActionCancel(ev: MotionEvent)
    }

    interface OnScaleDragGestureListener {
        fun onDrag(dx: Float, dy: Float)
        fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float)
        fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)
        fun onScaleBegin(): Boolean
        fun onScaleEnd()
    }
}