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
@file:Suppress("SetterBackingFieldAssignment")

package com.github.panpf.sketch.zoom.internal

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.zoom.SketchZoomImageView
import com.github.panpf.sketch.zoom.DefaultReadModeConditions
import com.github.panpf.sketch.zoom.ReadModeConditions
import com.github.panpf.sketch.zoom.block.Block
import com.github.panpf.sketch.zoom.block.BlockDisplayer
import kotlin.math.abs

/**
 * 图片缩放器，接收触摸事件，变换 [Matrix]，改变图片的显示效果，代理点击和长按事件
 */
// TODO 解决嵌套在别的可滑动 View 中时，会导致 ArrayIndexOutOfBoundsException 异常，初步猜测 requestDisallowInterceptTouchEvent 引起的
// todo 重构
class ImageZoomer(val imageView: SketchZoomImageView) {

    companion object {
        const val MODULE = "ImageZoomer"
    }

    /**
     * 获取 [ScaleType]
     */
    private var scaleTypeCache: ScaleType? = null // ImageView 原本的 ScaleType
    var scaleType: ScaleType?
        get() = scaleTypeCache
        set(value) {
            val oldValue = scaleTypeCache
            scaleTypeCache = value
            if (oldValue != value) {
                reset("setScaleType")
            }
        }
    private val sizes = Sizes()
    private var zoomScales: ZoomScales = AdaptiveTwoLevelScales(this)
    val logger by lazy {
        imageView.sketch.logger
    }
    var readModeConditions: ReadModeConditions = DefaultReadModeConditions()
        set(value) {
            field = value
            // todo 重置，因为此属性的改变会影响图片的默认缩放效果
        }

    /**
     * 获取旋转角度
     */
    var rotateDegrees // 旋转角度
            = 0
        private set

    /**
     * 缩放动画持续时间，单位毫秒
     */
    var zoomDuration = 200 // 双击缩放动画持续时间
        set(milliseconds) {
            if (milliseconds > 0) {
                field = milliseconds
            }
        }

    /**
     * 开启阅读模式，开启后尺寸类似长微博或清明上河图的图片将默认充满屏幕显示
     */
    var isReadMode = false // 阅读模式下，竖图将默认横向充满屏幕
        set(value) {
            if (field != value) {
                field = value
                reset("setReadMode")
            }
        }

    /**
     * 缩放动画插值器
     */
    var zoomInterpolator: Interpolator = AccelerateDecelerateInterpolator()

    /**
     * 允许父类在滑动到边缘的时候拦截事件（默认 true）
     */
    var isAllowParentInterceptOnEdge = true // 允许父 ViewGroup 在滑动到边缘时拦截事件

    /**
     * 设置飞速滚动监听器
     */
    var onDragFlingListener: OnDragFlingListener? = null

    /**
     * 设置缩放监听器
     */
    var onScaleChangeListener: OnScaleChangeListener? = null
    private var onRotateChangeListener: OnRotateChangeListener? = null

//    /**
//     * 设置单击监听器
//     */
//    var onViewTapListener: OnViewTapListener? = null

//    /**
//     * 长按监听器
//     */
//    var onViewLongPressListener: OnViewLongPressListener? = null
    private var onMatrixChangeListenerList: ArrayList<OnMatrixChangeListener>? = null
    private val tapHelper: TapHelper
    private val scaleDragHelper: ScaleDragHelper
    private val scrollBarHelper: ScrollBarHelper

    /**
     * 获取分块显示器
     */
    val blockDisplayer: BlockDisplayer  // todo 将 blockDisplayer 改成插件的形式放到 Zoomer 中

    init {
        val appContext = imageView.context.applicationContext
        tapHelper = TapHelper(appContext, this)
        scaleDragHelper = ScaleDragHelper(appContext, this)
        scrollBarHelper = ScrollBarHelper(appContext, this)
        blockDisplayer = BlockDisplayer(appContext, this)
    }

