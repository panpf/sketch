///*
// * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch.sample.widget
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.View
//import android.widget.FrameLayout
//import androidx.annotation.FloatRange
//import androidx.customview.widget.ViewDragHelper
//import kotlin.math.abs
//
//class SwipeBackLayout @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null
//) : FrameLayout(context, attrs) {
//
//    private lateinit var viewDragHelper: ViewDragHelper
//    private var dragOriginLeft = Int.MIN_VALUE
//    private var dragOriginTop = Int.MIN_VALUE
//
//    /**
//     * Threshold of scroll, we will back, when scrollPercent over this value
//     */
//    var scrollThreshold: Float = 0.3f
//    var callback: Callback? = null
//
//    init {
//        viewDragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
//
//            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
//                if (dragOriginLeft == Int.MIN_VALUE && dragOriginTop == Int.MIN_VALUE) {
//                    dragOriginLeft = child.left
//                    dragOriginTop = child.top
//                }
//                // If the parent container is a ViewPager,
//                // It can prevent the horizontal sliding of ViewPager from triggering when the
//                // vertical sliding is slightly inclined after Captured, causing the drag to be interrupted
//                parent.requestDisallowInterceptTouchEvent(true)
//                return true
//            }
//
//            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
//                return left
//            }
//
//            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
//                return top
//            }
//
//            override fun getViewHorizontalDragRange(child: View): Int {
//                return 0
//            }
//
//            override fun getViewVerticalDragRange(child: View): Int {
//                return 100000
//            }
//
//            override fun onViewPositionChanged(
//                changedView: View, left: Int, top: Int, dx: Int, dy: Int
//            ) {
//                super.onViewPositionChanged(changedView, left, top, dx, dy)
//                val capturedView = getChildAt(0) ?: return
//                val capturedViewTop = capturedView.top
//                val dragOriginTop = dragOriginTop.takeIf { it != Int.MIN_VALUE } ?: 0
//                val distance = abs(capturedViewTop - dragOriginTop)
//                val viewHeight = height
//                val progress = distance / viewHeight.toFloat()
//                callback?.onProgressChanged(progress)
//
//                if (progress >= 1f) {
//                    callback?.onBack()
//                }
//            }
//
//            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
//                val dragOriginLeft = dragOriginLeft.takeIf { it != Int.MIN_VALUE } ?: 0
//                val dragOriginTop = dragOriginTop.takeIf { it != Int.MIN_VALUE } ?: 0
//                val capturedView = getChildAt(0) ?: return
//                val capturedViewTop = capturedView.top
//                val distance = abs(capturedViewTop - dragOriginTop)
//                val viewHeight = height
//                val progress = distance / viewHeight.toFloat()
//                val exit = progress >= scrollThreshold || abs(yvel) > 0
//                val targetTop = if (exit) {
//                    if (capturedViewTop > 0) viewHeight else -viewHeight
//                } else {
//                    dragOriginTop
//                }
//                viewDragHelper.settleCapturedViewAt(dragOriginLeft, targetTop)
//                invalidate()
//            }
//        }).apply {
//            val density = resources.displayMetrics.density
//            minVelocity = 400 * density
//        }
//    }
//
//    fun back() {
//        val capturedView = getChildAt(0) ?: return
//        viewDragHelper.captureChildView(capturedView, 0)
//        val capturedViewLeft = capturedView.left
//        val capturedViewTop = capturedView.top
//        val viewHeight = height
//        val targetTop = if (capturedViewTop >= 0) viewHeight else -viewHeight
//        viewDragHelper.smoothSlideViewTo(capturedView, capturedViewLeft, targetTop)
//        invalidate()
//    }
//
//    override fun computeScroll() {
//        super.computeScroll()
//        if (viewDragHelper.continueSettling(true)) {
//            invalidate()
//        }
//    }
//
//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        return viewDragHelper.shouldInterceptTouchEvent(ev)
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        viewDragHelper.processTouchEvent(event)
//        return true
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        require(childCount <= 1) { "There is only one child View" }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }
//
//    interface Callback {
//        fun onProgressChanged(@FloatRange(from = 0.0, to = 1.0) progress: Float)
//        fun onBack()
//    }
//}