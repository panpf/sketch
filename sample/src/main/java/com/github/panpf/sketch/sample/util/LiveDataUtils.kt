package com.github.panpf.sketch.sample.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeWithViewLifecycle(view: View, observer: Observer<T>) {
    if (ViewCompat.isAttachedToWindow(view)) {
        observeForever(observer)
    }
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            try {
                observeForever(observer)
            } catch (e: IllegalArgumentException) {
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            removeObserver(observer)
        }
    })
}