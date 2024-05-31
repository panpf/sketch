package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.target.TargetLifecycle

@Composable
internal actual fun resolveTargetLifecycle(): TargetLifecycle? = null   // TODO Android official cross-platform Lifecycle. 1.6.10 版本才支持