package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.util.screenSize


/**
 * Set the resize size
 */
@Composable
actual fun ImageOptions.Builder.sizeWithWindow(): ImageOptions.Builder =
    apply {
        val screenSize = LocalContext.current.screenSize()
        size(screenSize)
    }