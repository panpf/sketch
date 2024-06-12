package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.github.panpf.sketch.lifecycle.PlatformLifecycle
import com.github.panpf.sketch.lifecycle.RealPlatformLifecycle

@Composable
internal actual fun resolvePlatformLifecycle(): PlatformLifecycle? {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        RealPlatformLifecycle(lifecycle)
    }
}