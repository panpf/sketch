package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import okio.Path

@WorkerThread
internal actual fun getDataSourceCacheFile(
    sketch: Sketch,
    request: ImageRequest,
    dataSource: DataSource,
): Path? = null