package com.github.panpf.sketch.sample

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.data.api.ApiServices
import com.github.panpf.sketch.sample.util.ParamLazy

object MyServices {
    val apiServiceLazy = ParamLazy<Context, ApiServices> { ApiServices(it) }
    val prefsServiceLazy = ParamLazy<Context, PrefsService> { PrefsService(it) }
}

val Context.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.applicationContext)
val Fragment.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.requireContext().applicationContext)
val View.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.context.applicationContext)

val Context.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.applicationContext)
val Fragment.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.requireContext().applicationContext)
val View.prefsService: PrefsService
    get() = MyServices.prefsServiceLazy.get(this.context.applicationContext)