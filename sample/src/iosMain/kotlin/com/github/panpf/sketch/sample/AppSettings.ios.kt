package com.github.panpf.sketch.sample

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame

actual fun isDebugMode(): Boolean = true

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {
    cacheDecodeTimeoutFrame(appSettings.cacheDecodeTimeoutFrame.value)
}