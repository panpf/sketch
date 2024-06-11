package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.github.panpf.sketch.target.RealTargetLifecycle
import com.github.panpf.sketch.target.TargetLifecycle

@Composable
actual fun resolveTargetLifecycle(): TargetLifecycle? {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        RealTargetLifecycle(lifecycle)
    }
}