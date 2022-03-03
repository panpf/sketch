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
package com.github.panpf.sketch.zoom.new.scale

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.VelocityTracker
import android.view.ViewConfiguration
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.zoom.internal.getPointerIndex
import kotlin.math.abs
import kotlin.math.sqrt

open class NewScaleDragGestureDetector constructor(context: Context) {

    companion object {
        private const val NAME = "ScaleDragGestureDetector"
        private const val INVALID_POINTER_ID = -1
    }

    private val logger = context.sketch.logger
    private val mTouchSlop: Float
    private val mMinimumVelocity: Float
    private val mDetector: ScaleGestureDetector
    private var mListener: OnScaleDragGestureListener? = null
    private var actionListener: ActionListener? = null
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private var mVelocityTracker: VelocityTracker? = null
    var isDragging = false
        private set
    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0

    init {
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        mDetector = ScaleGestureDetector(context, object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) return false
                mListener!!.onScale(
                    scaleFactor,
                    detector.focusX, detector.focusY
                )
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return mListener!!.onScaleBegin()
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                mListener!!.onScaleEnd()
            }
        })
    }

    fun setOnGestureListener(listener: OnScaleDragGestureListener?) {
        mListener = listener
    }

    fun setActionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    private fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float = try {
        ev.getY(mActivePointerIndex)
    } catch (e: Exception) {
        ev.y
    }

    val isScaling: Boolean
        get() = mDetector.isInProgress

    fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            mDetector.onTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            e.printStackTrace()
            return true
        }
        try {
            val action = ev.action
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> mActivePointerId = ev.getPointerId(0)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> mActivePointerId =
                    INVALID_POINTER_ID
                MotionEvent.ACTION_POINTER_UP -> {
                    // Ignore deprecation, ACTION_POINTER_ID_MASK and
                    // ACTION_POINTER_ID_SHIFT has same value and are deprecated
                    // You can have either deprecation or lint target api warning
                    val pointerIndex = getPointerIndex(ev.action)
                    val pointerId = ev.getPointerId(pointerIndex)
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        mActivePointerId = ev.getPointerId(newPointerIndex)
                        mLastTouchX = ev.getX(newPointerIndex)
                        mLastTouchY = ev.getY(newPointerIndex)
                    }
                }
            }
            mActivePointerIndex = ev
                .findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is
            e.printStackTrace()
            return true
        }
        return try {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    mVelocityTracker = VelocityTracker.obtain()
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    } else {
                        logger.w(NAME, "Velocity tracker is null")
                    }
                    mLastTouchX = getActiveX(ev)
                    mLastTouchY = getActiveY(ev)
                    isDragging = false
                    if (actionListener != null) {
                        actionListener!!.onActionDown(ev)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = getActiveX(ev)
                    val y = getActiveY(ev)
                    val dx = x - mLastTouchX
                    val dy = y - mLastTouchY
                    if (!isDragging) {
                        // Use Pythagoras to see if drag length is larger than
                        // touch slop
                        isDragging = sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                    }
                    if (isDragging) {
                        mListener!!.onDrag(dx, dy)
                        mLastTouchX = x
                        mLastTouchY = y
                        if (null != mVelocityTracker) {
                            mVelocityTracker!!.addMovement(ev)
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL -> {

                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.recycle()
                        mVelocityTracker = null
                    }
                    if (actionListener != null) {
                        actionListener!!.onActionCancel(ev)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        if (null != mVelocityTracker) {
                            mLastTouchX = getActiveX(ev)
                            mLastTouchY = getActiveY(ev)

                            // Compute velocity within the last 1000ms
                            mVelocityTracker!!.addMovement(ev)
                            mVelocityTracker!!.computeCurrentVelocity(1000)
                            val vX = mVelocityTracker!!.xVelocity
                            val vY = mVelocityTracker!!
                                .yVelocity

                            // If the velocity is greater than minVelocity, call
                            // listener
                            if (abs(vX).coerceAtLeast(abs(vY)) >= mMinimumVelocity) {
                                mListener!!.onFling(
                                    mLastTouchX, mLastTouchY, -vX,
                                    -vY
                                )
                            }
                        }
                    }

                    // Recycle Velocity Tracker
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.recycle()
                        mVelocityTracker = null
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
        fun onFling(
            startX: Float, startY: Float, velocityX: Float,
            velocityY: Float
        )

        fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)
        fun onScaleBegin(): Boolean
        fun onScaleEnd()
    }
}