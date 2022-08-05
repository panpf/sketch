/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.base

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.util.LinkedList

open class LifecycleAndroidViewModel(val application1: Application) :
    AndroidViewModel(application1), LifecycleOwner {

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
): Lazy<VM> = createViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { ownerProducer().viewModelStore },
    factoryProducer = factoryProducer
)