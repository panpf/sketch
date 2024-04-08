package com.github.panpf.sketch.sample

import com.github.panpf.sketch.request.ImageOptions

actual fun isDebugMode(): Boolean = true

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {
}