package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import okio.IOException
import okio.Path
import okio.Source

class FakeDataSource(override val dataFrom: DataFrom = LOCAL) : DataSource {

    override val key: String = "FakeDataSource"

    override fun openSource(): Source {
        throw IOException("Not implemented")
    }

    override fun getFile(sketch: Sketch): Path {
        throw IOException("Not implemented")
    }
}