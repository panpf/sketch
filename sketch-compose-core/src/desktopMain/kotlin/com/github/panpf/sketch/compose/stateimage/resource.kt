package com.github.panpf.sketch.compose.stateimage

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.painterResource


@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun resourcePainterStateImage(
    resourcePath: String,
    loader: ResourceLoader = ResourceLoader.Default
): PainterStateImage = PainterStateImage(painterResource(resourcePath, loader))