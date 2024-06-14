package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun equalityPainterResource(resource: DrawableResource): PainterEqualizer {
    val painter = painterResource(resource)
    return PainterEqualizer(wrapped = painter, equalityKey = resource)
}

@Composable
fun rememberEqualityPainterResource(resource: DrawableResource): PainterEqualizer {
    val painter = painterResource(resource)
    return remember(resource) {
        PainterEqualizer(wrapped = painter, equalityKey = resource)
    }
}