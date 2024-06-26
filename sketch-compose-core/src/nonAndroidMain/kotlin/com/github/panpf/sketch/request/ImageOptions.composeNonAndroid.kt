package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import com.github.panpf.sketch.util.toSketchSize


/**
 * Set the resize size
 */
@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun ImageOptions.Builder.sizeWithWindow(): ImageOptions.Builder =
    apply {
        val size = LocalWindowInfo.current.containerSize.toSketchSize()
        size(size.width, size.height)
    }