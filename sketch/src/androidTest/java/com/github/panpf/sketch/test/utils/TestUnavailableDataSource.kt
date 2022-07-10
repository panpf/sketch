package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.UnavailableDataSource
import com.github.panpf.sketch.request.ImageRequest
import java.io.InputStream

class TestUnavailableDataSource(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val dataFrom: DataFrom,
) : UnavailableDataSource {
    override fun length(): Long =
        throw UnsupportedOperationException("TestUnavailableDataSource cannot be used")

    override fun newInputStream(): InputStream =
        throw UnsupportedOperationException("TestUnavailableDataSource cannot be used")

    override fun toString(): String = "TestUnavailableDataSource"
}