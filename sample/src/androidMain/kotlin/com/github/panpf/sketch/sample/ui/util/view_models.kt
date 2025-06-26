package com.github.panpf.sketch.sample.ui.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier


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

/**
 * Retrieve Lazy ViewModel instance for Parent Fragment or Activity
 * @param qualifier
 * @param ownerProducer
 * @param extrasProducer
 * @param parameters
 */
@MainThread
inline fun <reified T : ViewModel> Fragment.parentViewModel(
    qualifier: Qualifier? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = {
        this.parentFragment ?: requireActivity()
    },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(qualifier, ownerProducer, extrasProducer, parameters)
    }
}