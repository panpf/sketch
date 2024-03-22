package com.github.panpf.sketch.compose

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.target.TargetLifecycle

@Composable
internal expect fun resolveTargetLifecycle(): TargetLifecycle?
