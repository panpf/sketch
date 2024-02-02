package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun rememberIconImageOutlinePainter(): Painter {
    return painterResource("ic_image_outline.xml")
}

@Composable
actual fun rememberIconErrorBaselinePainter(): Painter {
    return painterResource("ic_error_baseline.xml")
}

@Composable
actual fun rememberIconSignalCellularPainter(): Painter {
    return painterResource("ic_signal_cellular.xml")
}