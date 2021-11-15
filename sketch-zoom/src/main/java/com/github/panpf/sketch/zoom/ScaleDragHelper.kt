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
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.vm
import com.github.panpf.sketch.SLog.Companion.vmf
import com.github.panpf.sketch.Sketch.Companion.with
import com.github.panpf.sketch.util.SketchUtils.Companion.formatFloat
import com.github.panpf.sketch.util.SketchUtils.Companion.getMatrixScale
import com.github.panpf.sketch.zoom.ScaleDragGestureDetector.OnScaleDragGestureListener
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 缩放和拖拽处理，控制 Matrix 变化，更新 Matrix
 */
internal class ScaleDragHelper(context: Context, private val imageZoomer: ImageZoomer) :
    OnScaleDragGestureListener, ScaleDragGestureDetector.ActionListener {

    private val baseMatrix = Matrix() // 存储默认的缩放和位移信息
    private val supportMatrix = Matrix() // 存储用户通过触摸事件产生的缩放、位移和外部设置的旋转信息
    internal val drawMatrix = Matrix() // 存储 configMatrix 和 userMatrix 融合后的信息，用于绘制
    private var flingRunner // 执行飞速滚动
            : FlingRunner? = null
    private var locationRunner // 定位执行器
            : LocationRunner? = null
    private val scaleDragGestureDetector // 缩放和拖拽手势识别器
            : ScaleDragGestureDetector = ScaleDragGestureDetector(context.applicationContext)
    private val tempDisplayRectF = RectF()
    var horScrollEdge = EDGE_NONE // 横向滚动边界
        private set
    var verScrollEdge = EDGE_NONE // 竖向滚动边界
        private set
    private var disallowParentInterceptTouchEvent // 控制滑动或缩放中到达边缘了依然禁止父类拦截事件
            = false
    private var tempLastScaleFocusX = 0f
    private var tempLastScaleFocusY // 缓存最后一次缩放手势的坐标，在恢复缩放比例时使用
            = 0f
    var isZooming // 缩放中状态
            = false

    fun reset() {
        resetBaseMatrix()
        resetSupportMatrix()
        checkAndApplyMatrix()
    }

    fun recycle() {
        cancelFling()
    }

    fun onTouchEvent(event: MotionEvent): Boolean { // 定位操作不能被打断
        if (locationRunner != null) {
            if (locationRunner!!.isRunning) {
                if (isLoggable(SLog.VERBOSE)) {
                    vm(
                        ImageZoomer.MODULE,
                        "disallow parent intercept touch event. location running"
                    )
                }
                requestDisallowInterceptTouchEvent(imageZoomer.getImageView(), true)
                return true
            }
            locationRunner = null
        }

        // 缩放、拖拽手势处理
        val beforeInScaling = scaleDragGestureDetector.isScaling
        val beforeInDragging = scaleDragGestureDetector.isDragging
        val scaleDragHandled = scaleDragGestureDetector.onTouchEvent(event)
        val afterInScaling = scaleDragGestureDetector.isScaling
        val afterInDragging = scaleDragGestureDetector.isDragging
        disallowParentInterceptTouchEvent =
            !beforeInScaling && !afterInScaling && beforeInDragging && afterInDragging
        return scaleDragHandled
    }

    override fun onActionDown(ev: MotionEvent) {
        tempLastScaleFocusX = 0f
        tempLastScaleFocusY = 0f

        // 上来就禁止父View拦截事件
        if (isLoggable(SLog.VERBOSE)) {
            vm(ImageZoomer.MODULE, "disallow parent intercept touch event. action down")
        }
        requestDisallowInterceptTouchEvent(imageZoomer.getImageView(), true)

        // 取消快速滚动
        cancelFling()
    }

    override fun onDrag(dx: Float, dy: Float) {
        imageZoomer.getImageView()

        // 缩放中不能拖拽
        if (scaleDragGestureDetector.isScaling) {
            return
        }
        if (isLoggable(SLog.VERBOSE)) {
            vmf(ImageZoomer.MODULE, "drag. dx: %s, dy: %s", dx, dy)
        }
        supportMatrix.postTranslate(dx, dy)
        checkAndApplyMatrix()
        if (!imageZoomer.isAllowParentInterceptOnEdge || scaleDragGestureDetector.isScaling || disallowParentInterceptTouchEvent) {
            if (isLoggable(SLog.VERBOSE)) {
                vmf(
                    ImageZoomer.MODULE,
                    "disallow parent intercept touch event. onDrag. allowParentInterceptOnEdge=%s, scaling=%s, tempDisallowParentInterceptTouchEvent=%s",
                    imageZoomer.isAllowParentInterceptOnEdge,
                    scaleDragGestureDetector.isScaling,
                    disallowParentInterceptTouchEvent
                )
            }
            requestDisallowInterceptTouchEvent(imageZoomer.getImageView(), true)
            return
        }

        // 滑动到边缘时父类可以拦截触摸事件，但暂时不处理顶部和底部边界
//        if (horScrollEdge == EDGE_BOTH || (horScrollEdge == EDGE_START && dx >= 1f) || (horScrollEdge == EDGE_END && dx <= -1f)
//                    || verScrollEdge == EDGE_BOTH || (verScrollEdge == EDGE_START && dy >= 1f) || (verScrollEdge == EDGE_END && dy <= -1f)) {
        if (horScrollEdge == EDGE_BOTH || horScrollEdge == EDGE_START && dx >= 1f || horScrollEdge == EDGE_END && dx <= -1f) {
            if (isLoggable(SLog.VERBOSE)) {
                vmf(
                    ImageZoomer.MODULE,
                    "allow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                    getScrollEdgeName(horScrollEdge),
                    getScrollEdgeName(verScrollEdge)
                )
            }
            requestDisallowInterceptTouchEvent(imageZoomer.getImageView(), false)
        } else {
            if (isLoggable(SLog.VERBOSE)) {
                vmf(
                    ImageZoomer.MODULE,
                    "disallow parent intercept touch event. onDrag. scrollEdge=%s-%s",
                    getScrollEdgeName(horScrollEdge),
                    getScrollEdgeName(verScrollEdge)
                )
            }
            requestDisallowInterceptTouchEvent(imageZoomer.getImageView(), true)
        }
    }

    override fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float) {
        flingRunner = FlingRunner(imageZoomer, this@ScaleDragHelper)
        flingRunner!!.fling(velocityX.toInt(), velocityY.toInt())
        val onDragFlingListener = imageZoomer.onDragFlingListener
        onDragFlingListener?.onFling(startX, startY, velocityX, velocityY)
    }

    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
        var newScaleFactor = scaleFactor
        if (isLoggable(SLog.VERBOSE)) {
            vmf(
                ImageZoomer.MODULE,
                "scale. scaleFactor: %s, dx: %s, dy: %s",
                newScaleFactor,
                focusX,
                focusY
            )
        }
        tempLastScaleFocusX = focusX
        tempLastScaleFocusY = focusY
        val oldSuppScale = supportZoomScale
        var newSuppScale = oldSuppScale * newScaleFactor
        if (newScaleFactor > 1.0f) {
            // 放大的时候，如果当前已经超过最大缩放比例，就调慢缩放速度
            // 这样就能模拟出超过最大缩放比例时很难再继续放大有种拉橡皮筋的感觉
            val maxSuppScale = imageZoomer.maxZoomScale / getMatrixScale(baseMatrix)
            if (oldSuppScale >= maxSuppScale) {
                var addScale = newSuppScale - oldSuppScale
                addScale *= 0.4f
                newSuppScale = oldSuppScale + addScale
                newScaleFactor = newSuppScale / oldSuppScale
            }
        } else if (newScaleFactor < 1.0f) {
            // 缩小的时候，如果当前已经小于最小缩放比例，就调慢缩放速度
            // 这样就能模拟出小于最小缩放比例时很难再继续缩小有种拉橡皮筋的感觉
            val minSuppScale = imageZoomer.minZoomScale / getMatrixScale(baseMatrix)
            if (oldSuppScale <= minSuppScale) {
                var addScale = newSuppScale - oldSuppScale
                addScale *= 0.4f
                newSuppScale = oldSuppScale + addScale
                newScaleFactor = newSuppScale / oldSuppScale
            }
        }
        supportMatrix.postScale(newScaleFactor, newScaleFactor, focusX, focusY)
        checkAndApplyMatrix()
        val onScaleChangeListener = imageZoomer.onScaleChangeListener
        onScaleChangeListener?.onScaleChanged(newScaleFactor, focusX, focusY)
    }

    override fun onScaleBegin(): Boolean {
        if (isLoggable(SLog.VERBOSE)) {
            vm(ImageZoomer.MODULE, "scale begin")
        }
        isZooming = true
        return true
    }

    override fun onScaleEnd() {
        if (isLoggable(SLog.VERBOSE)) {
            vm(ImageZoomer.MODULE, "scale end")
        }
        val currentScale = formatFloat(zoomScale, 2)
        val overMinZoomScale = currentScale < formatFloat(imageZoomer.minZoomScale, 2)
        val overMaxZoomScale = currentScale > formatFloat(imageZoomer.maxZoomScale, 2)
        if (!overMinZoomScale && !overMaxZoomScale) {
            isZooming = false
            imageZoomer.onMatrixChanged()
        }
    }

    override fun onActionUp(ev: MotionEvent) {
        val currentScale = formatFloat(zoomScale, 2)
        if (currentScale < formatFloat(imageZoomer.minZoomScale, 2)) {
            // 如果当前缩放倍数小于最小倍数就回滚至最小倍数
            val drawRectF = RectF()
            getDrawRect(drawRectF)
            if (!drawRectF.isEmpty) {
                zoom(imageZoomer.minZoomScale, drawRectF.centerX(), drawRectF.centerY(), true)
            }
        } else if (currentScale > formatFloat(imageZoomer.maxZoomScale, 2)) {
            // 如果当前缩放倍数大于最大倍数就回滚至最大倍数
            if (tempLastScaleFocusX != 0f && tempLastScaleFocusY != 0f) {
                zoom(imageZoomer.maxZoomScale, tempLastScaleFocusX, tempLastScaleFocusY, true)
            }
        }
    }

    override fun onActionCancel(ev: MotionEvent) {
        onActionUp(ev)
    }

    private fun resetBaseMatrix() {
        baseMatrix.reset()
        val viewSize = imageZoomer.viewSize
        val imageSize = imageZoomer.imageSize
        val drawableSize = imageZoomer.drawableSize
        val readMode = imageZoomer.isReadMode
        val scaleType = imageZoomer.scaleType
        val drawableWidth =
            if (imageZoomer.rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (imageZoomer.rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val imageWidth =
            if (imageZoomer.rotateDegrees % 180 == 0) imageSize.width else imageSize.height
        val imageHeight =
            if (imageZoomer.rotateDegrees % 180 == 0) imageSize.height else imageSize.width
        val imageThanViewLarge = drawableWidth > viewSize.width || drawableHeight > viewSize.height
        val finalScaleType: ScaleType? = if (scaleType == ScaleType.MATRIX) {
            ScaleType.FIT_CENTER
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            if (imageThanViewLarge) ScaleType.FIT_CENTER else ScaleType.CENTER
        } else {
            scaleType
        }
        val initScale = imageZoomer.getZoomScales().initZoomScale
        val sizeCalculator = with(imageZoomer.getImageView().context).configuration.sizeCalculator
        if (readMode && sizeCalculator.canUseReadModeByHeight(imageWidth, imageHeight)) {
            baseMatrix.postScale(initScale, initScale)
        } else if (readMode && sizeCalculator.canUseReadModeByWidth(imageWidth, imageHeight)) {
            baseMatrix.postScale(initScale, initScale)
        } else if (finalScaleType == ScaleType.CENTER) {
            baseMatrix.postScale(initScale, initScale)
            baseMatrix.postTranslate(
                (viewSize.width - drawableWidth) / 2f,
                (viewSize.height - drawableHeight) / 2f
            )
        } else if (finalScaleType == ScaleType.CENTER_CROP) {
            baseMatrix.postScale(initScale, initScale)
            baseMatrix.postTranslate(
                (viewSize.width - drawableWidth * initScale) / 2f,
                (viewSize.height - drawableHeight * initScale) / 2f
            )
        } else if (finalScaleType == ScaleType.FIT_START) {
            baseMatrix.postScale(initScale, initScale)
            baseMatrix.postTranslate(0f, 0f)
        } else if (finalScaleType == ScaleType.FIT_END) {
            baseMatrix.postScale(initScale, initScale)
            baseMatrix.postTranslate(0f, viewSize.height - drawableHeight * initScale)
        } else if (finalScaleType == ScaleType.FIT_CENTER) {
            baseMatrix.postScale(initScale, initScale)
            baseMatrix.postTranslate(0f, (viewSize.height - drawableHeight * initScale) / 2f)
        } else if (finalScaleType == ScaleType.FIT_XY) {
            val mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(
                0f, 0f, viewSize.width.toFloat(), viewSize.height
                    .toFloat()
            )
            baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL)
        }
    }

    private fun resetSupportMatrix() {
        supportMatrix.reset()
        supportMatrix.postRotate(imageZoomer.rotateDegrees.toFloat())
    }

    private fun checkAndApplyMatrix() {
        if (!checkMatrixBounds()) {
            return
        }
        val imageView = imageZoomer.getImageView()
        check(ScaleType.MATRIX == imageView.scaleType) { "ImageView scaleType must be is MATRIX" }
        imageZoomer.onMatrixChanged()
    }

    private fun checkMatrixBounds(): Boolean {
        val drawRectF = tempDisplayRectF
        getDrawRect(drawRectF)
        if (drawRectF.isEmpty) {
            horScrollEdge = EDGE_NONE
            verScrollEdge = EDGE_NONE
            return false
        }
        val displayHeight = drawRectF.height()
        val displayWidth = drawRectF.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = imageZoomer.viewSize.height
        when {
            displayHeight.toInt() <= viewHeight -> {
                deltaY = when (imageZoomer.scaleType) {
                    ScaleType.FIT_START -> -drawRectF.top
                    ScaleType.FIT_END -> viewHeight - displayHeight - drawRectF.top
                    else -> (viewHeight - displayHeight) / 2 - drawRectF.top
                }
            }
            drawRectF.top.toInt() > 0 -> {
                deltaY = -drawRectF.top
            }
            drawRectF.bottom.toInt() < viewHeight -> {
                deltaY = viewHeight - drawRectF.bottom
            }
        }
        val viewWidth = imageZoomer.viewSize.width
        when {
            displayWidth.toInt() <= viewWidth -> {
                deltaX = when (imageZoomer.scaleType) {
                    ScaleType.FIT_START -> -drawRectF.left
                    ScaleType.FIT_END -> viewWidth - displayWidth - drawRectF.left
                    else -> (viewWidth - displayWidth) / 2 - drawRectF.left
                }
            }
            drawRectF.left.toInt() > 0 -> {
                deltaX = -drawRectF.left
            }
            drawRectF.right.toInt() < viewWidth -> {
                deltaX = viewWidth - drawRectF.right
            }
        }

        // Finally actually translate the matrix
        supportMatrix.postTranslate(deltaX, deltaY)
        verScrollEdge = when {
            displayHeight.toInt() <= viewHeight -> EDGE_BOTH
            drawRectF.top.toInt() >= 0 -> EDGE_START
            drawRectF.bottom.toInt() <= viewHeight -> EDGE_END
            else -> EDGE_NONE
        }
        horScrollEdge = when {
            displayWidth.toInt() <= viewWidth -> EDGE_BOTH
            drawRectF.left.toInt() >= 0 -> EDGE_START
            drawRectF.right.toInt() <= viewWidth -> EDGE_END
            else -> EDGE_NONE
        }
        return true
    }

    fun cancelFling() {
        if (flingRunner != null) {
            flingRunner!!.cancelFling()
            flingRunner = null
        }
    }

    fun translateBy(dx: Float, dy: Float) {
        supportMatrix.postTranslate(dx, dy)
        checkAndApplyMatrix()
    }

    fun scaleBy(addScale: Float, focalX: Float, focalY: Float) {
        supportMatrix.postScale(addScale, addScale, focalX, focalY)
        checkAndApplyMatrix()
    }

    /**
     * 定位到预览图上指定的位置（不用考虑旋转角度）
     */
    fun location(x: Float, y: Float, animate: Boolean) {
        var newX = x
        var newY = y
        val imageViewSize = imageZoomer.viewSize
        val drawableSize = imageZoomer.drawableSize

        // 旋转定位点
        val pointF = PointF(newX, newY)
        Size.rotatePoint(pointF, imageZoomer.rotateDegrees, drawableSize)
        newX = pointF.x
        newY = pointF.y
        cancelFling()
        if (locationRunner != null) {
            locationRunner!!.cancel()
        }
        val imageViewWidth = imageViewSize.width
        val imageViewHeight = imageViewSize.height

        // 充满的时候是无法移动的，因此先放到最大
        val scale = formatFloat(zoomScale, 2)
        val fullZoomScale = formatFloat(imageZoomer.fullZoomScale, 2)
        if (scale == fullZoomScale) {
            zoom(imageZoomer.maxZoomScale, newX, newY, false)
        }
        val drawRectF = RectF()
        getDrawRect(drawRectF)

        // 传进来的位置是预览图上的位置，需要乘以当前的缩放倍数才行
        val currentScale = zoomScale
        val scaleLocationX = (newX * currentScale).toInt()
        val scaleLocationY = (newY * currentScale).toInt()
        val trimScaleLocationX = scaleLocationX.coerceAtLeast(0).coerceAtMost(
            drawRectF.width()
                .toInt()
        )
        val trimScaleLocationY = scaleLocationY.coerceAtLeast(0).coerceAtMost(
            drawRectF.height()
                .toInt()
        )

        // 让定位点显示在屏幕中间
        val centerLocationX = trimScaleLocationX - imageViewWidth / 2
        val centerLocationY = trimScaleLocationY - imageViewHeight / 2
        val trimCenterLocationX = centerLocationX.coerceAtLeast(0)
        val trimCenterLocationY = centerLocationY.coerceAtLeast(0)

        // 当前显示区域的left和top就是开始位置
        val startX = abs(drawRectF.left.toInt())
        val startY = abs(drawRectF.top.toInt())
        if (isLoggable(SLog.VERBOSE)) {
            vmf(
                ImageZoomer.MODULE,
                "location. start=%dx%d, end=%dx%d",
                startX,
                startY,
                trimCenterLocationX,
                trimCenterLocationY
            )
        }
        if (animate) {
            locationRunner = LocationRunner(imageZoomer, this)
            locationRunner!!.location(startX, startY, trimCenterLocationX, trimCenterLocationY)
        } else {
            translateBy(
                -(trimCenterLocationX - startX).toFloat(),
                -(trimCenterLocationY - startY).toFloat()
            )
        }
    }

    fun zoom(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        if (animate) {
            ZoomRunner(imageZoomer, this, zoomScale, scale, focalX, focalY).zoom()
        } else {
            val baseScale = defaultZoomScale
            val supportZoomScale = supportZoomScale
            val finalScale = scale / baseScale
            val addScale = finalScale / supportZoomScale
            scaleBy(addScale, focalX, focalY)
        }
    }

    fun getDrawMatrix(): Matrix {
        drawMatrix.set(baseMatrix)
        drawMatrix.postConcat(supportMatrix)
        return drawMatrix
    }

    val defaultZoomScale: Float
        get() = getMatrixScale(baseMatrix)
    val supportZoomScale: Float
        get() = getMatrixScale(supportMatrix)
    val zoomScale: Float
        get() = getMatrixScale(getDrawMatrix())

    /**
     * 获取绘制区域
     */
    fun getDrawRect(rectF: RectF) {
        if (!imageZoomer.isWorking) {
            if (isLoggable(SLog.VERBOSE)) {
                vm(ImageZoomer.MODULE, "not working. getDrawRect")
            }
            rectF.setEmpty()
            return
        }
        val drawableSize = imageZoomer.drawableSize
        rectF[0f, 0f, drawableSize.width.toFloat()] = drawableSize.height.toFloat()
        getDrawMatrix().mapRect(rectF)
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    fun getVisibleRect(rect: Rect) {
        if (!imageZoomer.isWorking) {
            if (isLoggable(SLog.VERBOSE)) {
                vm(ImageZoomer.MODULE, "not working. getVisibleRect")
            }
            rect.setEmpty()
            return
        }
        val drawRectF = RectF()
        getDrawRect(drawRectF)
        if (drawRectF.isEmpty) {
            rect.setEmpty()
            return
        }
        val viewSize = imageZoomer.viewSize
        val drawableSize = imageZoomer.drawableSize
        val displayWidth = drawRectF.width()
        val displayHeight = drawRectF.height()
        val drawableWidth =
            if (imageZoomer.rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (imageZoomer.rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val widthScale = displayWidth / drawableWidth
        val heightScale = displayHeight / drawableHeight
        var right: Float
        var left: Float = if (drawRectF.left >= 0) {
            0f
        } else {
            abs(drawRectF.left)
        }
        right = if (displayWidth >= viewSize.width) {
            viewSize.width + left
        } else {
            drawRectF.right - drawRectF.left
        }
        var bottom: Float
        var top: Float = if (drawRectF.top >= 0) {
            0f
        } else {
            abs(drawRectF.top)
        }
        bottom = if (displayHeight >= viewSize.height) {
            viewSize.height + top
        } else {
            drawRectF.bottom - drawRectF.top
        }
        left /= widthScale
        right /= widthScale
        top /= heightScale
        bottom /= heightScale
        rect[left.roundToInt(), top.roundToInt(), right.roundToInt()] =
            bottom.roundToInt()

        // 将可见区域转回原始角度
        Size.reverseRotateRect(rect, imageZoomer.rotateDegrees, drawableSize)
    }

    /**
     * 可以横向滚动
     */
    fun canScrollHorizontally(): Boolean {
        return horScrollEdge != EDGE_BOTH
    }

    /**
     * 可以垂直滚动
     */
    fun canScrollVertically(): Boolean {
        return verScrollEdge != EDGE_BOTH
    }

    companion object {
        private const val EDGE_NONE = -1
        private const val EDGE_START = 0
        private const val EDGE_END = 1
        private const val EDGE_BOTH = 2

        /**
         * 获取边界名称，log 专用
         */
        private fun getScrollEdgeName(scrollEdge: Int): String {
            return when (scrollEdge) {
                EDGE_NONE -> "NONE"
                EDGE_START -> "START"
                EDGE_END -> "END"
                EDGE_BOTH -> "BOTH"
                else -> "UNKNOWN"
            }
        }

        private fun requestDisallowInterceptTouchEvent(
            imageView: ImageView,
            disallowIntercept: Boolean
        ) {
            val parent = imageView.parent
            parent?.requestDisallowInterceptTouchEvent(disallowIntercept)
        }
    }

    init {
        scaleDragGestureDetector.setOnGestureListener(this)
        scaleDragGestureDetector.setActionListener(this)
    }
}