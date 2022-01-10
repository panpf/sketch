package com.github.panpf.sketch.internal

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.getLastDrawable

open class DataFromHelper(val sizeDp: Float = DEFAULT_SIZE_DP) {

    companion object {
        const val DEFAULT_SIZE_DP = 20f
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
        private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
        private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
        private const val FROM_FLAG_COLOR_DISK_CACHE = 0x77FFFF00 // yellow
        private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red
    }

    private var path: Path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    var view: SketchImageView? = null
        set(value) {
            field = value
            initImageFromPath()
        }
    private var show: Boolean = false
    private val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

    open val key: String by lazy {
        "DataFromHelper(sizeDp=${sizeDp})"
    }

    open fun onLayout() {
        initImageFromPath()
    }

    private fun initImageFromPath() {
        path.reset()
        val view = view ?: return
        path.apply {
            moveTo(
                view.right - view.paddingRight - realSize,
                view.top + view.paddingTop.toFloat()
            )
            lineTo(
                view.right - view.paddingRight.toFloat(),
                view.top + view.paddingTop.toFloat()
            )
            lineTo(
                view.right - view.paddingRight.toFloat(),
                view.top - view.paddingTop.toFloat() + realSize
            )
            close()
        }
    }

    open fun onRequestStart(
        request: DisplayRequest,
    ) {
        show = false
        view?.postInvalidate()
    }

    open fun onRequestError(
        request: DisplayRequest,
        result: Error,
    ) {
        show = false
        view?.postInvalidate()
    }

    open fun onRequestSuccess(
        request: DisplayRequest,
        result: Success,
    ) {
        show = true
        view?.postInvalidate()
    }

    open fun onDraw(canvas: Canvas) {
//        if (!show) return
        val lastDrawable = view?.drawable?.getLastDrawable() ?: return
        if (lastDrawable !is SketchDrawable) return
        val dataFrom = lastDrawable.dataFrom ?: return
        val path = path.takeIf { !it.isEmpty } ?: return
        when (dataFrom) {
            DataFrom.MEMORY_CACHE -> paint.color = FROM_FLAG_COLOR_MEMORY_CACHE
            DataFrom.DISK_CACHE -> paint.color = FROM_FLAG_COLOR_DISK_CACHE
            DataFrom.NETWORK -> paint.color = FROM_FLAG_COLOR_NETWORK
            DataFrom.LOCAL -> paint.color = FROM_FLAG_COLOR_LOCAL
            DataFrom.MEMORY -> paint.color = FROM_FLAG_COLOR_MEMORY
            else -> return
        }
        canvas.drawPath(path, paint)
    }
}