    /* -----------主要方法----------- */
    /**
     * 当 [ImageView] 的 [Drawable]、[ScaleType]、尺寸发生改变或旋转角度、阅读模式修改了需要调用此方法重置
     *
     * @return true：重置以后可以工作，false：重置以后无法工作，通常是新的 [Drawable] 不满足条件导致
     */
    fun reset(why: String): Boolean {
        recycle(why)
        sizes.resetSizes(imageView)
        if (!isWorking) {
            return false
        }

        // 为什么要每次都重新获取 ScaleType ？因为 reset 是可以反复执行的，在此之前 ScaleType 可能会改变
        scaleTypeCache = imageView.scaleType
        imageView.scaleType = ScaleType.MATRIX
        zoomScales.reset(
            imageView.context,
            sizes,
            scaleTypeCache,
            rotateDegrees.toFloat(),
            isReadMode
        )
        scaleDragHelper.reset()
        blockDisplayer.reset()
        return true
    }

    /**
     * 不需要缩放时回收
     */
    fun recycle(why: String) {
        if (!isWorking) {
            return
        }
        sizes.clean()
        zoomScales.clean()
        scaleDragHelper.recycle()
        blockDisplayer.recycle(why)

        // 清空 Matrix，这很重要
        imageView.imageMatrix = null

        // 恢复 ScaleType 这很重要，一定要在 clean 以后执行，要不会被 {@link FunctionCallbackView#setScaleType(ScaleType)} 方法覆盖
        val scaleType = this@ImageZoomer.scaleTypeCache
        if (scaleType != null) {
            imageView.scaleType = scaleType
            this@ImageZoomer.scaleTypeCache = null
        }
    }

    /**
     * [ImageZoomer] 工作中
     */
    val isWorking: Boolean
        get() = !sizes.isEmpty

    /* -----------ImageView 回调----------- */
    /**
     * 绘制回调
     */
    fun onDraw(canvas: Canvas) {
        if (!isWorking) {
            return
        }
        blockDisplayer.onDraw(canvas)
        scrollBarHelper.onDraw(canvas) // scrollBarHelper.onDraw 必须在 blockDisplayer.onDraw 之后执行，这样才不会被覆盖掉
    }

    /**
     * 事件回调
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isWorking) {
            return false
        }
        val scaleAndDragConsumed = scaleDragHelper.onTouchEvent(event)
        val tapConsumed = tapHelper.onTouchEvent(event)
        return scaleAndDragConsumed || tapConsumed
    }

    /* -----------内部组件回调----------- */
    fun onMatrixChanged() {
        // 在 setImageMatrix 前面执行，省了再执行一次 imageView.invalidate()
        scrollBarHelper.onMatrixChanged()
        blockDisplayer.onMatrixChanged()
        imageView.imageMatrix = scaleDragHelper.getDrawMatrix()
        val onMatrixChangeListenerList = onMatrixChangeListenerList
        if (onMatrixChangeListenerList != null && onMatrixChangeListenerList.isNotEmpty()) {
            var w = 0
            val size = onMatrixChangeListenerList.size
            while (w < size) {
                onMatrixChangeListenerList[w].onMatrixChanged(this)
                w++
            }
        }
    }

    /* -----------交互功能----------- */

    /**
     * 定位到预览图上指定的位置，不用考虑缩放和旋转
     *
     * @param x 预览图上指定位置的 x 坐标
     * @param y 预览图上指定位置的 y 坐标
     * @return true：定位成功；false：定位失败，通常是 [ImageZoomer] 尚未开始工作
     */
    fun location(x: Float, y: Float, animate: Boolean = false): Boolean {
        if (!isWorking) {
            logger.w(MODULE, "not working. location")
            return false
        }
        scaleDragHelper.location(x, y, animate)
        return true
    }

    /**
     * 缩放，不用考虑缩放和旋转
     *
     * @param scale   缩放比例
     * @param focalX  缩放中心点在预览图上的 x 坐标
     * @param focalY  缩放中心点在预览图上的 y 坐标
     * @param animate 是否使用动画
     * @return true：缩放成功；false：缩放失败，通常是 [ImageZoomer] 尚未开始工作或者缩放比例小于最小缩放比例或大于最大缩放比例
     */
    fun zoom(scale: Float, focalX: Float, focalY: Float, animate: Boolean): Boolean {
        if (!isWorking) {
            logger.w(MODULE, "not working. zoom(float, float, float, boolean)")
            return false
        }
        if (scale < zoomScales.minZoomScale || scale > zoomScales.maxZoomScale) {
            logger.w(
                MODULE,
                "Scale must be within the range of ${zoomScales.minZoomScale}(minScale) and ${zoomScales.maxZoomScale}(maxScale). ${scale}"
            )
            return false
        }
        scaleDragHelper.zoom(scale, focalX, focalY, animate)
        return true
    }

