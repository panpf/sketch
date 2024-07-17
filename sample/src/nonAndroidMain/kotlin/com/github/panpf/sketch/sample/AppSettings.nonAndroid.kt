package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.util.ParamLazy


private val appSettingsLazy = ParamLazy<PlatformContext, AppSettings> { AppSettings(it) }

actual val PlatformContext.appSettings: AppSettings
    get() = appSettingsLazy.get(this)