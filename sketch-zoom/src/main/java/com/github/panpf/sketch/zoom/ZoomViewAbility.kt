package com.github.panpf.sketch.zoom

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
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
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbility.AttachObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawableObserver
import com.github.panpf.sketch.viewability.ViewAbility.ScaleTypeObserver
import com.github.panpf.sketch.viewability.ViewAbility.SizeChangeObserver
import com.github.panpf.sketch.viewability.ViewAbility.TouchEventObserver
import com.github.panpf.sketch.viewability.ViewAbility.VisibilityChangedObserver
import com.github.panpf.sketch.zoom.block.Blocks

class ZoomViewAbility : ViewAbility, AttachObserver, ScaleTypeObserver, DrawObserver,
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

    override var host: Host? = null

    var scrollBarEnabled: Boolean = true
        set(value) {
            field = value
            zoomer?.enabledScrollBar = value
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

    override fun onDrawForegroundBefore(canvas: Canvas) {

    }

    override fun onDrawForeground(canvas: Canvas) {

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
            enabledScrollBar = this@ZoomViewAbility.scrollBarEnabled
        }
        this.zoomer = newZoomer
        this.blocks = newZoomer?.let { tryNewBlocks(it) }
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
        val view = host.view

        val viewWidth = view.width - view.paddingLeft - view.paddingRight
        val viewHeight = view.height - view.paddingTop - view.paddingBottom
        if (viewWidth <= 0 || viewHeight <= 0) {
            return null
        }
        val viewSize = Size(viewWidth, viewHeight)

        val previewDrawable = host.drawable?.getLastDrawable()
        if (previewDrawable !is SketchDrawable) {
            return null
        }
        val previewWidth = previewDrawable.intrinsicWidth
        val previewHeight = previewDrawable.intrinsicHeight
        val originWidth = previewDrawable.imageInfo.width
        val originHeight = previewDrawable.imageInfo.height
        if (previewWidth <= 0 || previewHeight <= 0 || originWidth <= 0 || originHeight <= 0) {
            return null
        }
        val drawableSize = Size(previewWidth, previewHeight)
        val imageSize = Size(originWidth, originHeight)

        val scaleType = host.superScaleType
        return Zoomer(
            host.context,
            view = host.view,
            viewSize = viewSize,
            imageSize = imageSize,
            drawableSize = drawableSize,
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
                ("Don't need to use Blocks. previewSize: %dx%d, " +
                        "imageSize: %dx%d, mimeType: %s. %s")
                    .format(previewWidth, previewHeight, imageWidth, imageHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.valueOfMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
            logger.d(MODULE) {
                ("MimeType does not support Blocks. previewSize: %dx%d, " +
                        "imageSize: %dx%d, mimeType: %s. %s")
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
}