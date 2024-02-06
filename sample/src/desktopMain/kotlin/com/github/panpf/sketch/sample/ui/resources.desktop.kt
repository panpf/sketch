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

@Composable
actual fun rememberIconLayoutGridPainter(): Painter {
    return painterResource("ic_layout_grid.xml")
}

@Composable
actual fun rememberIconLayoutGridStaggeredPainter(): Painter {
    return painterResource("ic_layout_grid_staggered.xml")
}

@Composable
actual fun rememberIconPlayPainter(): Painter {
    return painterResource("ic_play.xml")
}

@Composable
actual fun rememberIconPausePainter(): Painter {
    return painterResource("ic_pause.xml")
}

@Composable
actual fun rememberIconSettingsPainter(): Painter {
    return painterResource("ic_settings.xml")
}