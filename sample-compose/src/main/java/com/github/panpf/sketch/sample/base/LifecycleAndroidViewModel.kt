package com.github.panpf.sketch.sample.base

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.*
import java.util.*

open class LifecycleAndroidViewModel(val application1: Application) : AndroidViewModel(application1), LifecycleOwner {

    private val clearedListenerList: MutableList<OnClearedListener> = LinkedList()

    @SuppressLint("StaticFieldLeak")
    private var lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun addOnClearedListener(clearedListener: OnClearedListener) {
        clearedListenerList.add(clearedListener)
    }

    @Suppress("unused")
    fun removeOnClearedListener(clearedListener: OnClearedListener) {
        clearedListenerList.remove(clearedListener)
    }

    override fun onCleared() {
        super.onCleared()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        clearedListenerList.forEach { onDestroyViewListener ->
            onDestroyViewListener.onCleared()
        }
        clearedListenerList.clear()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    fun interface OnClearedListener {
        fun onCleared()
    }
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.parentViewModels(
        noinline ownerProducer: () -> ViewModelStoreOwner = {
            this.parentFragment ?: requireActivity()
        },
        noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)