package com.github.panpf.sketch.state

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.state.ErrorStateImage.UriEmptyCondition


/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyDrawable: Drawable): ErrorStateImage.Builder =
    apply {
        addState(UriEmptyCondition to DrawableStateImage(emptyDrawable))
    }

/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyImageResId: Int): ErrorStateImage.Builder =
    apply {
        addState(UriEmptyCondition to DrawableStateImage(emptyImageResId))
    }