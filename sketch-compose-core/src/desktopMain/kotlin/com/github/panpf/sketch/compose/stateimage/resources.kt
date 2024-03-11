package com.github.panpf.sketch.compose.stateimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.painterResource


@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun rememberResourcePainterStateImage(
    resourcePath: String,
    loader: ResourceLoader = ResourceLoader.Default
): PainterStateImage {
    val painter = painterResource(resourcePath, loader)
    return remember(resourcePath, loader) {
        PainterStateImage(painter)
    }
}