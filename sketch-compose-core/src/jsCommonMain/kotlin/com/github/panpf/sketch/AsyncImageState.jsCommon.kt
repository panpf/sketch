package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.target.TargetLifecycle

@Composable
actual fun resolveTargetLifecycle(): TargetLifecycle? = null    // TODO Waiting for androidx lifecycle to support js