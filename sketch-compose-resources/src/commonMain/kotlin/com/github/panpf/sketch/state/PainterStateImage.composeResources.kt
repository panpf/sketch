package com.github.panpf.sketch.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.painter.rememberEqualityPainterResource
import org.jetbrains.compose.resources.DrawableResource


@Composable
fun rememberPainterStateImage(resource: DrawableResource): PainterStateImage {
    val painter = rememberEqualityPainterResource(resource)
    return remember(resource) { PainterStateImage(painter) }
}