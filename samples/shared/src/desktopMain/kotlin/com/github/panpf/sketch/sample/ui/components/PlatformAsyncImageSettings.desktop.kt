package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.sample.AppSettings

@Composable
actual inline fun composablePlatformAsyncImageSettings(appSettings: AppSettings): ImageOptions {
    return ComposableImageOptions {
        val cache by appSettings.cacheDecodeTimeoutFrame.collectAsState()
        cacheDecodeTimeoutFrame(cache)
    }
}