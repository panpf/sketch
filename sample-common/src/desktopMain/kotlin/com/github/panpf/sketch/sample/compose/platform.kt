package com.github.panpf.sketch.sample.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun icGamepadPainter(): Painter = painterResource("ic_gamepad.xml")

@Composable
actual fun icInfoPainter(): Painter = painterResource("ic_info.xml")

@Composable
actual fun icMoreVertPainter(): Painter = painterResource("ic_more_vert.xml")

@Composable
actual fun icRotateRightPainter(): Painter = painterResource("ic_rotate_right.xml")

@Composable
actual fun icZoomInPainter(): Painter = painterResource("ic_zoom_in.xml")

@Composable
actual fun icZoomOutPainter(): Painter = painterResource("ic_zoom_out.xml")

@Composable
actual fun icExpandMorePainter(): Painter = painterResource("ic_expand_more.xml")