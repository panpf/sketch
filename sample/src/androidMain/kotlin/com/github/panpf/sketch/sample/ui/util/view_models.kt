package com.github.panpf.sketch.sample.ui.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner


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