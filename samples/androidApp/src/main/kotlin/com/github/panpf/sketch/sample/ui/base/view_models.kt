package com.github.panpf.sketch.sample.ui.base

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

/**
 * Adapt to the Android Lifecycle ViewModel framework and obtain the ViewModel instance of the parent Fragment or Activity
 */
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
 * Adapt to the Android Lifecycle ViewModel framework and obtain the ViewModel instance of the parent Fragment or Activity based on the Class type
 */
@MainThread
inline fun <reified VM : ViewModel, reified VMSO : ViewModelStoreOwner> Fragment.parentViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val ownerProducer = createOwnerProducerWith<VMSO>()
    return createViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = { ownerProducer().viewModelStore },
        factoryProducer = factoryProducer
    )
}

/**
 * Adapt the Koin dependency injection framework to obtain the ViewModel instance of the parent Fragment or Activity
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.parentViewModel(
    qualifier: Qualifier? = null,
    noinline ownerProducer: () -> ViewModelStoreOwner = {
        this.parentFragment ?: requireActivity()
    },
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<VM> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(qualifier, ownerProducer, extrasProducer, parameters)
    }
}

/**
 * Adapt to the Koin dependency injection framework and obtain the ViewModel instance of the parent Fragment or Activity based on the Class type
 */
@MainThread
inline fun <reified VM : ViewModel, reified VMSO : ViewModelStoreOwner> Fragment.parentViewModelWith(
    qualifier: Qualifier? = null,
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline parameters: (() -> ParametersHolder)? = null,
): Lazy<VM> {
    val ownerProducer = createOwnerProducerWith<VMSO>()
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(qualifier, ownerProducer, extrasProducer, parameters)
    }
}

inline fun <reified VMSO : ViewModelStoreOwner> Fragment.createOwnerProducerWith(): () -> ViewModelStoreOwner {
    return {
        var viewModelStoreOwner: ViewModelStoreOwner? =
            this.parentFragment ?: this.requireActivity()
        while (viewModelStoreOwner != null) {
            if (viewModelStoreOwner::class == VMSO::class) {
                break
            } else if (viewModelStoreOwner is Fragment) {
                viewModelStoreOwner = viewModelStoreOwner.parentFragment
                    ?: viewModelStoreOwner.requireActivity()
            } else {
                viewModelStoreOwner = null
                break
            }
        }
        viewModelStoreOwner ?: this
    }
}