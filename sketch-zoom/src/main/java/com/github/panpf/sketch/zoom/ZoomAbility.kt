package com.github.panpf.sketch.zoom

import android.graphics.Canvas
import android.graphics.Matrix
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
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.util.getLifecycle
import com.github.panpf.sketch.util.isAttachedToWindowCompat
import com.github.panpf.sketch.viewability.AttachObserver
import com.github.panpf.sketch.viewability.DrawObserver
import com.github.panpf.sketch.viewability.DrawableObserver
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ScaleTypeObserver
import com.github.panpf.sketch.viewability.SizeChangeObserver
import com.github.panpf.sketch.viewability.TouchEventObserver
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.VisibilityChangedObserver
import com.github.panpf.sketch.zoom.internal.ScaleDragHelper
import com.github.panpf.sketch.zoom.internal.ScalesFactoryImpl
import com.github.panpf.sketch.zoom.tile.OnTileChangedListener
import com.github.panpf.sketch.zoom.tile.Tile
import com.github.panpf.sketch.zoom.tile.Tiles

class ZoomAbility : ViewAbility, AttachObserver, ScaleTypeObserver, DrawObserver,
    DrawableObserver, TouchEventObserver, SizeChangeObserver, VisibilityChangedObserver {

    companion object {
        private const val MODULE = "ZoomAbility"
    }

    private var zoomer: Zoomer? = null
    private var tiles: Tiles? = null
    private val tilesPauseLifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            ON_PAUSE -> {
                tiles?.paused = true
            }
            ON_RESUME -> {
                tiles?.paused = false
            }
            else -> {}
        }
    }
    private var onMatrixChangeListenerList: MutableSet<OnMatrixChangeListener>? = null
    private var onRotateChangeListenerList: MutableSet<OnRotateChangeListener>? = null
    private var onDragFlingListenerList: MutableSet<OnDragFlingListener>? = null
    private var onScaleChangeListenerList: MutableSet<OnScaleChangeListener>? = null
    private var onTileChangedListenerList: MutableSet<OnTileChangedListener>? = null
    private val imageMatrix = Matrix()

    var lifecycle: Lifecycle? = null
        set(value) {
            if (value != field) {
                unregisterTilesPauseLifecycleObserver()
                field = value ?: host?.context.getLifecycle()
                registerTilesPauseLifecycleObserver()
            }
        }

    override var host: Host? = null
        set(value) {
            val oldZoomer = zoomer
            if (oldZoomer != null) {
                field?.superScaleType = oldZoomer.scaleType
                oldZoomer.recycle()
            }

            field = value
            val newZoomer = if (value != null) newZoomer(value) else null
            zoomer = newZoomer
            if (newZoomer != null) {
                field?.superScaleType = ScaleType.MATRIX
            }

            lifecycle = value?.context.getLifecycle()
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
                    if (value) readModeDecider ?: DefaultReadModeDecider() else null
            }
        }
    var readModeDecider: ReadModeDecider? = null
        set(value) {
            if (field != value) {
                field = value
                if (readModeEnabled) {
                    zoomer?.readModeDecider = value
                }
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
            host?.imageMatrix = imageMatrix.apply { zoomer.getDrawMatrix(this) }
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
    fun zoom(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        zoomer?.zoom(scale, focalX, focalY, animate)
    }

    /**
     * Scale to the specified scale. You don't have to worry about rotation degrees
     */
    fun zoom(scale: Float, animate: Boolean = false) {
        zoomer?.zoom(scale, animate)
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

    val horScrollEdge: Int
        get() = zoomer?.horScrollEdge ?: ScaleDragHelper.EDGE_NONE

    val verScrollEdge: Int
        get() = zoomer?.verScrollEdge ?: ScaleDragHelper.EDGE_NONE

    val zoomScale: Float
        get() = zoomer?.zoomScale ?: 1f

    val baseZoomScale: Float
        get() = zoomer?.baseZoomScale ?: 1f

    val supportZoomScale: Float
        get() = zoomer?.supportZoomScale ?: 1f

    /** Zoom ratio that makes the image fully visible */
    val fullZoomScale: Float
        get() = zoomer?.fullZoomScale ?: 1f

    /** Gets the zoom that fills the image with the ImageView display */
    val fillZoomScale: Float
        get() = zoomer?.fillZoomScale ?: 1f

    /** Gets the scale that allows the image to be displayed at scale to scale */
    val originZoomScale: Float
        get() = zoomer?.originZoomScale ?: 1f

    val minZoomScale: Float
        get() = zoomer?.minZoomScale ?: 1f

    val maxZoomScale: Float
        get() = zoomer?.maxZoomScale ?: 1f

    val doubleClickZoomScales: FloatArray?
        get() = zoomer?.doubleClickZoomScales

    val isZooming: Boolean
        get() = zoomer?.isZooming == true

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
        registerTilesPauseLifecycleObserver()
    }

    override fun onDetachedFromWindow() {
        destroy()
        unregisterTilesPauseLifecycleObserver()
    }

    private fun registerTilesPauseLifecycleObserver() {
        if (host?.view?.isAttachedToWindowCompat == true) {
            this.lifecycle?.addObserver(tilesPauseLifecycleEventObserver)
            tiles?.paused = this.lifecycle?.currentState?.isAtLeast(STARTED) == false
        }
    }

    private fun unregisterTilesPauseLifecycleObserver() {
        this.lifecycle?.removeObserver(tilesPauseLifecycleEventObserver)
        tiles?.paused = false
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        val host = host ?: return
        val view = host.view
        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        zoomer?.viewSize = Size(viewWidth, viewHeight)
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
        tiles?.destroy()
        tiles = tryNewTiles(zoomer)?.apply {
            showTileBounds = this@ZoomAbility.showTileBounds
            paused = this@ZoomAbility.lifecycle?.currentState?.isAtLeast(STARTED) == false
        }
    }

    private fun destroy() {
        zoomer?.recycle()
        tiles?.destroy()
        tiles = null
    }

    private fun newZoomer(host: Host): Zoomer {
        val scaleType = host.superScaleType
        require(scaleType != ScaleType.MATRIX) {
            "ScaleType cannot be MATRIX"
        }
        return Zoomer(
            host.context,
            view = host.view,
            scaleType = scaleType,
            readModeDecider = if (readModeEnabled) readModeDecider else null,
        ).apply {
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
        val previewDrawable = host.drawable?.getLastDrawable()
        zoomer.drawableSize =
            Size(previewDrawable?.intrinsicWidth ?: 0, previewDrawable?.intrinsicHeight ?: 0)
        if (previewDrawable is SketchDrawable) {
            zoomer.imageSize =
                Size(previewDrawable.imageInfo.width, previewDrawable.imageInfo.height)
        }
    }

    private fun tryNewTiles(zoomer: Zoomer?): Tiles? {
        zoomer ?: return null
        val host = host ?: return null
        val logger = host.context.sketch.logger
        val view = host.view

        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        if (viewWidth <= 0 || viewHeight <= 0) {
            logger.d(MODULE) { "View size error" }
            return null
        }
        val viewSize = Size(viewWidth, viewHeight)

        val previewDrawable = host.drawable?.getLastDrawable()
        if (previewDrawable !is SketchDrawable || previewDrawable is Animatable) {
            logger.d(MODULE) { "Can't use Tiles" }
            return null
        }

        val previewWidth = previewDrawable.bitmapInfo.width
        val previewHeight = previewDrawable.bitmapInfo.height
        val imageWidth = previewDrawable.imageInfo.width
        val imageHeight = previewDrawable.imageInfo.height
        val mimeType = previewDrawable.imageInfo.mimeType
        val key = previewDrawable.requestKey

        if (previewWidth >= imageWidth && previewHeight >= imageHeight) {
            logger.d(MODULE) {
                "Don't need to use Tiles. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.valueOfMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
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
        val exifOrientation: Int = previewDrawable.imageExifOrientation
        val imageUri = previewDrawable.requestUri
        return Tiles(
            context = host.context,
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
}