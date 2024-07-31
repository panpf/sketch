package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KotlinResourceDataSource
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (resourceName != other.resourceName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + resourceName.hashCode()
        return result
    }

    override fun toString(): String = "ResourceDataSource('$resourceName')"
}