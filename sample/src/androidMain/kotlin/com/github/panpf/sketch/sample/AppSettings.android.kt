package com.github.panpf.sketch.sample

import android.view.View
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.util.ParamLazy

private val appSettingsLazy = ParamLazy<PlatformContext, AppSettings> { AppSettings(it) }

actual val PlatformContext.appSettings: AppSettings
    get() = appSettingsLazy.get(this.applicationContext)

val Fragment.appSettings: AppSettings
    get() = this.requireContext().appSettings

val View.appSettings: AppSettings
    get() = this.context.appSettings