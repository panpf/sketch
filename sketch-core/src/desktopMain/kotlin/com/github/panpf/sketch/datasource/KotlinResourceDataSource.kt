package com.github.panpf.sketch.datasource

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ClassLoaderResourceLoader
import okio.Path
import okio.Source
import okio.source
import java.io.IOException

class KotlinResourceDataSource(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val resourceName: String,
) : DataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    @WorkerThread
    @Throws(IOException::class)
    override fun openSourceOrNull(): Source =
        ClassLoaderResourceLoader.Default.load(resourceName).source()

    @WorkerThread
    @Throws(IOException::class)
    override fun getFileOrNull(): Path? = getDataSourceCacheFile(sketch, request, this)

    override fun toString(): String = "ResourceDataSource($resourceName)"
}