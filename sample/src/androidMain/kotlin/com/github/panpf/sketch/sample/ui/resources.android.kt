package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.panpf.sketch.sample.R

@Composable
actual fun rememberIconImageOutlinePainter(): Painter {
    return painterResource(R.drawable.ic_image_outline)
}

@Composable
actual fun rememberIconErrorBaselinePainter(): Painter {
    return painterResource(R.drawable.ic_error_baseline)
}

@Composable
actual fun rememberIconSignalCellularPainter(): Painter {
    return painterResource(R.drawable.ic_signal_cellular)
}