    /**
     * 缩放，不用考虑缩放和旋转，默认缩放中心点是 [ImageView] 的中心，默认不使用动画
     *
     * @param scale 缩放比例
     * @return true：缩放成功；false：缩放失败，通常是 [ImageZoomer] 尚未开始工作或者缩放比例小于最小缩放比例或大于最大缩放比例
     */
    @JvmOverloads
    fun zoom(scale: Float, animate: Boolean = false): Boolean {
        if (!isWorking) {
            logger.w(MODULE, "not working. zoom(float, boolean)")
            return false
        }
        val imageView = getImageView()
        return zoom(
            scale,
            (imageView.right / 2).toFloat(),
            (imageView.bottom / 2).toFloat(),
            animate
        )
    }

    /**
     * 旋转图片，旋转会清除已经存在的缩放和移动数据
     *
     * @param degrees 旋转角度，只能是 90°、180°、270°、360°
     * @return true：旋转成功；false：旋转失败，通常是 [ImageZoomer] 尚未开始工作或者旋转角度不是 90 的倍数
     */
    // TODO: 16/9/28 支持旋转动画
    // TODO: 16/9/28 增加手势旋转功能
    // TODO: 16/10/19 研究任意角度旋转和旋转时不清空位移以及缩放信息
    fun rotateTo(degrees: Int): Boolean {
        var newDegrees = degrees
        if (!isWorking) {
            logger.w(MODULE, "not working. rotateTo")
            return false
        }
        if (rotateDegrees == newDegrees) {
            return false
        }
        if (newDegrees % 90 != 0) {
            logger.w(MODULE, "rotate degrees must be in multiples of 90")
            return false
        }
        newDegrees %= 360
        if (newDegrees <= 0) {
            newDegrees = 360 - newDegrees
        }
        rotateDegrees = newDegrees
        reset("rotateTo")
        if (onRotateChangeListener != null) {
            onRotateChangeListener!!.onRotateChanged(this)
        }
        return true
    }

    /**
     * 在当前旋转角度的基础上旋转一定角度，旋转会清除已经存在的缩放和移动数据
     *
     * @param degrees 旋转角度，只能是 90°、180°、270°、360°
     * @return true：旋转成功；false：旋转失败，通常是 [ImageZoomer] 尚未开始工作或者旋转角度不是 90 的倍数
     */
    fun rotateBy(degrees: Int): Boolean {
        return rotateTo(degrees + rotateDegrees)
    }
    /* -----------可获取信息----------- */
    /**
     * 可以横向滚动
     */
    fun canScrollHorizontally(): Boolean {
        return scaleDragHelper.canScrollHorizontally()
    }

    /**
     * 可以垂直滚动
     */
    fun canScrollVertically(): Boolean {
        return scaleDragHelper.canScrollVertically()
    }

    val horScrollEdge: Int
        get() = scaleDragHelper.horScrollEdge
    val verScrollEdge: Int
        get() = scaleDragHelper.verScrollEdge

    fun getImageView(): ImageView {
        return imageView
    }

    /**
     * 获取 [ImageView] 的尺寸
     */
    val viewSize: Size
        get() = sizes.viewSize

    /**
     * 获取图片原始尺寸
     */
    val imageSize: Size
        get() = sizes.imageSize

    /**
     * 获取预览图的尺寸
     */
    val drawableSize: Size
        get() = sizes.drawableSize

    /**
     * 拷贝绘制 [Matrix] 的参数
     */
    fun getDrawMatrix(matrix: Matrix) {
        matrix.set(scaleDragHelper.drawMatrix)
    }

    /**
     * 获取绘制区域
     */
    fun getDrawRect(rectF: RectF) {
        scaleDragHelper.getDrawRect(rectF)
    }

    /**
     * 获取预览图上用户可以看到的区域（不受旋转影响）
     */
    fun getVisibleRect(rect: Rect) {
        scaleDragHelper.getVisibleRect(rect)
    }

