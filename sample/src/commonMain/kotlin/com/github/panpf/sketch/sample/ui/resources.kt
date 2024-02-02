package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter


@Composable
expect fun rememberIconImageOutlinePainter(): Painter

@Composable
expect fun rememberIconErrorBaselinePainter(): Painter

@Composable
expect fun rememberIconSignalCellularPainter(): Painter