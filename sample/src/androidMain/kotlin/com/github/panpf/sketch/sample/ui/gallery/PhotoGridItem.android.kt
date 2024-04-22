package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.state.StateImage

@Composable
actual fun rememberAnimatedPlaceholderStateImage(context: PlatformContext): StateImage? {
    // AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above android api 29
    return rememberIconAnimatablePainterStateImage(
        icon = R.drawable.ic_placeholder_eclipse_animated,
        background = R.color.placeholder_bg,
    )
}