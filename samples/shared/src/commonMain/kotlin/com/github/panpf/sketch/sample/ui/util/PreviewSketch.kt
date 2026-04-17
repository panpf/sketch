package com.github.panpf.sketch.sample.ui.util

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.Sketch
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet


private val previewSketch = atomic<Sketch?>(null)

@Composable
fun getPreviewSketch(): Sketch {
    val context = LocalPlatformContext.current
    return previewSketch.updateAndGet {
        it ?: Sketch.Builder(context).build()
    }!!
}