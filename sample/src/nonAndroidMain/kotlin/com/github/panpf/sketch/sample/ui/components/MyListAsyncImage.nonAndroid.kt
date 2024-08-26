package com.github.panpf.sketch.sample.ui.components

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.state.StateImage

@Composable
actual fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage? {
    // Animated svg is not yet supported on non-Android platforms
    return null
}