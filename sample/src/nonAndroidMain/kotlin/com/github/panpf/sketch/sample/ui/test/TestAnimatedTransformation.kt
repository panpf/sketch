package com.github.panpf.sketch.sample.ui.test

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.util.Rect

actual data object TestAnimatedTransformation : AnimatedTransformation {

    override val key: String = "TestAnimatedTransformation"

    private val paint = Paint().apply {
        color = androidx.compose.ui.graphics.Color.Red
    }

    override fun transform(canvas: Any, bounds: Rect): PixelOpacity {
        if (canvas is androidx.compose.ui.graphics.Canvas) {
            val radius = bounds.width() / 4f
            canvas.drawCircle(center = Offset(radius, radius), radius = radius, paint = paint)
        }
        return PixelOpacity.TRANSLUCENT
    }
}