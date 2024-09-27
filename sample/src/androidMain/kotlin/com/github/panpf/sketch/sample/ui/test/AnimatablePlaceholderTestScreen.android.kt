package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.drawable.getEquitableDrawableCompat
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.state.asEquitablePainter

@Composable
actual fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): EquitablePainter? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return remember {
        val resId = R.drawable.ic_placeholder_eclipse_animated
        context.getEquitableDrawableCompat(resId).asEquitablePainter()
    }
}