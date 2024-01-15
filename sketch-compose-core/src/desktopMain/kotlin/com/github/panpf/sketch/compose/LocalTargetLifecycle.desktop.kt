package com.github.panpf.sketch.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.GlobalTargetLifecycle
import com.github.panpf.sketch.target.TargetLifecycle

//actual val LocalTargetLifecycle: ProvidableCompositionLocal<TargetLifecycle>


@Composable
internal actual fun resolveTargetLifecycle(): TargetLifecycle? {
    return null
}