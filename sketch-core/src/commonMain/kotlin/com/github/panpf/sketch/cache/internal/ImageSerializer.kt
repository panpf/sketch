package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import okio.BufferedSink


expect fun createImageSerializer(): ImageSerializer?

interface ImageSerializer {

    fun supportImage(image: Image): Boolean

    @WorkerThread
    fun compress(image: Image, sink: BufferedSink)

    @WorkerThread
    fun decode(requestContext: RequestContext, imageInfo: ImageInfo, dataSource: DataSource): Image
}