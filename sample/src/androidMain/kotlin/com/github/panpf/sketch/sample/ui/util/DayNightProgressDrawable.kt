package com.github.panpf.sketch.sample.ui.util

import android.content.Context
import androidx.core.graphics.ColorUtils
import com.github.panpf.sketch.drawable.AbsProgressDrawable.Companion.DEFAULT_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.sample.R


fun createDayNightMaskProgressDrawable(
    context: Context,
    hiddenWhenIndeterminate: Boolean = false,
    hiddenWhenCompleted: Boolean = true,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
): MaskProgressDrawable {
    val primary = context.resources.getColor(R.color.md_theme_primary)
    val primaryContainer = ColorUtils.setAlphaComponent(primary, 100)
    return MaskProgressDrawable(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        maskColor = primaryContainer
    )
}


fun createDayNightRingProgressDrawable(
    context: Context,
    hiddenWhenIndeterminate: Boolean = false,
    hiddenWhenCompleted: Boolean = true,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
): RingProgressDrawable {
    val primary = context.resources.getColor(R.color.md_theme_primary)
    val primaryContainer = ColorUtils.setAlphaComponent(primary, 100)
    return RingProgressDrawable(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        ringColor = primary,
        backgroundColor = primaryContainer,
    )
}

fun createDayNightSectorProgressDrawable(
    context: Context,
    hiddenWhenIndeterminate: Boolean = false,
    hiddenWhenCompleted: Boolean = true,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
): SectorProgressDrawable {
    val primary = context.resources.getColor(R.color.md_theme_primary)
    val primaryContainer = ColorUtils.setAlphaComponent(primary, 100)
    return SectorProgressDrawable(
        hiddenWhenIndeterminate = hiddenWhenIndeterminate,
        hiddenWhenCompleted = hiddenWhenCompleted,
        stepAnimationDuration = stepAnimationDuration,
        strokeColor = primary,
        progressColor = primary,
        backgroundColor = primaryContainer,
    )
}