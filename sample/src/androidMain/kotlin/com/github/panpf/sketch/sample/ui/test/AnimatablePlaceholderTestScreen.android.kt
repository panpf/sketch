package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.state.PainterEqualWrapper
import com.github.panpf.sketch.state.asPainterEqualWrapper
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.state.getEqualWrapperDrawableCompat

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): PainterEqualWrapper? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return remember {
        val resId = R.drawable.ic_placeholder_eclipse_animated
        context.getEqualWrapperDrawableCompat(resId).asPainterEqualWrapper()
    }
}