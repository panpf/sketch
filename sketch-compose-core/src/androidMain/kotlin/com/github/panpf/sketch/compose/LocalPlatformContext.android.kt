package com.github.panpf.sketch.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.PlatformContext

actual val LocalPlatformContext: ProvidableCompositionLocal<PlatformContext> = LocalContext
