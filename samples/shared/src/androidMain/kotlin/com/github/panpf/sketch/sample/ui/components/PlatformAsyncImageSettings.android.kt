package com.github.panpf.sketch.sample.ui.components

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.AppSettings

@Suppress("NOTHING_TO_INLINE")
@Composable
actual inline fun composablePlatformAsyncImageSettings(appSettings: AppSettings): ImageOptions {
    return ComposableImageOptions {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val colorSpace by appSettings.colorSpace.collectAsState()
            colorSpace(colorSpace)
        }
        val preferQualityOverSpeed by appSettings.preferQualityOverSpeed.collectAsState()
        @Suppress("DEPRECATION")
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed)

        val videoFramePercent by appSettings.videoFramePercent.collectAsState()
        videoFramePercent(videoFramePercent)

        val preferVideoCover by appSettings.preferVideoCover.collectAsState()
        preferVideoCover(preferVideoCover)
    }
}