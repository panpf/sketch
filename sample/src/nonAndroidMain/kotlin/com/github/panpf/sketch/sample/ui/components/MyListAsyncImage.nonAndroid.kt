package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.state.StateImage

@Composable
actual fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage? {
    // Animated svg is not yet supported on non-Android platforms
    return null
}

@Composable
actual inline fun ImageRequest.Builder.platformListImageRequest(appSettings: AppSettings) {
    val cache by appSettings.cacheDecodeTimeoutFrame.collectAsState()
    cacheDecodeTimeoutFrame(cache)
}