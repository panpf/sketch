package com.github.panpf.sketch.sample.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE
import com.github.panpf.sketch.ability.PROGRESS_INDICATOR_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.painter.MaskProgressPainter
import com.github.panpf.sketch.painter.RingProgressPainter
import com.github.panpf.sketch.painter.SectorProgressPainter
import com.github.panpf.sketch.painter.rememberMaskProgressPainter
import com.github.panpf.sketch.painter.rememberRingProgressPainter
import com.github.panpf.sketch.painter.rememberSectorProgressPainter

@Composable
fun rememberThemeMaskProgressPainter(
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
): MaskProgressPainter {
    val colorScheme = MaterialTheme.colorScheme
    val primary = colorScheme.primary
    val primaryContainer = primary.copy(alpha = 0.4f)
    return rememberMaskProgressPainter(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        maskColor = primaryContainer
    )
}

@Composable
fun rememberThemeRingProgressPainter(
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
): RingProgressPainter {
    val colorScheme = MaterialTheme.colorScheme
    val primary = colorScheme.primary
    val primaryContainer = primary.copy(alpha = 0.4f)
    return rememberRingProgressPainter(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        ringColor = primary,
        backgroundColor = primaryContainer,
    )
}

@Composable
fun rememberThemeSectorProgressPainter(
    hiddenWhenIndeterminate: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_INDETERMINATE,
    hiddenWhenCompleted: Boolean = PROGRESS_INDICATOR_HIDDEN_WHEN_COMPLETED,
    stepAnimationDuration: Int = PROGRESS_INDICATOR_STEP_ANIMATION_DURATION,
): SectorProgressPainter {
    val colorScheme = MaterialTheme.colorScheme
    val primary = colorScheme.primary
    val primaryContainer = primary.copy(alpha = 0.4f)
    return rememberSectorProgressPainter(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        strokeColor = primary,
        progressColor = primary,
        backgroundColor = primaryContainer,
    )
}