package com.github.panpf.sketch.viewability.internal

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

internal fun Context?.getLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}

internal val View.isAttachedToWindowCompat: Boolean
    get() = ViewCompat.isAttachedToWindow(this)