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
import android.widget.ImageView.ScaleType.MATRIX
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
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
import com.github.panpf.sketch.zoom.block.Blocks
import com.github.panpf.sketch.zoom.internal.ScaleDragHelper

class ZoomAbility : ViewAbility, AttachObserver, ScaleTypeObserver, DrawObserver,
    DrawableObserver, TouchEventObserver, SizeChangeObserver, VisibilityChangedObserver {

    companion object {
        private const val MODULE = "ZoomViewAbility"
    }

    private var zoomer: Zoomer? = null

    private var blocks: Blocks? = null
    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            ON_PAUSE -> blocks?.setPause(true)
            ON_RESUME -> blocks?.setPause(false)
            else -> {}
        }
    }
    private var onMatrixChangeListenerList: MutableSet<OnMatrixChangeListener>? = null
    private var onRotateChangeListenerList: MutableSet<OnRotateChangeListener>? = null
    private var onDragFlingListenerList: MutableSet<OnDragFlingListener>? = null
    private var onScaleChangeListenerList: MutableSet<OnScaleChangeListener>? = null

    override var host: Host? = null

    init {
        addOnMatrixChangeListener {
            blocks?.onMatrixChanged()
        }
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        val host = host ?: return
        destroyZoomer()
        if (host.view.isAttachedToWindowCompat) {
            createZoomer()
        }
    }

    override fun onAttachedToWindow() {
        createZoomer()
        host?.context?.getLifecycle()?.addObserver(lifecycleEventObserver)
    }

    override fun onDetachedFromWindow() {
        destroyZoomer()
        host?.context?.getLifecycle()?.removeObserver(lifecycleEventObserver)
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
        blocks?.onDraw(canvas)
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
        blocks?.setPause(visibility != View.VISIBLE)
    }

    private fun createZoomer() {
        val host = host ?: return
        host.superScaleType = MATRIX
        val newZoomer = tryNewZoomer()?.apply {
            scrollBarEnabled = this@ZoomAbility.scrollBarEnabled
            zoomAnimationDuration = this@ZoomAbility.zoomAnimationDuration
            zoomInterpolator = this@ZoomAbility.zoomInterpolator
            allowParentInterceptOnEdge = this@ZoomAbility.allowParentInterceptOnEdge
            onViewLongPressListener = this@ZoomAbility.onViewLongPressListener
            onViewTapListener = this@ZoomAbility.onViewTapListener
            onMatrixChangeListenerList = this@ZoomAbility.onMatrixChangeListenerList
            onScaleChangeListenerList = this@ZoomAbility.onScaleChangeListenerList
            onRotateChangeListenerList = this@ZoomAbility.onRotateChangeListenerList
            onDragFlingListenerList = this@ZoomAbility.onDragFlingListenerList
        }
        this.zoomer = newZoomer
        this.blocks = newZoomer?.let { tryNewBlocks(it) }?.apply {
            isShowBlockBounds = this@ZoomAbility.showBlockBounds
        }
    }

    private fun destroyZoomer() {
        val host = host ?: return
        zoomer?.apply {
            recycle()
            host.superScaleType = scaleType
        }
        zoomer = null

        blocks?.recycle("destroyZoomer")
        blocks = null
    }

    private fun tryNewZoomer(): Zoomer? {
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
        if (previewDrawable !is SketchDrawable) {
            logger.d(MODULE) { "Can't use Blocks" }
            return null
        }
        val previewWidth = previewDrawable.intrinsicWidth
        val previewHeight = previewDrawable.intrinsicHeight
        val imageWidth = previewDrawable.imageInfo.width
        val imageHeight = previewDrawable.imageInfo.height
        val mimeType = previewDrawable.imageInfo.mimeType
        val key = previewDrawable.requestKey
        if (previewWidth <= 0 || previewHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            logger.d(MODULE) {
                "imageSize or previewSize error. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        val previewSize = Size(previewWidth, previewHeight)
        val imageSize = Size(imageWidth, imageHeight)

        logger.d(MODULE) {
            "Use Zoomer. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
        }

        val scaleType = host.superScaleType
        return Zoomer(
            host.context,
            view = host.view,
            viewSize = viewSize,
            imageSize = imageSize,
            drawableSize = previewSize,
            scaleType = scaleType,
            readModeDecider = if (readModeEnabled) readModeDecider else null,
            zoomScales = zoomScales,
        ) { matrix ->
            host.imageMatrix = matrix
        }
    }

    private fun tryNewBlocks(zoomer: Zoomer): Blocks? {
        val host = host ?: return null
        val logger = host.context.sketch.logger

        val previewDrawable = host.drawable?.getLastDrawable()
        if (previewDrawable !is SketchDrawable || previewDrawable is Animatable) {
            logger.d(MODULE) { "Can't use Blocks" }
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
                "Don't need to use Blocks. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.valueOfMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
            logger.d(MODULE) {
                "MimeType does not support Blocks. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }

        logger.d(MODULE) {
            "Use Blocks. previewSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
        }
        val exifOrientation: Int = previewDrawable.imageExifOrientation
        val imageUri = previewDrawable.requestUri
        return Blocks(host.context, zoomer, imageUri, exifOrientation)
    }


    var scrollBarEnabled: Boolean = true
        set(value) {
            field = value
            zoomer?.scrollBarEnabled = value
        }

    var readModeEnabled: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                zoomer?.readModeDecider = if (value) readModeDecider else null
            }
        }
    var readModeDecider: ReadModeDecider = DefaultReadModeDecider()
        set(value) {
            if (field != value) {
                field = value
                if (readModeEnabled) {
                    zoomer?.readModeDecider = value
                }
            }
        }

    var zoomScales: ZoomScales = AdaptiveTwoLevelScales()
        set(value) {
            if (field != value) {
                field = value
                zoomer?.zoomScales = value
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

    var showBlockBounds: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                blocks?.isShowBlockBounds = value
            }
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

    fun addOnScaleChangeListener(listener: OnScaleChangeListener) {
        this.onScaleChangeListenerList = (onScaleChangeListenerList ?: LinkedHashSet()).apply {
            add(listener)
        }
    }

    fun removeOnScaleChangeListener(listener: OnScaleChangeListener): Boolean {
        return onScaleChangeListenerList?.remove(listener) == true
    }

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

    fun getDrawMatrix(matrix: Matrix) = zoomer?.getDrawMatrix(matrix)

    fun getDrawRect(rectF: RectF) = zoomer?.getDrawRect(rectF)

    /** Gets the area that the user can see on the preview (not affected by rotation) */
    fun getVisibleRect(rect: Rect) = zoomer?.getVisibleRect(rect)
}