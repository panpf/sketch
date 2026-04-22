package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.AppSettings

@Composable
actual inline fun composablePlatformAsyncImageSettings(appSettings: AppSettings): ImageOptions {
    return ComposableImageOptions {
        val cache by appSettings.cacheDecodeTimeoutFrame.collectAsState()
        cacheDecodeTimeoutFrame(cache)

        val videoFramePercent by appSettings.videoFramePercent.collectAsState()
        videoFramePercent(videoFramePercent)

        val preferVideoCover by appSettings.preferVideoCover.collectAsState()
        preferVideoCover(preferVideoCover)
    }
}