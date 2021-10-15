package me.panpf.sketch.sample.util

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> safeRun(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> LiveData<T>.observe(view: View, observer: Observer<T>) {
    observeForever(observer)
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