package com.github.panpf.sketch.internal

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt

class MaskProgressIndicator(
    @ColorInt val maskColor: Int = DEFAULT_MASK_COLOR,
) : AbsProgressIndicator() {

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
    }

    private val paint = Paint().apply {
        color = maskColor
        isAntiAlias = true
    }

    override val key: String by lazy {
        "MaskProgressIndicator(maskColor=$maskColor)"
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