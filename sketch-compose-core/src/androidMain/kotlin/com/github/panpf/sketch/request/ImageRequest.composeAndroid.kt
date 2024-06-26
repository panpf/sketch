package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.util.screenSize


/**
 * Set the resize size
 */
@Composable
actual fun ImageRequest.Builder.sizeWithWindow(): ImageRequest.Builder =
    apply {
        val screenSize = LocalContext.current.screenSize()
        size(screenSize)
    }