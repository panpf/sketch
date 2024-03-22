package com.github.panpf.sketch.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.github.panpf.sketch.target.AndroidTargetLifecycle
import com.github.panpf.sketch.target.TargetLifecycle

@Composable
internal actual fun resolveTargetLifecycle(): TargetLifecycle? {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        AndroidTargetLifecycle(lifecycle)
    }
}