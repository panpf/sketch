package com.github.panpf.sketch.viewability.extensions

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.widget.ImageView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.viewability.DrawAbility
import com.github.panpf.sketch.viewability.LayoutAbility
import com.github.panpf.sketch.viewability.RequestListenerAbility
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainerOwner

class DataFromViewAbility(
    sizeDp: Float = DEFAULT_SIZE_DP
) : ViewAbility, RequestListenerAbility, DrawAbility, LayoutAbility {

    companion object {
        const val DEFAULT_SIZE_DP = 20f
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
        private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
        private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
        private const val FROM_FLAG_COLOR_DISK_CACHE = 0x77FFFF00 // yellow
        private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red
    }

    private var path: Path = Path()
    private val paint = Paint().apply { isAntiAlias = true }
    private val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

    override var view: ImageView? = null
        set(value) {
            field = value
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        initImageFromPath()
    }

    override fun onRequestStart(request: DisplayRequest) {
        view?.postInvalidate()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        view?.postInvalidate()
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        view?.postInvalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
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

    override fun onDrawForegroundBefore(canvas: Canvas) {
    }

    override fun onDrawForeground(canvas: Canvas) {
    }
}

fun ViewAbilityContainerOwner.showDataFrom(
    showDataFrom: Boolean = true,
    sizeDp: Float = DataFromViewAbility.DEFAULT_SIZE_DP
) {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is DataFromViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (showDataFrom) {
        viewAbilityContainer.addViewAbility(DataFromViewAbility(sizeDp))
    }
}