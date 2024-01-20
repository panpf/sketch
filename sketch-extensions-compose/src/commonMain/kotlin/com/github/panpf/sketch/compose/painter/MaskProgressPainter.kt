package com.github.panpf.sketch.compose.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_MASK_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.compose.painter.internal.AbsProgressPainter

@Composable
fun rememberMaskProgressPainter(
    maskColor: Color = Color(PROGRESS_INDICATOR_MASK_COLOR),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
): MaskProgressPainter {
    return remember(
        maskColor,
        hiddenWhenIndeterminate,
        hiddenWhenCompleted,
        stepAnimationDuration
    ) {
        MaskProgressPainter(
            maskColor = maskColor,
            hiddenWhenIndeterminate = hiddenWhenIndeterminate,
            hiddenWhenCompleted = hiddenWhenCompleted,
            stepAnimationDuration = stepAnimationDuration
        )
    }
}

class MaskProgressPainter(
    private val maskColor: Color = Color(PROGRESS_INDICATOR_MASK_COLOR),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
) : AbsProgressPainter(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
) {

    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.drawProgress(drawProgress: Float) {
        val progressHeight = drawProgress * size.height
        drawRect(
            color = maskColor,
            topLeft = Offset(0f, progressHeight),
            size = Size(size.width, size.height - progressHeight)
        )
    }
}