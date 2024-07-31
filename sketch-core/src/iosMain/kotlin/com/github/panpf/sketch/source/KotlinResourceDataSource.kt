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
    val resourceName: String,
) : DataSource {

    override val dataFrom: DataFrom
        get() = LOCAL

    override fun openSourceOrNull(): Source {
        val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        val filePath = resourcePath.resolve("compose-resources").resolve(resourceName)
        return sketch.fileSystem.source(filePath)
    }

    override fun getFileOrNull(): Path {
        val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
        return resourcePath.resolve("compose-resources").resolve(resourceName)
    }

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

    override fun toString(): String = "KotlinResourceDataSource('$resourceName')"
}