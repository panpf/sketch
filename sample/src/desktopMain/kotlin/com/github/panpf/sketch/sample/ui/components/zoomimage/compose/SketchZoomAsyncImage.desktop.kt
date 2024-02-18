package com.github.panpf.zoomimage

import com.github.panpf.sketch.Sketch
import com.github.panpf.zoomimage.subsampling.TileBitmapCache

actual fun createTileBitmapCache(
    sketch: Sketch,
    caller: String
): TileBitmapCache? = null  // TODO