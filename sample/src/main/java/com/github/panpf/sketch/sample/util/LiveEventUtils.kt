package com.github.panpf.sketch.sample.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.sample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import java.math.BigDecimal

fun <T> LiveEvent<T>.observeWithViewLifecycle(view: View, observer: com.github.panpf.liveevent.Listener<T>) {
    if (ViewCompat.isAttachedToWindow(view)) {
        listenForever(observer)
    }
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            try {
                listenForever(observer)
            } catch (e: IllegalArgumentException) {
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            removeListener(observer)
        }
    })
}