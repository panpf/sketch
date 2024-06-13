package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.state.asPainterEqualizer
import com.github.panpf.sketch.painter.PainterEqualizer
import com.github.panpf.sketch.drawable.getEqualityDrawableCompat

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): PainterEqualizer? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return remember {
        val resId = R.drawable.ic_placeholder_eclipse_animated
        context.getEqualityDrawableCompat(resId).asPainterEqualizer()
    }
}