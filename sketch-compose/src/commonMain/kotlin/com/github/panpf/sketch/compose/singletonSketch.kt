package com.github.panpf.sketch.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch

/**
 * Alias for [SingletonSketch.setSafe] that's optimized for calling from Compose.
 */
@Composable
@ReadOnlyComposable
fun setSingletonImageLoaderFactory(factory: (context: PlatformContext) -> Sketch) {
    // This can't be invoked inside a LaunchedEffect as it needs to run immediately before
    // SingletonSketch.get is called by any composables.
    SingletonSketch.setSafe(factory)
}