package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.sample.AppSettings


@Composable
expect inline fun composablePlatformAsyncImageSettings(appSettings: AppSettings): ImageOptions