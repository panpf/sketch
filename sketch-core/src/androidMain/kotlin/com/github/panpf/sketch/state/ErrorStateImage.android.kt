package com.github.panpf.sketch.state

import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.state.ErrorStateImage.Condition
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor


/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    drawable: DrawableEqualizer
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(drawable))
}

/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    resId: Int
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(resId))
}

/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    color: IntColor
): ErrorStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}

/**
 * Add a custom error state
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    color: ResColor
): ErrorStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}