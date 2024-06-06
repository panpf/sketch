package com.github.panpf.sketch.state

import com.github.panpf.sketch.state.ErrorStateImage.UriEmptyCondition
import com.github.panpf.sketch.util.DrawableEqualizer


/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyDrawable: DrawableEqualizer): ErrorStateImage.Builder =
    apply {
        addState(UriEmptyCondition to DrawableStateImage(emptyDrawable))
    }

/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyResId: Int): ErrorStateImage.Builder =
    apply {
        addState(UriEmptyCondition to DrawableStateImage(emptyResId))
    }