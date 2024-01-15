package com.github.panpf.sketch.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import com.github.panpf.sketch.PlatformContext

expect val LocalPlatformContext: ProvidableCompositionLocal<PlatformContext>
