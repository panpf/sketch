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
import com.github.panpf.sketch.compose.painter.internal.SketchPainter

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
), SketchPainter {

    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.drawProgress(drawProgress: Float) {
        val progressHeight = drawProgress * size.height
        drawRect(
            color = maskColor,
            topLeft = Offset(0f, progressHeight),
            size = Size(size.width, size.height - progressHeight)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaskProgressPainter) return false
        if (maskColor != other.maskColor) return false
        if (hiddenWhenIndeterminate != other.hiddenWhenIndeterminate) return false
        if (hiddenWhenCompleted != other.hiddenWhenCompleted) return false
        return stepAnimationDuration == other.stepAnimationDuration
    }

    override fun hashCode(): Int {
        var result = maskColor.hashCode()
        result = 31 * result + hiddenWhenIndeterminate.hashCode()
        result = 31 * result + hiddenWhenCompleted.hashCode()
        result = 31 * result + stepAnimationDuration.hashCode()
        return result
    }

    override fun toString(): String {
        return "MaskProgressPainter(maskColor=$maskColor, hiddenWhenIndeterminate=$hiddenWhenIndeterminate, hiddenWhenCompleted=$hiddenWhenCompleted, stepAnimationDuration=$stepAnimationDuration)"
    }
}