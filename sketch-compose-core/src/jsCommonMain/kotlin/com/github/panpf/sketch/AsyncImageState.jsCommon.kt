package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.lifecycle.PlatformLifecycle

// TODO Waiting for androidx lifecycle to support js
@Composable
internal actual fun resolvePlatformLifecycle(): PlatformLifecycle? = null