    /**
     * 获取当前缩放比例
     */
    val zoomScale: Float
        get() = scaleDragHelper.zoomScale

    /**
     * 获取 Base 缩放比例
     */
    val baseZoomScale: Float
        get() = scaleDragHelper.defaultZoomScale

    /**
     * 获取 support 缩放比例
     */
    val supportZoomScale: Float
        get() = scaleDragHelper.supportZoomScale

    /**
     * 获取能够让图片完整显示的缩放比例
     */
    val fullZoomScale: Float
        get() = zoomScales.fullZoomScale

    /**
     * 获取能够让图片充满 [ImageView] 显示的缩放比例
     */
    val fillZoomScale: Float
        get() = zoomScales.fillZoomScale

    /**
     * 获取能够让图片按原图比例一比一显示的缩放比例
     */
    val originZoomScale: Float
        get() = zoomScales.originZoomScale

    /**
     * 获取最小缩放比例
     */
    val minZoomScale: Float
        get() = zoomScales.minZoomScale

    /**
     * 获取最大缩放比例
     */
    val maxZoomScale: Float
        get() = zoomScales.maxZoomScale

    /**
     * 获取双击缩放比例
     */
    val doubleClickZoomScales: FloatArray
        get() = zoomScales.zoomScales!!

    /**
     * 正在缩放
     */
    val isZooming: Boolean
        get() = scaleDragHelper.isZooming

    /* -----------配置----------- */

    /**
     * 添加 [Matrix] 变化监听器
     */
    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        (onMatrixChangeListenerList ?: ArrayList<OnMatrixChangeListener>(1).apply {
            this@ImageZoomer.onMatrixChangeListenerList = this
        }).add(listener)
    }

    /**
     * 移除 [Matrix] 变化监听器
     */
    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        return onMatrixChangeListenerList?.remove(listener) == true
    }

    /**
     * 设置旋转监听器
     */
    fun setOnRotateChangeListener(onRotateChangeListener: OnRotateChangeListener?) {
        this.onRotateChangeListener = onRotateChangeListener
    }

    fun getZoomScales(): ZoomScales {
        return zoomScales
    }

    fun setZoomScales(zoomScales: ZoomScales?) {
        if (zoomScales != null) {
            this.zoomScales = zoomScales
        } else {
            this.zoomScales = AdaptiveTwoLevelScales(this)
        }
        reset("setZoomScales")
    }

    fun getBlockByDrawablePoint(drawableX: Int, drawableY: Int): Block? {
        return blockDisplayer.getBlockByDrawablePoint(drawableX, drawableY)
    }

    fun getBlockByImagePoint(imageX: Int, imageY: Int): Block? {
        return blockDisplayer.getBlockByImagePoint(imageX, imageY)
    }

    /**
     * view 的触摸点转换成 drawable 上对应的点
     */
    fun touchPointToDrawablePoint(touchX: Int, touchY: Int): Point? {
        val drawRect = RectF()
        getDrawRect(drawRect)
        return if (drawRect.contains(touchX.toFloat(), touchY.toFloat())) {
            val zoomScale = zoomScale
            val drawableX = ((abs(drawRect.left) + touchX) / zoomScale).toInt()
            val drawableY = ((abs(drawRect.top) + touchY) / zoomScale).toInt()
            Point(drawableX, drawableY)
        } else {
            null
        }
    }

    /**
     * 飞速拖拽监听器
     */
    fun interface OnDragFlingListener {
        fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float)
    }

    /**
     * 单击监听器
     */
    fun interface OnViewTapListener {
        fun onViewTap(view: View, x: Float, y: Float)
    }

    /**
     * [Matrix] 变化监听器
     */
    fun interface OnMatrixChangeListener {
        fun onMatrixChanged(imageZoomer: ImageZoomer)
    }

    /**
     * 缩放监听器
     */
    fun interface OnScaleChangeListener {
        fun onScaleChanged(scaleFactor: Float, focusX: Float, focusY: Float)
    }

    /**
     * 长按监听器
     */
    fun interface OnViewLongPressListener {
        fun onViewLongPress(view: View, x: Float, y: Float)
    }

    /**
     * 旋转监听器
     */
    fun interface OnRotateChangeListener {
        fun onRotateChanged(imageZoomer: ImageZoomer)
    }
}