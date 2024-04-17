package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.PlatformContext

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): Painter? {
    // Animated svg is not yet supported on non-Android platforms
    return null
}