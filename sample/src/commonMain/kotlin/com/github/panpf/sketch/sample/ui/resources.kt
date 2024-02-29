package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.panpf.sketch.PlatformContext


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

@Composable
expect fun rememberIconInfoBaseLinePainter(): Painter

@Composable
expect fun rememberIconRotateRightPainter(): Painter

@Composable
expect fun rememberIconSavePainter(): Painter

@Composable
expect fun rememberIconSharePainter(): Painter

@Composable
expect fun rememberIconZoomInPainter(): Painter

@Composable
expect fun rememberIconZoomOutPainter(): Painter

@Composable
expect fun rememberIconImage2BaselinePainter(): Painter

@Composable
expect fun rememberIconImage2OutlinePainter(): Painter

@Composable
expect fun rememberIconExpandMorePainter(): Painter

@Composable
expect fun rememberIconDebugPainter(): Painter

@Composable
expect fun rememberIconGiphyPainter(): Painter

@Composable
expect fun rememberIconPexelsPainter(): Painter

@Composable
expect fun rememberIconPhonePainter(): Painter

@Composable
expect fun rememberIconPlaceholderEclipseAnimatedPainter(context: PlatformContext): Painter?