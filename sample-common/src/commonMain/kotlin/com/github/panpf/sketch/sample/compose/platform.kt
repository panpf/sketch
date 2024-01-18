package com.github.panpf.sketch.sample.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun icGamepadPainter(): Painter

@Composable
expect fun icInfoPainter(): Painter

@Composable
expect fun icMoreVertPainter(): Painter

@Composable
expect fun icRotateRightPainter(): Painter

@Composable
expect fun icZoomInPainter(): Painter

@Composable
expect fun icZoomOutPainter(): Painter

@Composable
expect fun icExpandMorePainter(): Painter