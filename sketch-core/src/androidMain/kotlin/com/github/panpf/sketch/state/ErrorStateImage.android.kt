package com.github.panpf.sketch.state

import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.state.ErrorStateImage.Condition
import com.github.panpf.sketch.util.IntColor


/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    drawable: DrawableEqualizer
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(drawable))
}

/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    resId: Int
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(resId))
}

/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    intColor: IntColor
): ErrorStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(intColor))
}