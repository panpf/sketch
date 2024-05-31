package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.SkiaBitmapToComposeBitmapRequestInterceptor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getWindowContainerSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}

internal actual fun ImageRequest.Builder.platformConfig() {
    mergeComponents {
        addRequestInterceptor(SkiaBitmapToComposeBitmapRequestInterceptor())
    }
}