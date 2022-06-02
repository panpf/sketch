package com.github.panpf.sketch.viewability

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.util.findLastSketchDrawable

/**
 * Set to enable the data source identification function
 */
fun ViewAbilityContainer.showDataFromLogo(sizeDp: Float = DataFromLogoAbility.DEFAULT_SIZE_DP) {
    removeDataFromLogo()
    addViewAbility(DataFromLogoAbility(sizeDp))
}

/**
 * Remove the data source identification function
 */
fun ViewAbilityContainer.removeDataFromLogo() {
    viewAbilityList
        .find { it is DataFromLogoAbility }
        ?.let { removeViewAbility(it) }
}

/**
 * Returns true if data source identification feature is enabled
 */
val ViewAbilityContainer.isShowDataFromLogo: Boolean
    get() = viewAbilityList.find { it is DataFromLogoAbility } != null

/**
 * In the upper right corner of the View, a semi-transparent color block called Samsung is displayed to indicate where the image is loaded this time.
 */
class DataFromLogoAbility(
    sizeDp: Float = DEFAULT_SIZE_DP
) : ViewAbility, AttachObserver, DrawObserver, LayoutObserver, DrawableObserver {

    companion object {
        const val DEFAULT_SIZE_DP = 20f
        private const val FROM_FLAG_COLOR_MEMORY_CACHE = 0x7700FF00   // green
        private const val FROM_FLAG_COLOR_MEMORY = 0x77008800   // dark green
        private const val FROM_FLAG_COLOR_RESULT_DISK_CACHE = 0x77FFFF00 // yellow
        private const val FROM_FLAG_COLOR_DISK_CACHE = 0x77FF8800 // dark yellow
        private const val FROM_FLAG_COLOR_LOCAL = 0x771E90FF   // dodger blue
        private const val FROM_FLAG_COLOR_NETWORK = 0x77FF0000  // red
    }

    private var path: Path = Path()
    private val paint = Paint().apply { isAntiAlias = true }
    private val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

    override var host: Host? = null

    override fun onAttachedToWindow() {
        reset()
        host?.view?.invalidate()
    }

    override fun onDetachedFromWindow() {

    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        reset()
        host?.view?.invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        reset()
        host?.view?.invalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        val path = path.takeIf { !it.isEmpty } ?: return
        canvas.drawPath(path, paint)
    }

    private fun reset(): Boolean {
        // Execute first, path will remain empty if subsequent conditions are not met, and the onDraw method will not execute
        path.reset()
        val host = host ?: return false

        val lastDrawable = host.container.getDrawable()?.findLastSketchDrawable() ?: return false
        when (lastDrawable.dataFrom) {
            DataFrom.MEMORY_CACHE -> paint.color = FROM_FLAG_COLOR_MEMORY_CACHE
            DataFrom.MEMORY -> paint.color = FROM_FLAG_COLOR_MEMORY
            DataFrom.RESULT_DISK_CACHE -> paint.color = FROM_FLAG_COLOR_RESULT_DISK_CACHE
            DataFrom.DISK_CACHE -> paint.color = FROM_FLAG_COLOR_DISK_CACHE
            DataFrom.LOCAL -> paint.color = FROM_FLAG_COLOR_LOCAL
            DataFrom.NETWORK -> paint.color = FROM_FLAG_COLOR_NETWORK
        }

        val view = host.view
        val viewWidth = host.view.width
        path.apply {
            moveTo(
                viewWidth - view.paddingRight - realSize,
                view.paddingTop.toFloat()
            )
            lineTo(
                viewWidth - view.paddingRight.toFloat(),
                view.paddingTop.toFloat()
            )
            lineTo(
                viewWidth - view.paddingRight.toFloat(),
                view.paddingTop.toFloat() + realSize
            )
            close()
        }
        return true
    }
}