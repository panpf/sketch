package com.github.panpf.sketch.sample.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.panpf.zoomimage.sample.common.R

@Composable
actual fun icGamepadPainter(): Painter = painterResource(R.drawable.ic_gamepad)

@Composable
actual fun icInfoPainter(): Painter = painterResource(R.drawable.ic_info)

@Composable
actual fun icMoreVertPainter(): Painter = painterResource(R.drawable.ic_more_vert)

@Composable
actual fun icRotateRightPainter(): Painter = painterResource(R.drawable.ic_rotate_right)

@Composable
actual fun icZoomInPainter(): Painter = painterResource(R.drawable.ic_zoom_in)

@Composable
actual fun icZoomOutPainter(): Painter = painterResource(R.drawable.ic_zoom_out)

@Composable
actual fun icExpandMorePainter(): Painter = painterResource(R.drawable.ic_expand_more)