package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_COLOR
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_SIZE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_RING_WIDTH_PERCENT
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.painter.internal.AbsProgressPainter

@Composable
fun rememberRingProgressPainter(
    size: Dp = PROGRESS_INDICATOR_RING_SIZE.dp,
    ringWidth: Dp = size * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
    ringColor: Color = Color(PROGRESS_INDICATOR_RING_COLOR),
    backgroundColor: Color = ringColor.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
): RingProgressPainter {
    val density = LocalDensity.current
    return remember(
        density,
        size,
        ringWidth,
        ringColor,
        backgroundColor,
        hiddenWhenIndeterminate,
        hiddenWhenCompleted,
        stepAnimationDuration
    ) {
        RingProgressPainter(
            density = density,
            size = size,
            ringWidth = ringWidth,
            ringColor = ringColor,
            backgroundColor = backgroundColor,
            hiddenWhenIndeterminate = hiddenWhenIndeterminate,
            hiddenWhenCompleted = hiddenWhenCompleted,
            stepAnimationDuration = stepAnimationDuration
        )
    }
}

@Stable
class RingProgressPainter(
    density: Density,
    private val size: Dp = PROGRESS_INDICATOR_RING_SIZE.dp,
    private val ringWidth: Dp = size * PROGRESS_INDICATOR_RING_WIDTH_PERCENT,
    private val ringColor: Color = Color(PROGRESS_INDICATOR_RING_COLOR),
    private val backgroundColor: Color = ringColor.copy(alpha = PROGRESS_INDICATOR_BACKGROUND_ALPHA_PERCENT),
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
) : AbsProgressPainter(
    hiddenWhenIndeterminate = hiddenWhenIndeterminate,
    hiddenWhenCompleted = hiddenWhenCompleted,
    stepAnimationDuration = stepAnimationDuration
), SketchPainter {

    override val intrinsicSize: Size = with(density) { Size(size.toPx(), size.toPx()) }

    override fun DrawScope.drawProgress(drawProgress: Float) {
        // background
        val widthRadius = size.width / 2f
        val heightRadius = size.height / 2f
        val radius = widthRadius.coerceAtMost(heightRadius)
        val cx = 0 + widthRadius
        val cy = 0 + heightRadius
        val center = Offset(widthRadius, heightRadius)
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(ringWidth.toPx())
        )

        // progress
        val sweepAngle = drawProgress * 360f
        drawArc(
            color = ringColor,
            startAngle = 270f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(ringWidth.toPx(), cap = StrokeCap.Round),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RingProgressPainter) return false
        if (size != other.size) return false
        if (ringWidth != other.ringWidth) return false
        if (ringColor != other.ringColor) return false
        if (backgroundColor == other.backgroundColor) return false
        if (hiddenWhenIndeterminate != other.hiddenWhenIndeterminate) return false
        if (hiddenWhenCompleted != other.hiddenWhenCompleted) return false
        return stepAnimationDuration == other.stepAnimationDuration
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + ringWidth.hashCode()
        result = 31 * result + ringColor.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        result = 31 * result + hiddenWhenIndeterminate.hashCode()
        result = 31 * result + hiddenWhenCompleted.hashCode()
        result = 31 * result + stepAnimationDuration.hashCode()
        return result
    }

    override fun toString(): String {
        return "RingProgressPainter(size=$size, ringWidth=$ringWidth, ringColor=$ringColor, backgroundColor=$backgroundColor, hiddenWhenIndeterminate=$hiddenWhenIndeterminate, hiddenWhenCompleted=$hiddenWhenCompleted, stepAnimationDuration=$stepAnimationDuration)"
    }
}