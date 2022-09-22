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

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView.ScaleType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.isSketchGlobalLifecycle
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.findLastSketchDrawable
import com.github.panpf.sketch.viewability.AttachObserver
import com.github.panpf.sketch.viewability.DrawObserver
import com.github.panpf.sketch.viewability.DrawableObserver
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ImageMatrixObserver
import com.github.panpf.sketch.viewability.RequestListenerObserver
import com.github.panpf.sketch.viewability.ScaleTypeObserver
import com.github.panpf.sketch.viewability.SizeChangeObserver
import com.github.panpf.sketch.viewability.TouchEventObserver
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.VisibilityChangedObserver
import com.github.panpf.sketch.zoom.internal.canUseSubsampling
import com.github.panpf.sketch.zoom.internal.contentSize
import com.github.panpf.sketch.zoom.internal.getLifecycle
import com.github.panpf.sketch.zoom.internal.isAttachedToWindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ZoomAbility : ViewAbility, AttachObserver, ScaleTypeObserver, DrawObserver,
    DrawableObserver, TouchEventObserver, SizeChangeObserver, VisibilityChangedObserver,
    RequestListenerObserver, ImageMatrixObserver {

    companion object {
        private const val MODULE = "ZoomAbility"
    }

    private var zoomerHelper: ZoomerHelper? = null
    private var subsamplingHelper: SubsamplingHelper? = null
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            ON_START -> {
                subsamplingHelper?.paused = true
            }
            ON_STOP -> {
                subsamplingHelper?.paused = false
            }
            else -> {}
        }
    }
    private var onMatrixChangeListenerList: MutableSet<OnMatrixChangeListener>? = null
    private var onRotateChangeListenerList: MutableSet<OnRotateChangeListener>? = null
    private var onDragFlingListenerList: MutableSet<OnDragFlingListener>? = null
    private var onScaleChangeListenerList: MutableSet<OnScaleChangeListener>? = null
    private var onOnViewDragListenerList: MutableSet<OnViewDragListener>? = null
    private var onTileChangedListenerList: MutableSet<OnTileChangedListener>? = null
    private val imageMatrix = Matrix()
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    private var lastPostResetSubsamplingHelperJob: Job? = null

    private var lifecycle: Lifecycle? = null
        set(value) {
            if (value != field) {
                unregisterLifecycleObserver()
                field = value
                registerLifecycleObserver()
            }
        }

    override var host: Host? = null
        set(value) {
            val oldZoomerHelper = zoomerHelper
            if (oldZoomerHelper != null) {
                oldZoomerHelper.clean()
                field?.container?.superSetScaleType(oldZoomerHelper.scaleType)
            }

            field = value
            val newZoomerHelper = if (value != null) newZoomerHelper(value) else null
            zoomerHelper = newZoomerHelper
            resetDrawableSize()
            if (newZoomerHelper != null) {
                field?.container?.superSetScaleType(ScaleType.MATRIX)
            }

            lifecycle = value?.context.getLifecycle()
            if (value == null) {
                scope.cancel()
            }
        }

    var scrollBarEnabled: Boolean = true
        set(value) {
            field = value
            zoomerHelper?.scrollBarEnabled = value
        }
    var readModeEnabled: Boolean = false
        set(value) {
            field = value
            zoomerHelper?.readModeEnabled = value
        }
    var readModeDecider: ReadModeDecider? = null
        set(value) {
            field = value
            zoomerHelper?.readModeDecider = value
        }
    var scaleStateFactory: ScaleState.Factory? = null
        set(value) {
            if (field != value) {
                field = value
                zoomerHelper?.scaleStateFactory = value ?: DefaultScaleStateFactory()
            }
        }
    var zoomAnimationDuration: Int = 200
        set(value) {
            if (value > 0) {
                field = value
                zoomerHelper?.zoomAnimationDuration = value
            }
        }
    var zoomInterpolator: Interpolator? = null
        set(value) {
            if (field != value) {
                field = value
                zoomerHelper?.zoomInterpolator = value ?: AccelerateDecelerateInterpolator()
            }
        }
    var allowParentInterceptOnEdge: Boolean = true
        set(value) {
            field = value
            zoomerHelper?.allowParentInterceptOnEdge = value
        }
    var onViewLongPressListener: OnViewLongPressListener? = null
        set(value) {
            field = value
            zoomerHelper?.onViewLongPressListener = value
        }
    var onViewTapListener: OnViewTapListener? = null
        set(value) {
            field = value
            zoomerHelper?.onViewTapListener = value
        }
    var showTileBounds: Boolean = false
        set(value) {
            field = value
            subsamplingHelper?.showTileBounds = value
        }

    init {
        addOnMatrixChangeListener {
            val container = host?.container
            val zoomer = zoomerHelper
            if (container != null && zoomer != null) {
                val matrix = imageMatrix.apply { zoomer.getDrawMatrix(this) }
                container.superSetImageMatrix(matrix)
            }
        }
    }


    /*************************************** Interaction ******************************************/

    /**
     * Locate to the location specified on the preview image. You don't have to worry about scaling and rotation
     *
     * @param x Preview the x coordinate on the diagram
     * @param y Preview the y-coordinate on the diagram
     */
    fun location(x: Float, y: Float, animate: Boolean = false) {
        zoomerHelper?.location(x, y, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     *
     * @param focalX  Scale the x coordinate of the center point on the preview image
     * @param focalY  Scale the y coordinate of the center point on the preview image
     */
    fun scale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        zoomerHelper?.scale(scale, focalX, focalY, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     */
    fun scale(scale: Float, animate: Boolean = false) {
        zoomerHelper?.scale(scale, animate)
    }

    /**
     * Rotate the image to the specified degrees
     *
     * @param degrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateTo(degrees: Int) {
        zoomerHelper?.rotateTo(degrees)
    }

    /**
     * Rotate an degrees based on the current rotation degrees
     *
     * @param addDegrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateBy(addDegrees: Int) {
        zoomerHelper?.rotateBy(addDegrees)
    }


    /***************************************** Information ****************************************/

    val rotateDegrees: Int
        get() = zoomerHelper?.rotateDegrees ?: 0

    fun canScrollHorizontally(direction: Int): Boolean =
        zoomerHelper?.canScrollHorizontally(direction) == true

    fun canScrollVertically(direction: Int): Boolean =
        zoomerHelper?.canScrollVertically(direction) == true

    val horScrollEdge: Edge
        get() = zoomerHelper?.horScrollEdge ?: Edge.NONE

    val verScrollEdge: Edge
        get() = zoomerHelper?.verScrollEdge ?: Edge.NONE

    val scale: Float
        get() = zoomerHelper?.scale ?: 1f

    val baseScale: Float
        get() = zoomerHelper?.baseScale ?: 1f

    val supportScale: Float
        get() = zoomerHelper?.supportScale ?: 1f

    /** Zoom ratio that makes the image fully visible */
    val fullScale: Float
        get() = zoomerHelper?.fullScale ?: 1f

    /** Gets the zoom that fills the image with the ImageView display */
    val fillScale: Float
        get() = zoomerHelper?.fillScale ?: 1f

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originScale: Float
        get() = zoomerHelper?.originScale ?: 1f

    val minScale: Float
        get() = zoomerHelper?.minScale ?: 1f

    val maxScale: Float
        get() = zoomerHelper?.maxScale ?: 1f

    val stepScales: FloatArray?
        get() = zoomerHelper?.stepScales

    val isScaling: Boolean
        get() = zoomerHelper?.isScaling == true

    val tileList: List<Tile>?
        get() = subsamplingHelper?.tileList

    val imageSize: Size?
        get() = zoomerHelper?.imageSize

    val previewSize: Size?
        get() = zoomerHelper?.drawableSize

    fun getDrawMatrix(matrix: Matrix) = zoomerHelper?.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = zoomerHelper?.getDrawRect(rectF)

    /** Gets the area that the user can see on the preview (not affected by rotation) */
    fun getVisibleRect(rect: Rect) = zoomerHelper?.getVisibleRect(rect)

    fun touchPointToDrawablePoint(touchPoint: PointF): Point? {
        return zoomerHelper?.touchPointToDrawablePoint(touchPoint)
    }

    fun eachTileList(action: (tile: Tile, load: Boolean) -> Unit) {
        subsamplingHelper?.eachTileList(action)
    }

    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        this.onMatrixChangeListenerList = (onMatrixChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomerHelper?.addOnMatrixChangeListener(listener)
    }

    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        zoomerHelper?.removeOnMatrixChangeListener(listener)
        return onMatrixChangeListenerList?.remove(listener) == true
    }

    fun addOnRotateChangeListener(listener: OnRotateChangeListener) {
        this.onRotateChangeListenerList = (onRotateChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomerHelper?.addOnRotateChangeListener(listener)
    }

    fun removeOnRotateChangeListener(listener: OnRotateChangeListener): Boolean {
        zoomerHelper?.removeOnRotateChangeListener(listener)
        return onRotateChangeListenerList?.remove(listener) == true
    }

    fun addOnDragFlingListener(listener: OnDragFlingListener) {
        this.onDragFlingListenerList = (onDragFlingListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomerHelper?.addOnDragFlingListener(listener)
    }

    fun removeOnDragFlingListener(listener: OnDragFlingListener): Boolean {
        zoomerHelper?.removeOnDragFlingListener(listener)
        return onDragFlingListenerList?.remove(listener) == true
    }

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        this.onScaleChangeListenerList = (onScaleChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomerHelper?.addOnScaleChangeListener(listener)
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        zoomerHelper?.removeOnScaleChangeListener(listener)
        return onScaleChangeListenerList?.remove(listener) == true
    }

    fun addOnViewDragListener(listener: OnViewDragListener) {
        this.onOnViewDragListenerList = (onOnViewDragListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomerHelper?.addOnViewDragListener(listener)
    }

    fun removeOnViewDragListener(listener: OnViewDragListener): Boolean {
        zoomerHelper?.removeOnViewDragListener(listener)
        return onOnViewDragListenerList?.remove(listener) == true
    }

    fun addOnTileChangedListener(listener: OnTileChangedListener) {
        this.onTileChangedListenerList = (onTileChangedListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        subsamplingHelper?.addOnTileChangedListener(listener)
    }

    fun removeOnTileChangedListener(listener: OnTileChangedListener): Boolean {
        subsamplingHelper?.removeOnTileChangedListener(listener)
        return onTileChangedListenerList?.remove(listener) == true
    }


    /**************************************** Internal ********************************************/

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        val host = host ?: return
        destroy()
        if (host.view.isAttachedToWindowCompat) {
            initialize()
        }
    }

    override fun onAttachedToWindow() {
        initialize()
        registerLifecycleObserver()
    }

    override fun onDetachedFromWindow() {
        destroy()
        unregisterLifecycleObserver()
    }

    private fun registerLifecycleObserver() {
        if (host?.view?.isAttachedToWindowCompat == true) {
            lifecycle?.addObserver(lifecycleObserver)
            subsamplingHelper?.paused = lifecycle?.currentState?.isAtLeast(STARTED) == false
        }
    }

    private fun unregisterLifecycleObserver() {
        lifecycle?.removeObserver(lifecycleObserver)
        subsamplingHelper?.paused = false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        val host = host ?: return
        val view = host.view
        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        zoomerHelper?.viewSize = Size(viewWidth, viewHeight)
        postDelayResetSubsamplingHelper()
    }

    private fun postDelayResetSubsamplingHelper() {
        // Triggering the reset SubsamplingHelper frequently (such as changing the view size in shared element animations)
        // can cause large fluctuations in memory, so delayed resets can avoid this problem
        lastPostResetSubsamplingHelperJob?.cancel()
        lastPostResetSubsamplingHelperJob = scope.launch(Dispatchers.Main) {
            delay(60)
            subsamplingHelper?.destroy()
            subsamplingHelper = newSubsamplingHelper(zoomerHelper)
        }
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        subsamplingHelper?.onDraw(canvas)
        zoomerHelper?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
        zoomerHelper?.onTouchEvent(event) ?: false

    override fun setScaleType(scaleType: ScaleType): Boolean {
        val zoomerHelper = zoomerHelper
        zoomerHelper?.scaleType = scaleType
        return zoomerHelper != null
    }

    override fun getScaleType(): ScaleType? = zoomerHelper?.scaleType

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        subsamplingHelper?.paused = visibility != View.VISIBLE
    }

    private fun initialize() {
        resetDrawableSize()
        postDelayResetSubsamplingHelper()
    }

    private fun destroy() {
        zoomerHelper?.clean()
        subsamplingHelper?.destroy()
        subsamplingHelper = null
    }

    private fun newZoomerHelper(host: Host): ZoomerHelper {
        val scaleType = host.container.superGetScaleType()
        require(scaleType != ScaleType.MATRIX) {
            "ScaleType cannot be MATRIX"
        }
        return ZoomerHelper(
            context = host.context,
            logger = host.context.sketch.logger,
            view = host.view,
            scaleType = scaleType,
        ).apply {
            this@apply.readModeEnabled = this@ZoomAbility.readModeEnabled
            this@apply.readModeDecider = this@ZoomAbility.readModeDecider
            this@apply.scrollBarEnabled = this@ZoomAbility.scrollBarEnabled
            this@apply.zoomAnimationDuration = this@ZoomAbility.zoomAnimationDuration
            this@apply.allowParentInterceptOnEdge = this@ZoomAbility.allowParentInterceptOnEdge
            this@apply.onViewLongPressListener = this@ZoomAbility.onViewLongPressListener
            this@apply.onViewTapListener = this@ZoomAbility.onViewTapListener
            this@ZoomAbility.zoomInterpolator?.let {
                this@apply.zoomInterpolator = it
            }
            this@ZoomAbility.scaleStateFactory?.let {
                this@apply.scaleStateFactory = it
            }
            this@ZoomAbility.onMatrixChangeListenerList?.forEach {
                this@apply.addOnMatrixChangeListener(it)
            }
            this@ZoomAbility.onScaleChangeListenerList?.forEach {
                this@apply.addOnScaleChangeListener(it)
            }
            this@ZoomAbility.onRotateChangeListenerList?.forEach {
                this@apply.addOnRotateChangeListener(it)
            }
            this@ZoomAbility.onDragFlingListenerList?.forEach {
                this@apply.addOnDragFlingListener(it)
            }
        }
    }

    private fun resetDrawableSize() {
        val host = host ?: return
        val zoomerHelper = zoomerHelper ?: return
        val previewDrawable = host.container.getDrawable()
        zoomerHelper.drawableSize =
            Size(previewDrawable?.intrinsicWidth ?: 0, previewDrawable?.intrinsicHeight ?: 0)
        val sketchDrawable = previewDrawable?.findLastSketchDrawable()
        zoomerHelper.imageSize =
            Size(sketchDrawable?.imageInfo?.width ?: 0, sketchDrawable?.imageInfo?.height ?: 0)
    }

    private fun newSubsamplingHelper(zoomerHelper: ZoomerHelper?): SubsamplingHelper? {
        zoomerHelper ?: return null
        val host = host ?: return null
        val sketch = host.context.sketch
        val logger = sketch.logger
        val viewContentSize = host.view.contentSize
        if (viewContentSize == null) {
            logger.d(MODULE) { "Can't use Subsampling. View size error" }
            return null
        }
        val previewDrawable = host.container.getDrawable()
        val sketchDrawable = previewDrawable?.findLastSketchDrawable()?.takeIf { it !is Animatable }
        if (sketchDrawable == null) {
            logger.d(MODULE) { "Can't use Subsampling. Drawable error" }
            return null
        }

        val previewWidth = sketchDrawable.bitmapInfo.width
        val previewHeight = sketchDrawable.bitmapInfo.height
        val imageWidth = sketchDrawable.imageInfo.width
        val imageHeight = sketchDrawable.imageInfo.height
        val mimeType = sketchDrawable.imageInfo.mimeType
        val key = sketchDrawable.requestKey

        if (previewWidth >= imageWidth && previewHeight >= imageHeight) {
            logger.d(MODULE) {
                "Don't need to use Subsampling. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (!canUseSubsampling(imageWidth, imageHeight, previewWidth, previewHeight)) {
            logger.d(MODULE) {
                "Can't use Subsampling. previewSize error. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.parseMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
            logger.d(MODULE) {
                "MimeType does not support Subsampling. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }

        logger.d(MODULE) {
            "Use Subsampling. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
        }
        return SubsamplingHelper(
            context = host.context,
            sketch = sketch,
            zoomerHelper = zoomerHelper,
            imageUri = sketchDrawable.imageUri,
            imageInfo = sketchDrawable.imageInfo,
            viewSize = viewContentSize
        ).apply {
            this@apply.showTileBounds = this@ZoomAbility.showTileBounds
            this@apply.showTileBounds = this@ZoomAbility.showTileBounds
            this@apply.paused =
                this@ZoomAbility.lifecycle?.currentState?.isAtLeast(STARTED) == false
            this@ZoomAbility.onTileChangedListenerList?.forEach {
                this@apply.addOnTileChangedListener(it)
            }
        }
    }

    override fun onRequestStart(request: DisplayRequest) {
        lifecycle = request.lifecycle.takeIf { !it.isSketchGlobalLifecycle() }
            ?: host?.context.getLifecycle()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
    }

    override fun setImageMatrix(imageMatrix: Matrix?): Boolean {
        return true
    }

    override fun getImageMatrix(): Matrix? {
        return null
    }
}