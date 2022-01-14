package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.github.panpf.sketch.viewability.internal.AbsProgressViewAbility
import com.github.panpf.sketch.viewability.internal.Host
import com.github.panpf.sketch.viewability.internal.ViewAbilityContainerOwner

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

    override fun drawIndicator(canvas: Canvas, host: Host, progress: Float) {
        val layoutRect = host.layoutRect
        val paddingRect = host.paddingRect
        canvas.drawRect(
            layoutRect.left + paddingRect.left.toFloat(),
            layoutRect.top + paddingRect.top + ((progress * (layoutRect.height() - paddingRect.top - paddingRect.bottom))),
            layoutRect.right - paddingRect.right.toFloat(),
            layoutRect.bottom - paddingRect.top.toFloat(),
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