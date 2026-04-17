@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.datasource

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.source.cacheFile
import okio.Path
import okio.Source
import okio.source
import java.io.IOException
import java.io.InputStream

@Deprecated(
    message = "Use com.github.panpf.sketch.source.DataFrom instead",
    replaceWith = ReplaceWith("DataFrom", "com.github.panpf.sketch.source.DataFrom")
)
typealias DataFrom = DataFrom

@Deprecated(
    message = "Use com.github.panpf.sketch.source.AssetDataSource instead",
    replaceWith = ReplaceWith("AssetDataSource", "com.github.panpf.sketch.source.AssetDataSource")
)
typealias AssetDataSource = AssetDataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.ByteArrayDataSource instead",
    replaceWith = ReplaceWith(
        "ByteArrayDataSource",
        "com.github.panpf.sketch.source.ByteArrayDataSource"
    )
)
typealias ByteArrayDataSource = ByteArrayDataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.ContentDataSource instead",
    replaceWith = ReplaceWith(
        "ContentDataSource",
        "com.github.panpf.sketch.source.ContentDataSource"
    )
)
typealias ContentDataSource = ContentDataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.DataSource instead",
    replaceWith = ReplaceWith("DataSource", "com.github.panpf.sketch.source.DataSource")
)
typealias DataSource = DataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.DrawableDataSource instead",
    replaceWith = ReplaceWith(
        "DrawableDataSource",
        "com.github.panpf.sketch.source.DrawableDataSource"
    )
)
typealias DrawableDataSource = DrawableDataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.FileDataSource instead",
    replaceWith = ReplaceWith("FileDataSource", "com.github.panpf.sketch.source.FileDataSource")
)
typealias FileDataSource = FileDataSource

@Deprecated(
    message = "Use com.github.panpf.sketch.source.ResourceDataSource instead",
    replaceWith = ReplaceWith(
        "ResourceDataSource",
        "com.github.panpf.sketch.source.ResourceDataSource"
    )
)
typealias ResourceDataSource = ResourceDataSource

@Deprecated(
    message = "Use DataSource instead",
    replaceWith = ReplaceWith("DataSource", "com.github.panpf.sketch.source.DataSource")
)
interface BasedStreamDataSource : DataSource {

    override fun openSource(): Source {
        return newInputStream().source()
    }

    override fun getFile(sketch: Sketch): Path {
        return cacheFile(sketch)
    }

    @WorkerThread
    @Throws(IOException::class)
    fun newInputStream(): InputStream
}