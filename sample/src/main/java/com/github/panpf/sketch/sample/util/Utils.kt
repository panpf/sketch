package com.github.panpf.sketch.sample.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.github.panpf.liveevent.LiveEvent
import java.lang.reflect.ParameterizedType

fun <T> safeRun(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> LiveData<T>.observeFromView(view: View, observer: Observer<T>) {
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

fun <T> LiveEvent<T>.observeFromView(view: View, observer: com.github.panpf.liveevent.Listener<T>) {
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

fun <T> LiveData<T>.observeFromViewAndInit(view: View, observer: Observer<T>) {
    if (ViewCompat.isAttachedToWindow(view)) {
        observeForever(observer)
    } else {
        observer.onChanged(value)
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

fun Class<*>.instanceViewBinding(viewBindingParamIndex: Int, inflater: LayoutInflater, parent: ViewGroup?): ViewBinding {
    val type = genericSuperclass
    if (type is ParameterizedType) {
        val clazz = type.actualTypeArguments[viewBindingParamIndex] as Class<ViewBinding>
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, inflater, parent, false) as ViewBinding
    } else {
        throw IllegalArgumentException("${this} 需要定义一个泛型参数，例如 class MyFragment<T: ViewBinding>: Fragment()")
    }
}