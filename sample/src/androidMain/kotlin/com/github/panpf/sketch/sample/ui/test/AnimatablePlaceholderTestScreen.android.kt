package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.compose.painter.asPainter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.ui.util.getDrawableCompat

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): Painter? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return remember {
        context.getDrawableCompat(R.drawable.ic_placeholder_eclipse_animated).asPainter()
    }
}