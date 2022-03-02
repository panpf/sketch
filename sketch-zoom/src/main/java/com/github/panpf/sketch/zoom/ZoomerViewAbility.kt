package com.github.panpf.sketch.zoom

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.widget.ImageView.ScaleType
import android.widget.ImageView.ScaleType.MATRIX
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.decode.internal.supportBitmapRegionDecoder
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.util.isAttachedToWindowCompat
import com.github.panpf.sketch.viewability.Host
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbility.AttachObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawableObserver
import com.github.panpf.sketch.viewability.ViewAbility.ScaleTypeObserver
import com.github.panpf.sketch.viewability.ViewAbility.SizeChangeObserver
import com.github.panpf.sketch.viewability.ViewAbility.TouchEventObserver
import com.github.panpf.sketch.zoom.new.Blocks
import com.github.panpf.sketch.zoom.new.Zoomer

class ZoomerViewAbility : ViewAbility, AttachObserver, ScaleTypeObserver, DrawObserver,
    DrawableObserver, TouchEventObserver, SizeChangeObserver {

    companion object {
        private const val MODULE = "ZoomerViewAbility"
    }

    private var zoomer: Zoomer? = null
    private var blockDisplayer: Blocks? = null
    private var readModeDecider: ReadModeDecider? = null
        set(value) {
            field = value
            destroyZoomer()
            createZoomer()
        }

    override var host: Host? = null

    val isEnabledReadMode: Boolean
        get() = readModeDecider != null

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        val host = host ?: return
        destroyZoomer()
        if (host.view.isAttachedToWindowCompat) {
            createZoomer()
        }
    }

    override fun onAttachedToWindow() {
        createZoomer()
    }

    override fun onDetachedFromWindow() {
        destroyZoomer()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
//        zoomer?.reset("onSizeChanged")
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
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

    private fun createZoomer() {
        val host = host ?: return
        val scaleType = host.superScaleType
        host.superScaleType = MATRIX
        val zoomer = Zoomer(host.context.sketch, host.view, scaleType)
        val blockDisplayer = tryNewBlockDisplayer(zoomer)
        this.zoomer = zoomer
        this.blockDisplayer = blockDisplayer
    }

    private fun destroyZoomer() {
        val host = host ?: return
        zoomer?.apply {
            recycle("destroyZoomer")
            host.superScaleType = scaleType
        }
        zoomer = null
        blockDisplayer?.recycle("destroyZoomer")
        blockDisplayer = null
    }

    override fun getScaleType(): ScaleType? = zoomer?.scaleType

    fun enabledReadMode(readModeDecider: ReadModeDecider = defaultReadModeDecider()) {
        this.readModeDecider = readModeDecider
    }

    fun disabledReadMode() {
        this.readModeDecider = null
    }

    private fun tryNewBlockDisplayer(imageZoomer: Zoomer): Blocks? {
        val host = host ?: return null
        val logger = host.context.sketch.logger

        val previewDrawable = host.drawable?.getLastDrawable()
        if (previewDrawable !is SketchDrawable || previewDrawable is Animatable) {
            return null
        }

        val previewWidth = previewDrawable.bitmapInfo.width
        val previewHeight = previewDrawable.bitmapInfo.height
        val originWidth = previewDrawable.imageInfo.width
        val originHeight = previewDrawable.imageInfo.height
        val mimeType = previewDrawable.imageInfo.mimeType
        val key = previewDrawable.requestKey

        if (previewWidth >= originWidth && previewHeight >= originHeight) {
            logger.d(MODULE) {
                ("Don't need to use Blocks. previewSize: %dx%d, " +
                        "originSize: %dx%d, mimeType: %s. %s")
                    .format(previewWidth, previewHeight, originWidth, originHeight, mimeType, key)
            }
            return null
        }
        if (ImageFormat.valueOfMimeType(mimeType)?.supportBitmapRegionDecoder() != true) {
            logger.d(MODULE) {
                ("MimeType does not support Blocks. previewSize: %dx%d, " +
                        "originSize: %dx%d, mimeType: %s. %s")
                    .format(previewWidth, previewHeight, originWidth, originHeight, mimeType, key)
            }
            return null
        }

        logger.d(MODULE) {
            "Use Blocks. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s"
                .format(previewWidth, previewHeight, originWidth, originHeight, mimeType, key)
        }
        val exifOrientation: Int = previewDrawable.imageExifOrientation
        val imageUri = previewDrawable.requestUri
        return Blocks(host.context, imageZoomer, imageUri, exifOrientation)
    }
}