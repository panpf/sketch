package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import okio.Path
import okio.Path.Companion.toPath
import okio.Source
import platform.Foundation.NSBundle

class KotlinResourceDataSource(
    override val sketch: Sketch,
    override val request: ImageRequest,
    val resourcePath: String,
) : DataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    override fun openSourceOrNull(): Source {
        val appResourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        val filePath = appResourcePath.resolve("compose-resources").resolve(resourcePath)
        return sketch.fileSystem.source(filePath)
    }

    override fun getFileOrNull(): Path {
        val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        return resourcePath.resolve("compose-resources").resolve(this.resourcePath)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as KotlinResourceDataSource
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (resourcePath != other.resourcePath) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + resourcePath.hashCode()
        return result
    }

    override fun toString(): String = "KotlinResourceDataSource('$resourcePath')"
}