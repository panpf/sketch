package me.panpf.sketch.sample

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import me.panpf.sketch.sample.net.ApiServices
import me.panpf.sketch.sample.util.ParamLazy

object MyServices {
    val apiServiceLazy = ParamLazy<Context, ApiServices> { ApiServices(it) }
    val appSettingsServiceLazy = ParamLazy<Context, AppSettingsService> { AppSettingsService(it) }
}

val Context.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.applicationContext)
val Fragment.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.requireContext().applicationContext)
val View.apiService: ApiServices
    get() = MyServices.apiServiceLazy.get(this.context.applicationContext)

val Context.appSettingsService: AppSettingsService
    get() = MyServices.appSettingsServiceLazy.get(this.applicationContext)
val Fragment.appSettingsService: AppSettingsService
    get() = MyServices.appSettingsServiceLazy.get(this.requireContext().applicationContext)
val View.appSettingsService: AppSettingsService
    get() = MyServices.appSettingsServiceLazy.get(this.context.applicationContext)