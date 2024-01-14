package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.stateimage.ErrorStateImage.Builder
import com.github.panpf.sketch.stateimage.ErrorStateImage.UriEmptyCondition


/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyDrawable: Drawable): Builder = apply {
    addState(UriEmptyCondition to DrawableStateImage(emptyDrawable))
}

/**
 * Add a StateImage dedicated to the empty uri error
 */
fun ErrorStateImage.Builder.uriEmptyError(emptyImageResId: Int): Builder = apply {
    addState(UriEmptyCondition to DrawableStateImage(emptyImageResId))
}