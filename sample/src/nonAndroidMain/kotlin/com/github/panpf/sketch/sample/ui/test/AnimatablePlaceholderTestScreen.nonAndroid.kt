package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.compose.state.PainterEqualWrapper

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): PainterEqualWrapper? {
    // Animated svg is not yet supported on non-Android platforms
    return null
}