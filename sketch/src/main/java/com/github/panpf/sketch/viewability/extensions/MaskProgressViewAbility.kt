package com.github.panpf.sketch.viewability.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt
import com.github.panpf.sketch.viewability.ViewAbilityContainerOwner

class MaskProgressViewAbility(
    @ColorInt private val maskColor: Int = DEFAULT_MASK_COLOR,
) : AbsProgressViewAbility() {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
    }

    private val paint = Paint().apply {
        color = maskColor
        isAntiAlias = true
    }

    override fun drawIndicator(canvas: Canvas, view: View, progress: Float) {
        canvas.drawRect(
            view.left + view.paddingLeft.toFloat(),
            view.top + view.paddingTop + ((progress * (view.height - view.paddingTop - view.paddingBottom))),
            view.right - view.paddingRight.toFloat(),
            view.bottom - view.paddingTop.toFloat(),
            paint
        )
    }
}

fun ViewAbilityContainerOwner.showMaskProgressIndicator(
    showProgressIndicator: Boolean = true,
    @ColorInt maskColor: Int = MaskProgressViewAbility.DEFAULT_MASK_COLOR
) {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is MaskProgressViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (showProgressIndicator) {
        viewAbilityContainer.addViewAbility(MaskProgressViewAbility(maskColor))
    }
}