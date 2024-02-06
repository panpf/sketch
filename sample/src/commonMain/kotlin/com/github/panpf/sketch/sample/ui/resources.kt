package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource


@Composable
expect fun rememberIconImageOutlinePainter(): Painter

@Composable
expect fun rememberIconErrorBaselinePainter(): Painter

@Composable
expect fun rememberIconSignalCellularPainter(): Painter

@Composable
expect fun rememberIconLayoutGridPainter(): Painter

@Composable
expect fun rememberIconLayoutGridStaggeredPainter(): Painter

@Composable
expect fun rememberIconPlayPainter(): Painter

@Composable
expect fun rememberIconPausePainter(): Painter

@Composable
expect fun rememberIconSettingsPainter(): Painter