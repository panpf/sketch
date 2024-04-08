package com.github.panpf.sketch.datasource

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.request.ImageRequest
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

    override fun toString(): String = "KotlinResourceDataSource($resourceName)"
}