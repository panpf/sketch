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
import androidx.exifinterface.media.ExifInterface
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
import com.github.panpf.sketch.zoom.internal.Edge
import com.github.panpf.sketch.zoom.internal.ScalesFactoryImpl
import com.github.panpf.sketch.zoom.internal.getLifecycle
import com.github.panpf.sketch.zoom.internal.isAttachedToWindowCompat
import com.github.panpf.sketch.zoom.internal.sizeWithoutPaddingOrNull
import com.github.panpf.sketch.zoom.tile.OnTileChangedListener
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.Tiles
import com.github.panpf.sketch.zoom.tile.internal.shouldUseTiles
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

    private var zoomer: Zoomer? = null
    private var tiles: Tiles? = null
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            ON_START -> {
                tiles?.paused = true
            }
            ON_STOP -> {
                tiles?.paused = false
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
    private var lastPostResetTilesJob: Job? = null

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
            val oldZoomer = zoomer
            if (oldZoomer != null) {
                field?.container?.superSetScaleType(oldZoomer.scaleType)
                oldZoomer.recycle()
            }

            field = value
            val newZoomer = if (value != null) newZoomer(value) else null
            zoomer = newZoomer
            if (newZoomer != null) {
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
            zoomer?.scrollBarEnabled = value
        }
    var readModeEnabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                zoomer?.readModeDecider =
                    if (value) readModeDecider ?: longImageReadMode() else null
            }
        }
    var readModeDecider: ReadModeDecider? = null
        set(value) {
            if (field != value) {
                field = value
                zoomer?.readModeDecider = value
            }
        }
    var scalesFactory: ScalesFactory? = null
        set(value) {
            if (field != value) {
                field = value
                zoomer?.scalesFactory = value ?: ScalesFactoryImpl()
            }
        }
    var zoomAnimationDuration: Int = 200
        set(value) {
            if (value > 0 && field != value) {
                field = value
                zoomer?.zoomAnimationDuration = value
            }
        }
    var zoomInterpolator: Interpolator = AccelerateDecelerateInterpolator()
        set(value) {
            if (field != value) {
                field = value
                zoomer?.zoomInterpolator = value
            }
        }
    var allowParentInterceptOnEdge: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                zoomer?.allowParentInterceptOnEdge = value
            }
        }
    var onViewLongPressListener: OnViewLongPressListener? = null
        set(value) {
            if (field != value) {
                field = value
                zoomer?.onViewLongPressListener = value
            }
        }
    var onViewTapListener: OnViewTapListener? = null
        set(value) {
            if (field != value) {
                field = value
                zoomer?.onViewTapListener = value
            }
        }
    var showTileBounds: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                tiles?.showTileBounds = value
            }
        }

    init {
        addOnMatrixChangeListener { zoomer ->
            host?.container?.superSetImageMatrix(imageMatrix.apply { zoomer.getDrawMatrix(this) })
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
        zoomer?.location(x, y, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     *
     * @param focalX  Scale the x coordinate of the center point on the preview image
     * @param focalY  Scale the y coordinate of the center point on the preview image
     */
    fun scale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        zoomer?.scale(scale, focalX, focalY, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     */
    fun scale(scale: Float, animate: Boolean = false) {
        zoomer?.scale(scale, animate)
    }

    /**
     * Rotate the image to the specified degrees
     *
     * @param degrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateTo(degrees: Int) {
        zoomer?.rotateTo(degrees)
    }

    /**
     * Rotate an degrees based on the current rotation degrees
     *
     * @param addDegrees Rotation degrees, can only be 90°, 180°, 270°, 360°
     */
    fun rotateBy(addDegrees: Int) {
        zoomer?.rotateBy(addDegrees)
    }


    /***************************************** Information ****************************************/

    val rotateDegrees: Int
        get() = zoomer?.rotateDegrees ?: 0

    fun canScrollHorizontally(direction: Int): Boolean =
        zoomer?.canScrollHorizontally(direction) == true

    fun canScrollVertically(direction: Int): Boolean =
        zoomer?.canScrollVertically(direction) == true

    val horScrollEdge: Edge
        get() = zoomer?.horScrollEdge ?: Edge.NONE

    val verScrollEdge: Edge
        get() = zoomer?.verScrollEdge ?: Edge.NONE

    val scale: Float
        get() = zoomer?.scale ?: 1f

    val baseScale: Float
        get() = zoomer?.baseScale ?: 1f

    val supportScale: Float
        get() = zoomer?.supportScale ?: 1f

    /** Zoom ratio that makes the image fully visible */
    val fullScale: Float
        get() = zoomer?.fullScale ?: 1f

    /** Gets the zoom that fills the image with the ImageView display */
    val fillScale: Float
        get() = zoomer?.fillScale ?: 1f

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originScale: Float
        get() = zoomer?.originScale ?: 1f

    val minScale: Float
        get() = zoomer?.minScale ?: 1f

    val maxScale: Float
        get() = zoomer?.maxScale ?: 1f

    val stepScales: FloatArray?
        get() = zoomer?.stepScales

    val isScaling: Boolean
        get() = zoomer?.isScaling == true

    val tileList: List<Tile>?
        get() = tiles?.tileList

    val imageSize: Size?
        get() = zoomer?.imageSize ?: tiles?.imageSize

    val previewSize: Size?
        get() = zoomer?.drawableSize

    fun getDrawMatrix(matrix: Matrix) = zoomer?.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = zoomer?.getDrawRect(rectF)

    /** Gets the area that the user can see on the preview (not affected by rotation) */
    fun getVisibleRect(rect: Rect) = zoomer?.getVisibleRect(rect)

    fun touchPointToDrawablePoint(touchPoint: PointF): Point? {
        return zoomer?.touchPointToDrawablePoint(touchPoint)
    }

    fun eachTileList(action: (tile: Tile, load: Boolean) -> Unit) {
        tiles?.eachTileList(action)
    }

    fun addOnMatrixChangeListener(listener: OnMatrixChangeListener) {
        this.onMatrixChangeListenerList = (onMatrixChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomer?.addOnMatrixChangeListener(listener)
    }

    fun removeOnMatrixChangeListener(listener: OnMatrixChangeListener): Boolean {
        zoomer?.removeOnMatrixChangeListener(listener)
        return onMatrixChangeListenerList?.remove(listener) == true
    }

    fun addOnRotateChangeListener(listener: OnRotateChangeListener) {
        this.onRotateChangeListenerList = (onRotateChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomer?.addOnRotateChangeListener(listener)
    }

    fun removeOnRotateChangeListener(listener: OnRotateChangeListener): Boolean {
        zoomer?.removeOnRotateChangeListener(listener)
        return onRotateChangeListenerList?.remove(listener) == true
    }

    fun addOnDragFlingListener(listener: OnDragFlingListener) {
        this.onDragFlingListenerList = (onDragFlingListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomer?.addOnDragFlingListener(listener)
    }

    fun removeOnDragFlingListener(listener: OnDragFlingListener): Boolean {
        zoomer?.removeOnDragFlingListener(listener)
        return onDragFlingListenerList?.remove(listener) == true
    }

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        this.onScaleChangeListenerList = (onScaleChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomer?.addOnScaleChangeListener(listener)
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        zoomer?.removeOnScaleChangeListener(listener)
        return onScaleChangeListenerList?.remove(listener) == true
    }

    fun addOnViewDragListener(listener: OnViewDragListener) {
        this.onOnViewDragListenerList = (onOnViewDragListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        zoomer?.addOnViewDragListener(listener)
    }

    fun removeOnViewDragListener(listener: OnViewDragListener): Boolean {
        zoomer?.removeOnViewDragListener(listener)
        return onOnViewDragListenerList?.remove(listener) == true
    }

    fun addOnTileChangedListener(listener: OnTileChangedListener) {
        this.onTileChangedListenerList = (onTileChangedListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
        tiles?.addOnTileChangedListener(listener)
    }

    fun removeOnTileChangedListener(listener: OnTileChangedListener): Boolean {
        tiles?.removeOnTileChangedListener(listener)
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
            tiles?.paused = lifecycle?.currentState?.isAtLeast(STARTED) == false
        }
    }

    private fun unregisterLifecycleObserver() {
        lifecycle?.removeObserver(lifecycleObserver)
        tiles?.paused = false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        val host = host ?: return
        val view = host.view
        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        zoomer?.viewSize = Size(viewWidth, viewHeight)
        postResetTiles()
    }

    private fun postResetTiles() {
        // Triggering the reset tiles frequently (such as changing the view size in shared element animations)
        // can cause large fluctuations in memory, so delayed resets can avoid this problem
        lastPostResetTilesJob?.cancel()
        lastPostResetTilesJob = scope.launch(Dispatchers.Main) {
            delay(60)
            tiles?.destroy()
            tiles = newTiles(zoomer)?.apply {
                showTileBounds = this@ZoomAbility.showTileBounds
                paused = this@ZoomAbility.lifecycle?.currentState?.isAtLeast(STARTED) == false
            }
        }
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        tiles?.onDraw(canvas)
        zoomer?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
        zoomer?.onTouchEvent(event) ?: false

    override fun setScaleType(scaleType: ScaleType): Boolean {
        val zoomer = zoomer
        zoomer?.scaleType = scaleType
        return zoomer != null
    }

    override fun getScaleType(): ScaleType? = zoomer?.scaleType

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        tiles?.paused = visibility != View.VISIBLE
    }

    private fun initialize() {
        setZoomerDrawable()
        postResetTiles()
    }

    private fun destroy() {
        zoomer?.recycle()
        tiles?.destroy()
        tiles = null
    }

    private fun newZoomer(host: Host): Zoomer {
        val scaleType = host.container.superGetScaleType()
        require(scaleType != ScaleType.MATRIX) {
            "ScaleType cannot be MATRIX"
        }
        return Zoomer(
            context = host.context,
            sketch = host.context.sketch,
            view = host.view,
            scaleType = scaleType,
        ).apply {
            readModeDecider = if (readModeEnabled) readModeDecider else null
            scrollBarEnabled = this@ZoomAbility.scrollBarEnabled
            zoomAnimationDuration = this@ZoomAbility.zoomAnimationDuration
            zoomInterpolator = this@ZoomAbility.zoomInterpolator
            allowParentInterceptOnEdge = this@ZoomAbility.allowParentInterceptOnEdge
            onViewLongPressListener = this@ZoomAbility.onViewLongPressListener
            onViewTapListener = this@ZoomAbility.onViewTapListener
            onMatrixChangeListenerList?.forEach {
                addOnMatrixChangeListener(it)
            }
            onScaleChangeListenerList?.forEach {
                addOnScaleChangeListener(it)
            }
            onRotateChangeListenerList?.forEach {
                addOnRotateChangeListener(it)
            }
            onDragFlingListenerList?.forEach {
                addOnDragFlingListener(it)
            }
            this@ZoomAbility.scalesFactory?.let {
                this@apply.scalesFactory = it
            }
        }
    }

    private fun setZoomerDrawable() {
        val host = host ?: return
        val zoomer = zoomer ?: return
        val previewDrawable = host.container.getDrawable()
        zoomer.drawableSize =
            Size(previewDrawable?.intrinsicWidth ?: 0, previewDrawable?.intrinsicHeight ?: 0)
        val sketchDrawable = previewDrawable?.findLastSketchDrawable()
        zoomer.imageSize =
            Size(sketchDrawable?.imageInfo?.width ?: 0, sketchDrawable?.imageInfo?.height ?: 0)
    }

    private fun newTiles(zoomer: Zoomer?): Tiles? {
        zoomer ?: return null
        val host = host ?: return null
        val sketch = host.context.sketch
        val logger = sketch.logger
        val viewSize = host.view.sizeWithoutPaddingOrNull
        if (viewSize == null) {
            logger.d(MODULE) { "Can't use Tiles. View size error" }
            return null
        }
        val previewDrawable = host.container.getDrawable()
        val sketchDrawable = previewDrawable?.findLastSketchDrawable()?.takeIf { it !is Animatable }
        if (sketchDrawable == null) {
            logger.d(MODULE) { "Can't use Tiles. Drawable error" }
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
                "Don't need to use Tiles. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (!shouldUseTiles(imageWidth, imageHeight, previewWidth, previewHeight)) {
            logger.d(MODULE) {
                "Can't use Tiles. previewSize error. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.parseMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
            logger.d(MODULE) {
                "MimeType does not support Tiles. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }

        logger.d(MODULE) {
            "Use Tiles. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
        }
        val exifOrientation: Int = sketchDrawable.imageInfo.exifOrientation
        val imageUri = sketchDrawable.imageUri
        return Tiles(
            context = host.context,
            sketch = sketch,
            zoomer = zoomer,
            imageUri = imageUri,
            viewSize = viewSize,
            disabledExifOrientation = exifOrientation == ExifInterface.ORIENTATION_UNDEFINED
        ).apply {
            this@ZoomAbility.onTileChangedListenerList?.forEach {
                addOnTileChangedListener(it)
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